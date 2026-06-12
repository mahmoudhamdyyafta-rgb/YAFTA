package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.data.api.FirebaseManager
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DesignProject
import com.example.ui.viewmodel.DesignViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedProjectsScreen(
    viewModel: DesignViewModel,
    onNavigateBack: () -> Unit
) {
    val projects by viewModel.savedProjects.collectAsStateWithLifecycle()
    var showSyncDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مشاريعي المحفوظة 🗃️", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    IconButton(onClick = { showSyncDialog = true }) {
                        Icon(Icons.Default.Cloud, contentDescription = "مزامنة سحابية", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (projects.isEmpty()) {
                // Empty state view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "لا توجد مسودات لافتات بعد",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "ابدأ بابتكار فكرتك بالذكاء الاصطناعي في الاستوديو وصمم يافطة أولى كاملة، ثم احفظها لحفظها ومراجعتها هنا في أي وقت!",
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("الذهاب إلى الستوديو الآن 🚀", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Display grid list of saved projects
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(projects) { project ->
                        ProjectGridCard(
                            project = project,
                            onLoad = {
                                viewModel.loadProject(project)
                                onNavigateBack()
                            },
                            onDelete = {
                                viewModel.deleteProject(project)
                            }
                        )
                    }
                }
            }
        }

        if (showSyncDialog) {
            FirebaseSyncDialog(
                viewModel = viewModel,
                onDismiss = { showSyncDialog = false }
            )
        }
    }
}

@Composable
fun ProjectGridCard(
    project: DesignProject,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    // Determine dynamic background thumbnail color
    val thumbnailColor = try {
        Color(android.graphics.Color.parseColor(project.thumbnailColorHex))
    } catch (e: Exception) {
        Color(0xFF1E293B)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable { onLoad() }
    ) {
        Column {
            // Stylized Poster Thumbnail Block
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(thumbnailColor)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = project.aspectRatio,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Project specifications segment
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = project.title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                
                val dateString = DateFormat.format("yyyy-MM-dd", Date(project.updatedAt)).toString()
                Text(
                    text = "تعديل: $dateString",
                    color = Color.LightGray,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLoad,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "فتح للتعديل",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف المشروع",
                            tint = Color.Red.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirebaseSyncDialog(
    viewModel: DesignViewModel,
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val isInitialized by FirebaseManager.isInitialized.collectAsStateWithLifecycle()
    val initStatus by FirebaseManager.initStatusMessage.collectAsStateWithLifecycle()
    val syncStatus by FirebaseManager.syncStatus.collectAsStateWithLifecycle()
    val currentUserEmail by FirebaseManager.currentUserEmail.collectAsStateWithLifecycle()
    val projects by viewModel.savedProjects.collectAsStateWithLifecycle()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    tint = if (isInitialized) Color(0xFF4CAF50) else Color(0xFFFF9800),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = "سحابة يافطة الذكية ☁️",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Connection Info Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isInitialized) Color(0xFF1B3B2B) else Color(0xFF2C2418)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "حالة الاتصال بالسحاب:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isInitialized) Color(0xFFA5D6A7) else Color(0xFFFFCC80)
                        )
                        Text(
                            text = initStatus,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }

                // Authentication Status Segment
                if (currentUserEmail != null) {
                    // Logged in
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "الحساب النشط حالياً:",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = currentUserEmail ?: "",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Button(
                                onClick = {
                                    FirebaseManager.signOut()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text("خروج 👤", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                } else {
                    // Not logged in -> Sign In fields
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "سجل دخولك لحفظ لافتاتك وتصميماتك بالسحاب:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("البريد الإلكتروني", fontSize = 12.sp) },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("كلمة المرور", fontSize = 12.sp) },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Lock, null, modifier = Modifier.size(18.dp)) },
                            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                            trailingIcon = {
                                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(image, null, modifier = Modifier.size(18.dp))
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (email.isNotBlank() && password.length >= 6) {
                                        coroutineScope.launch {
                                            FirebaseManager.signInWithEmail(email.trim(), password.trim())
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = email.isNotBlank() && password.length >= 6
                            ) {
                                Text("دخول / تسجيل 🔑", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        FirebaseManager.signInAnonymously()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("دخول كضيف 👤", fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Cloud Sync Controls Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "عمليات المزامنة (Firestore Sync):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Backup Button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        FirebaseManager.backupProjectsToCloud(projects)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Cloud, null, modifier = Modifier.size(16.dp))
                                    Text("رفع للسحاب 📤", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Restore Button
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val restored = FirebaseManager.restoreProjectsFromCloud()
                                        if (restored != null) {
                                            viewModel.importCloudProjects(restored)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0288D1)),
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Sync, null, modifier = Modifier.size(16.dp))
                                    Text("تنزيل السحاب 📥", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Live Sync Status Logger
                when (val status = syncStatus) {
                    is FirebaseManager.SyncStatus.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Text("جاري معالجة طلب المزامنة السحابي...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    is FirebaseManager.SyncStatus.Success -> {
                        Text(
                            text = "🎉 ${status.message}",
                            fontSize = 12.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    is FirebaseManager.SyncStatus.Error -> {
                        Text(
                            text = "❌ ${status.error}",
                            fontSize = 11.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {}
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", fontWeight = FontWeight.Bold)
            }
        }
    )
}
