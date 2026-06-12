package com.example.data.repository

import com.example.data.local.DesignDao
import com.example.data.model.DesignLayer
import com.example.data.model.DesignProject
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class DesignRepository(private val designDao: DesignDao) {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val layersType = Types.newParameterizedType(List::class.java, DesignLayer::class.java)
    private val layersAdapter = moshi.adapter<List<DesignLayer>>(layersType)

    val allProjects: Flow<List<DesignProject>> = designDao.getAllProjects()

    suspend fun getProjectById(id: Int): DesignProject? {
        return designDao.getProjectById(id)
    }

    fun observeProjectById(id: Int): Flow<DesignProject?> {
        return designDao.observeProjectById(id)
    }

    suspend fun insertProject(project: DesignProject): Long {
        return designDao.insertProject(project)
    }

    suspend fun updateProject(project: DesignProject) {
        designDao.updateProject(project)
    }

    suspend fun deleteProjectById(id: Int) {
        designDao.deleteProjectById(id)
    }

    fun layersToJson(layers: List<DesignLayer>): String {
        return try {
            layersAdapter.toJson(layers) ?: "[]"
        } catch (e: Exception) {
            "[]"
        }
    }

    fun jsonToLayers(json: String?): List<DesignLayer> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            layersAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
