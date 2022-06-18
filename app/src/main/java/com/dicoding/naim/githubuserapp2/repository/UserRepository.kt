package com.dicoding.naim.githubuserapp2.repository

import android.app.Application
import com.dicoding.naim.githubuserapp2.database.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository private constructor(application: Application) {

    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun getAllFavUsers() = mUserDao.getAllFavUsers()

    fun insert(user: User) {
        executorService.execute { mUserDao.insert(user) }
    }

    fun delete(user: User) {
        executorService.execute { mUserDao.delete(user) }
    }

    fun isFavUser(username: String) = mUserDao.isFavUser(username)

    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null

        @JvmStatic
        fun getInstance(application: Application) =
            INSTANCE ?: synchronized(UserRepository::class.java) {
                UserRepository(application)
            }.also { INSTANCE = it }
    }
}