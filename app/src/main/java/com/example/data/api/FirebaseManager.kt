package com.example.data.api

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.DesignProject
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private const val TAG = "FirebaseManager"

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized

    private val _initStatusMessage = MutableStateFlow("Uninitialized")
    val initStatusMessage: StateFlow<String> = _initStatusMessage

    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus

    private val _currentUserEmail = MutableStateFlow<String?>(null)
    val currentUserEmail: StateFlow<String?> = _currentUserEmail

    sealed class SyncStatus {
        object Idle : SyncStatus()
        object Loading : SyncStatus()
        data class Success(val message: String) : SyncStatus()
        data class Error(val error: String) : SyncStatus()
    }

    fun initialize(context: Context) {
        if (_isInitialized.value) return
        _initStatusMessage.value = "Initializing..."
        try {
            // 1. Try standard default initialization first (if google-services.json is present)
            FirebaseApp.initializeApp(context)
            // Force verify that standard default app initialization succeeded (will throw if options are missing/invalid)
            FirebaseApp.getInstance()
            
            _isInitialized.value = true
            _initStatusMessage.value = "Connected via google-services.json!"
            checkCurrentUser()
            Log.d(TAG, "Firebase initialized via default options.")
        } catch (e: Exception) {
            Log.w(TAG, "Default Firebase initialization failed: ${e.message}. Attempting dynamic config fallback...")
            // 2. Try programmatic options fallback using values injected from the Secrets Panel/BuildConfig
            try {
                val apiKey = BuildConfig.FIREBASE_API_KEY
                val appId = BuildConfig.FIREBASE_APP_ID
                val projectId = BuildConfig.FIREBASE_PROJECT_ID

                if (!apiKey.isNullOrBlank() && !appId.isNullOrBlank() && !projectId.isNullOrBlank()) {
                    val options = FirebaseOptions.Builder()
                        .setApiKey(apiKey)
                        .setApplicationId(appId)
                        .setProjectId(projectId)
                        .build()
                    FirebaseApp.initializeApp(context, options)
                    _isInitialized.value = true
                    _initStatusMessage.value = "Connected dynamically via BuildConfig!"
                    checkCurrentUser()
                    Log.d(TAG, "Firebase initialized dynamically via BuildConfig options.")
                } else {
                    _initStatusMessage.value = "Offline mode active. No Google Services file or developer API keys provided."
                    Log.i(TAG, "No Firebase credentials in BuildConfig. Firebase components remain in simulated offline mode.")
                }
            } catch (inner: Exception) {
                _initStatusMessage.value = "Configuration Error: ${inner.localizedMessage}"
                Log.e(TAG, "Dynamic Firebase initialization failed", inner)
            }
        }
    }

    private fun checkCurrentUser() {
        if (!_isInitialized.value) return
        try {
            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser
            if (user != null) {
                _currentUserEmail.value = if (user.isAnonymous) "Anonymous User" else user.email
            } else {
                _currentUserEmail.value = null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Auth user check failed", e)
        }
    }

    suspend fun signInAnonymously(): Boolean {
        if (!_isInitialized.value) {
            // Simulated offline flow
            _currentUserEmail.value = "Guest (Offline Mode)"
            return true
        }
        _syncStatus.value = SyncStatus.Loading
        return try {
            val auth = FirebaseAuth.getInstance()
            auth.signInAnonymously().await()
            _currentUserEmail.value = "Anonymous Cloud Active"
            _syncStatus.value = SyncStatus.Success("Signed in anonymously to Cloud Storage")
            true
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error("Auth failed: ${e.localizedMessage}")
            false
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Boolean {
        if (!_isInitialized.value) {
            // Simulated local mode login
            _currentUserEmail.value = email
            _syncStatus.value = SyncStatus.Success("Signed in to Local Profile (Offline Mode)")
            return true
        }
        _syncStatus.value = SyncStatus.Loading
        return try {
            val auth = FirebaseAuth.getInstance()
            try {
                auth.signInWithEmailAndPassword(email, password).await()
            } catch (authError: Exception) {
                // If login fails, seek to automatically register first to make a friction-free onboarding UI
                auth.createUserWithEmailAndPassword(email, password).await()
            }
            _currentUserEmail.value = auth.currentUser?.email
            _syncStatus.value = SyncStatus.Success("Successfully Authenticated on Cloud!")
            true
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error("Email access failed: ${e.localizedMessage}")
            false
        }
    }

    fun signOut() {
        if (_isInitialized.value) {
            try {
                FirebaseAuth.getInstance().signOut()
            } catch (e: Exception) {
                Log.e(TAG, "Sign out error", e)
            }
        }
        _currentUserEmail.value = null
        _syncStatus.value = SyncStatus.Idle
    }

    // --- FIRESTORE BACKUP OF ENTIRE YAFTA PROJECTS LIST ---
    suspend fun backupProjectsToCloud(projects: List<DesignProject>): Boolean {
        if (!_isInitialized.value) {
            _syncStatus.value = SyncStatus.Error("Firebase is not configured. Configure developer keys or add google-services.json to sync.")
            return false
        }
        _syncStatus.value = SyncStatus.Loading
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: "anonymous_device_sync"
        
        return try {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(uid)
            
            // Map the designs into premium key-value pairs
            val mappedProjects = projects.map { project ->
                mapOf(
                    "id" to project.id,
                    "title" to project.title,
                    "width" to project.width,
                    "height" to project.height,
                    "aspectRatio" to project.aspectRatio,
                    "layersJson" to project.layersJson,
                    "updatedAt" to project.updatedAt,
                    "thumbnailColorHex" to project.thumbnailColorHex,
                    "promptUsed" to project.promptUsed
                )
            }

            val data = mapOf(
                "lastSyncTime" to System.currentTimeMillis(),
                "device" to "Android Emulator Studio",
                "designsCount" to projects.size,
                "designs" to mappedProjects
            )

            userDocRef.set(data, SetOptions.merge()).await()
            _syncStatus.value = SyncStatus.Success("Exported ${projects.size} designs to Firestore cloud backup!")
            true
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error("Firestore write failed: ${e.localizedMessage}")
            false
        }
    }

    // --- FIRESTORE DOWNLOAD / RESTORE FROM CLOUD ---
    suspend fun restoreProjectsFromCloud(): List<DesignProject>? {
        if (!_isInitialized.value) {
            _syncStatus.value = SyncStatus.Error("Firebase not initialized.")
            return null
        }
        _syncStatus.value = SyncStatus.Loading
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid ?: "anonymous_device_sync"

        return try {
            val db = FirebaseFirestore.getInstance()
            val docSnapshot = db.collection("users").document(uid).get().await()
            if (docSnapshot.exists()) {
                val designsListRaw = docSnapshot.get("designs") as? List<Map<String, Any>>
                if (designsListRaw != null) {
                    val projectsList = designsListRaw.map { map ->
                        DesignProject(
                            id = (map["id"] as? Long)?.toInt() ?: 0,
                            title = map["title"] as? String ?: "Cloud Restored Template",
                            width = (map["width"] as? Long)?.toInt() ?: 1000,
                            height = (map["height"] as? Long)?.toInt() ?: 1000,
                            aspectRatio = map["aspectRatio"] as? String ?: "1:1",
                            layersJson = map["layersJson"] as? String ?: "[]",
                            updatedAt = map["updatedAt"] as? Long ?: System.currentTimeMillis(),
                            thumbnailColorHex = map["thumbnailColorHex"] as? String ?: "#2D3748",
                            promptUsed = map["promptUsed"] as? String
                        )
                    }
                    _syncStatus.value = SyncStatus.Success("Restored ${projectsList.size} designs from Firestore successfully!")
                    projectsList
                } else {
                    _syncStatus.value = SyncStatus.Success("No backup found in cloud storage.")
                    emptyList()
                }
            } else {
                _syncStatus.value = SyncStatus.Success("No cloud database records found for this user.")
                emptyList()
            }
        } catch (e: Exception) {
            _syncStatus.value = SyncStatus.Error("Firestore read failed: ${e.localizedMessage}")
            null
        }
    }
}
