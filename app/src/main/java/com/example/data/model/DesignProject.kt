package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "design_projects")
data class DesignProject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val width: Int = 1000,
    val height: Int = 1000,
    val aspectRatio: String = "1:1",
    val layersJson: String, // List<DesignLayer> JSON string
    val updatedAt: Long = System.currentTimeMillis(),
    val thumbnailColorHex: String = "#2D3748",
    val promptUsed: String? = null
)
