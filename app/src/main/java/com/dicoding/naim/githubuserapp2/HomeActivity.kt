package com.dicoding.naim.githubuserapp2

import android.app.SearchManager
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
import androidx.appcompat.widget.SearchView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.naim.githubuserapp2.adapter.ListUserAdapter
import com.dicoding.naim.githubuserapp2.database.User
import com.dicoding.naim.githubuserapp2.databinding.ActivityHomeBinding
import com.dicoding.naim.githubuserapp2.response.ItemsItem
import com.dicoding.naim.githubuserapp2.viewmodel.HomeViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeActivity : AppCompatActivity() {

    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding
    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModel.Factory(SettingPreferences.getInstance(dataStore))
    }
    private var uname: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Github User's Search"

        binding?.rvUsers?.layoutManager =
            if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) GridLayoutManager(
                this,
                2
            ) else LinearLayoutManager(this)

        homeViewModel.apply {
            listUser.observe(this@HomeActivity) { users ->
                setUserData(users)
            }
            isLoading.observe(this@HomeActivity) {
                showLoading(it)
            }
            snackText.observe(this@HomeActivity) {
                it.getContentIfNotHandled()?.let { snackText ->
                    binding?.let { it1 ->
                        Snackbar.make(
                            it1.root,
                            snackText,
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("TRY AGAIN") { homeViewModel.findUser(uname) }
                            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun setUserData(users: List<ItemsItem?>?) {
        val listUser = ArrayList<User>()
        if (users != null) {
            for (user in users) {
                val userData = User(
                    name = user?.login.toString(),
                    username = user?.login.toString(),
                    avatar = user?.avatarUrl.toString()
                )
                listUser.add(userData)
            }
        }
        val listUserAdapter = ListUserAdapter(listUser)
        binding?.rvUsers?.adapter = listUserAdapter

        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: User) {
                showUserDetailData(data)
            }
        })
    }

    private fun showUserDetailData(user: User) {
        val detailUserIntent = Intent(this@HomeActivity, DetailUserActivity::class.java).apply {
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

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.apply {
            maxWidth = Int.MAX_VALUE
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            queryHint = resources.getString(R.string.search_hint)
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    if (query == "") homeViewModel.findUser("\"\"")
                    else {
                        homeViewModel.findUser(query)
                        uname = query
                        searchView.clearFocus()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    if (newText == "") homeViewModel.findUser("\"\"")
                    else {
                        homeViewModel.findUser(newText)
                        uname = newText
                    }
                    return false
                }
            })
        }

        val homeBtn = menu.findItem(R.id.home)
        homeBtn.isVisible = false

        val switchTheme = menu.findItem(R.id.change_theme)

        homeViewModel.getThemeSettings().observe(this@HomeActivity) { darkMode ->
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
            R.id.favorite -> {
                val favoriteIntent = Intent(this, FavoriteUserActivity::class.java)
                startActivity(favoriteIntent)
                true
            }
            R.id.change_theme -> {
                homeViewModel.saveThemeSetting(!item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}