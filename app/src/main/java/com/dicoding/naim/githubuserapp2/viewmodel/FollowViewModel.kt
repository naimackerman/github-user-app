package com.dicoding.naim.githubuserapp2.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.naim.githubuserapp2.Event
import com.dicoding.naim.githubuserapp2.api.ApiConfig
import com.dicoding.naim.githubuserapp2.response.ItemsItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowViewModel : ViewModel() {

    private val _listUser = MutableLiveData<List<ItemsItem?>>()
    val listUser: LiveData<List<ItemsItem?>> = _listUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackText = MutableLiveData<Event<String>>()
    val snackText: LiveData<Event<String>> = _snackText

    private fun callBack(option: String) = object : Callback<List<ItemsItem>> {
        override fun onResponse(
            call: Call<List<ItemsItem>>,
            response: Response<List<ItemsItem>>
        ) {
            _isLoading.value = false
            if (response.isSuccessful) {
                _listUser.value = response.body()
            } else {
                if (option == "follower") _snackText.value = Event("ERROR: FOLLOWER")
                else _snackText.value = Event("ERROR: FOLLOWING")
                Log.e(TAG, "onFailure: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<List<ItemsItem>>, t: Throwable) {
            _isLoading.value = false
            if (option == "follower") _snackText.value = Event("ERROR: FOLLOWER")
            else _snackText.value = Event("ERROR: FOLLOWING")
            Log.e(TAG, "onFailure: ${t.message.toString()}")
        }
    }

    fun findUserFollower(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowersUser(username)
        client?.enqueue(callBack("follower"))
    }

    fun findUserFollowing(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getFollowingUser(username)
        client?.enqueue(callBack("following"))
    }

    companion object {
        private const val TAG = "FollowViewModel"
    }
}