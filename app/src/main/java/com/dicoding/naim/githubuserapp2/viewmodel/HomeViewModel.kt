package com.dicoding.naim.githubuserapp2.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.naim.githubuserapp2.Event
import com.dicoding.naim.githubuserapp2.SettingPreferences
import com.dicoding.naim.githubuserapp2.api.ApiConfig
import com.dicoding.naim.githubuserapp2.response.ItemsItem
import com.dicoding.naim.githubuserapp2.response.UserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(private val pref: SettingPreferences) : ViewModel() {
    private val _listUser = MutableLiveData<List<ItemsItem?>?>()
    val listUser: LiveData<List<ItemsItem?>?> = _listUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackText = MutableLiveData<Event<String>>()
    val snackText: LiveData<Event<String>> = _snackText

    init {
        findUser("\"\"")
    }

    fun findUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getUser(username)
        client?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _listUser.value = response.body()?.items
                } else {
                    _snackText.value = Event("ERROR: SEARCH USER")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _isLoading.value = false
                _snackText.value = Event("ERROR: SEARCH USER")
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val pref: SettingPreferences) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(pref) as T
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}