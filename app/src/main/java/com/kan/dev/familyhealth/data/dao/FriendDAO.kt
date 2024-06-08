package com.kan.dev.familyhealth.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kan.dev.familyhealth.data.model.BMI
import com.kan.dev.familyhealth.data.model.FriendModel
import kotlinx.coroutines.flow.Flow


@Dao
interface FriendDAO {
    @get:Query("SELECT * FROM friend")
    val all: Flow<List<FriendModel>>

    @Query("select * from friend where id = :id")
    fun getItem(id: Int): FriendModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friendModel: FriendModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(friendModel: FriendModel)

    @Delete
    fun delete(friendModel: FriendModel)

    @Query("DELETE FROM friend WHERE code = :code")
    fun deleteUserByCode(code: String)

    @Query("DELETE FROM friend")
    fun deleteAll()
}