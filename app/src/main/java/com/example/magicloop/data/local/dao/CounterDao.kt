package com.example.magicloop.data.local.dao

import androidx.room.*
import com.example.magicloop.data.local.entity.CounterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {
    @Query("SELECT * FROM counters WHERE projectId = :projectId")
    fun getCountersForProject(projectId: Long): Flow<List<CounterEntity>>

    @Insert
    suspend fun insert(counter: CounterEntity): Long

    @Update
    suspend fun update(counter: CounterEntity)

    @Query("UPDATE counters SET currentValue = currentValue + 1 WHERE id = :counterId")
    suspend fun increment(counterId: Long)

    @Query("UPDATE counters SET currentValue = MAX(currentValue - 1, 0) WHERE id = :counterId")
    suspend fun decrement(counterId: Long)

    @Delete
    suspend fun delete(counter: CounterEntity)
}