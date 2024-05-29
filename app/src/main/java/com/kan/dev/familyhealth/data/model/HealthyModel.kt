package com.kan.dev.familyhealth.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "healthy")
data class HealthyModel (
    var step : Int,
    var distance : Float,
    var moving : Float,
    val time : String
):Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}