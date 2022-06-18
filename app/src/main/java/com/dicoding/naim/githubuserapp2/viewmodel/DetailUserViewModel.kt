package com.dicoding.naim.githubuserapp2.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.naim.githubuserapp2.Event
import com.dicoding.naim.githubuserapp2.SettingPreferences
import com.dicoding.naim.githubuserapp2.api.ApiConfig
import com.dicoding.naim.githubuserapp2.database.User
import com.dicoding.naim.githubuserapp2.repository.UserRepository
import com.dicoding.naim.githubuserapp2.response.DetailUserResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(private val pref: SettingPreferences, username: String, application: Application) : ViewModel() {
    private val _detailUser = MutableLiveData<DetailUserResponse>()
    val detailUser: LiveData<DetailUserResponse> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackText = MutableLiveData<Event<String>>()
    val snackText: LiveData<Event<String>> = _snackText

    private val userRepository = UserRepository.getInstance(application)

    val isFavUser = userRepository.isFavUser(username)

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun setFavUser(user: User, isFavUser: Boolean) {
        if (isFavUser) {
            userRepository.insert(user)
        } else {
            userRepository.delete(user)
        }
    }

    fun showDetailUser(username: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailUser(username)
        client?.enqueue(object : Callback<DetailUserResponse> {
            override fun onResponse(
                call: Call<DetailUserResponse>,
                response: Response<DetailUserResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _detailUser.value = response.body()
                } else {
                    _snackText.value = Event("ERROR: DETAIL USER")
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponse>, t: Throwable) {
                _isLoading.value = false
                _snackText.value = Event("ERROR: DETAIL USER")
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val pref: SettingPreferences, private val username: String, private val application: Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DetailUserViewModel(pref, username, application) as T
        }
    }

    companion object {
        private const val TAG = "DetailUserViewModel"
    }
}