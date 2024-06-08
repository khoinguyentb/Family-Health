package com.kan.dev.familyhealth.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kan.dev.familyhealth.data.model.HealthyModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthyDao {
    @get:Query("SELECT * FROM healthy")
    val all: Flow<List<HealthyModel>>

    @Query("select * from healthy where id = :id")
    fun getItem(id: Int): Flow<HealthyModel>

    @Query("SELECT * FROM healthy WHERE date = :date")
    fun getItemByDate(date: String): Flow<HealthyModel>

    @Query("SELECT * FROM healthy WHERE date = :date")
    fun getRecordByDate(date: String): HealthyModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(healthyModel: HealthyModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(healthyModel: HealthyModel)

    @Delete
    fun delete(healthyModel: HealthyModel)
    @Query("DELETE FROM healthy")
    fun deleteAll()
}
