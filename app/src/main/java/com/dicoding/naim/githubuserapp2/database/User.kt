package com.dicoding.naim.githubuserapp2.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favorite_user")
@Parcelize
data class User(

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "username")
    @PrimaryKey
    val username: String,

    @ColumnInfo(name = "avatar")
    val avatar: String,
) : Parcelable