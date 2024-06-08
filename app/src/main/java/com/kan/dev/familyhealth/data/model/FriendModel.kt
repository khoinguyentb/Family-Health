package com.kan.dev.familyhealth.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity (tableName = "friend")
data class FriendModel(
    var code: String,
    var avt: Int,
    var battery: Int,
    var name: String,
    var weight: Float,
    var height: Float,
    var dateOfBirth : String,
    var nickname: String,
    var gender: String,
    var phoneNumber: String,
    var checkCm: Boolean,
    var checkSt: Boolean,
    var checkKg: Boolean,
    var checkLb: Boolean,
    var latLng: String,
    var visible: Boolean,
    var isSos: Boolean,
    var statusSos: Boolean,
    var isTracking: Boolean,
    var lastActive: Long,
    var online: Boolean
) : Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
    constructor() : this("",0,0,"",0f,0f,"","","","",true,true,true,true,
        "",true,true,true,true,0L,true)
}