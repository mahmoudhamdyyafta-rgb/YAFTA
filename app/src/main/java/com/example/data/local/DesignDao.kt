package com.example.data.local

import androidx.room.*
import com.example.data.model.DesignProject
import kotlinx.coroutines.flow.Flow

@Dao
interface DesignDao {
    @Query("SELECT * FROM design_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<DesignProject>>

    @Query("SELECT * FROM design_projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: Int): DesignProject?

    @Query("SELECT * FROM design_projects WHERE id = :id LIMIT 1")
    fun observeProjectById(id: Int): Flow<DesignProject?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: DesignProject): Long

    @Query("DELETE FROM design_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Int)

    @Update
    suspend fun updateProject(project: DesignProject)
}
