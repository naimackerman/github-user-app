package com.dicoding.naim.githubuserapp2.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.dicoding.naim.githubuserapp2.SettingPreferences
import com.dicoding.naim.githubuserapp2.repository.UserRepository
import kotlinx.coroutines.launch

class FavoriteUserViewModel(private val pref: SettingPreferences, application: Application) : ViewModel() {

    private val userRepository = UserRepository.getInstance(application)

    fun getFavorites() = userRepository.getAllFavUsers()

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val pref: SettingPreferences, private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavoriteUserViewModel(pref, application) as T
        }
    }

}