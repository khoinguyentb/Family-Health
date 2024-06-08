package com.kan.dev.familyhealth.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "healthy")
data class HealthyModel (
    var steps: Int,
    var distance : Float,
    var calories : Float,
    var date : String = ""
):Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
    constructor() : this(0,0f,0f,"")
}