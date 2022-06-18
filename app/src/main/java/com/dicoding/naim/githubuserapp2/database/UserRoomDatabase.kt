package com.dicoding.naim.githubuserapp2.database

import android.content.Context
import androidx.room.*
import androidx.room.Room.databaseBuilder

@Database(entities = [User::class], version = 1)
abstract class UserRoomDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        @Volatile
        private var INSTANCE: UserRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context) =
            INSTANCE ?: synchronized(UserRoomDatabase::class.java) {
                databaseBuilder(
                    context.applicationContext,
                    UserRoomDatabase::class.java, "favorite_user_database"
                )
                    .build()
            }.also { INSTANCE = it }
    }
}
