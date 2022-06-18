package com.dicoding.naim.githubuserapp2

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.naim.githubuserapp2.adapter.ListUserAdapter
import com.dicoding.naim.githubuserapp2.database.User
import com.dicoding.naim.githubuserapp2.databinding.ActivityFavoriteUserBinding
import com.dicoding.naim.githubuserapp2.viewmodel.FavoriteUserViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class FavoriteUserActivity : AppCompatActivity() {

    private var _binding: ActivityFavoriteUserBinding? = null
    private val binding get() = _binding

    private val favoriteUserViewModel by viewModels<FavoriteUserViewModel> {
        FavoriteUserViewModel.Factory(SettingPreferences.getInstance(dataStore), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityFavoriteUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Favorite User"

        binding?.rvUsers?.layoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(
                this,
                2
            ) else LinearLayoutManager(this)

        favoriteUserViewModel.apply {
            getFavorites().observe(this@FavoriteUserActivity) { users ->
                binding?.apply {
                    showLoading(true)
                    rvUsers.adapter = ListUserAdapter(ArrayList(users)).apply {
                        setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
                            override fun onItemClicked(data: User) {
                                showUserDetailData(data)
                            }
                        })
                    }
                    showLoading(false)
                }
            }
        }
    }

    private fun showUserDetailData(user: User) {
        val detailUserIntent =
            Intent(this@FavoriteUserActivity, DetailUserActivity::class.java).apply {
                putExtra(DetailUserActivity.EXTRA_USER, user)
            }
        startActivity(detailUserIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchView = menu.findItem(R.id.search)
        searchView.isVisible = false

        val favBtn = menu.findItem(R.id.favorite)
        favBtn.isVisible = false

        val switchTheme = menu.findItem(R.id.change_theme)

        favoriteUserViewModel.getThemeSettings().observe(this@FavoriteUserActivity) { darkMode ->
            if (darkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switchTheme.apply {
                    isChecked = true
                    setIcon(R.drawable.ic_baseline_light_mode_24)
                }
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switchTheme.apply {
                    isChecked = false
                    setIcon(R.drawable.ic_baseline_mode_night_24)
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.home -> {
                finish()
                true
            }
            R.id.change_theme -> {
                favoriteUserViewModel.saveThemeSetting(!item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}