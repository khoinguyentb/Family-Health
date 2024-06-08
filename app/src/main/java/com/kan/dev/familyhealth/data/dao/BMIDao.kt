package com.kan.dev.familyhealth.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kan.dev.familyhealth.data.model.BMI
import kotlinx.coroutines.flow.Flow


@Dao
interface BMIDao {
    @get:Query("SELECT * FROM bmi")
    val all: Flow<List<BMI>>
    @Query("select * from bmi where id = :month")
    fun getList(month: Int): LiveData<List<BMI>>

    @Query("select * from bmi where id = :id")
    fun getItem(id: Int): Flow<BMI>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bmi: BMI)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(bmi: BMI)

    @Delete
    fun delete(bmi: BMI)
    @Query("DELETE FROM bmi")
    fun deleteAll()
}

