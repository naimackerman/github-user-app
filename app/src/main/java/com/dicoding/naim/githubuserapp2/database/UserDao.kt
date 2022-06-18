package com.dicoding.naim.githubuserapp2.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM favorite_user ORDER BY username ASC")
    fun getAllFavUsers(): LiveData<List<User>>

    @Query("SELECT EXISTS(SELECT username FROM favorite_user WHERE username = :username)")
    fun isFavUser(username: String): LiveData<Boolean>
}