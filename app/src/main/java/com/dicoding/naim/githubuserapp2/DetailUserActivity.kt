package com.dicoding.naim.githubuserapp2

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.dicoding.naim.githubuserapp2.adapter.SectionsPagerAdapter
import com.dicoding.naim.githubuserapp2.database.User
import com.dicoding.naim.githubuserapp2.databinding.ActivityDetailUserBinding
import com.dicoding.naim.githubuserapp2.response.DetailUserResponse
import com.dicoding.naim.githubuserapp2.viewmodel.DetailUserViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailUserActivity : AppCompatActivity() {

    private var _binding: ActivityDetailUserBinding? = null
    private val binding get() = _binding
    private val detailUserViewModel by viewModels<DetailUserViewModel> {
        DetailUserViewModel.Factory(
            SettingPreferences.getInstance(dataStore),
            intent.getParcelableExtra<User>(EXTRA_USER)?.username ?: "",
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail User"

        val user = intent.getParcelableExtra<User>(EXTRA_USER)

        detailUserViewModel.apply {
            showDetailUser(user?.username.toString())
            detailUser.observe(this@DetailUserActivity) { user ->
                setUserDetailData(user)
            }
            isLoading.observe(this@DetailUserActivity) {
                showLoading(it)
            }
            snackText.observe(this@DetailUserActivity) {
                it.getContentIfNotHandled()?.let { snackText ->
                    binding?.let { it1 ->
                        Snackbar.make(
                            it1.root,
                            snackText,
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("TRY AGAIN") { detailUserViewModel.showDetailUser(user?.username.toString()) }
                            .setDuration(BaseTransientBottomBar.LENGTH_LONG)
                            .show()
                    }
                }
            }
            isFavUser.observe(this@DetailUserActivity) { fav ->
                binding?.apply {
                    floatingFavButton.visibility = swapFavButton(!fav)
                    floatingUnFavButton.visibility = swapFavButton(fav)
                }
            }
        }

        binding?.apply {
            if (user != null) {
                floatingFavButton.setOnClickListener {
                    detailUserViewModel.setFavUser(user, true)
                    Toast.makeText(
                        this@DetailUserActivity,
                        getString(R.string.add_favorite),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                floatingUnFavButton.setOnClickListener {
                    detailUserViewModel.setFavUser(user, false)
                    Toast.makeText(
                        this@DetailUserActivity,
                        getString(R.string.delete_favorite),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        val viewPager = binding?.viewPager
        viewPager?.adapter = SectionsPagerAdapter(this, user?.username.toString())
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> viewPager?.setBackgroundColor(Color.DKGRAY)
            Configuration.UI_MODE_NIGHT_NO -> viewPager?.setBackgroundColor(Color.LTGRAY)
            else -> viewPager?.setBackgroundColor(Color.TRANSPARENT)
        }

        val tabs = binding?.tabs
        tabs?.setSelectedTabIndicatorColor(Color.parseColor("#2DBA4E"))
        if (tabs != null && viewPager != null) {
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.text = resources.getString(TAB_TITLES[position])
            }.attach()
        }
        supportActionBar?.elevation = 0f
    }

    private fun setUserDetailData(user: DetailUserResponse) {
        binding?.apply {
            Glide.with(this@DetailUserActivity)
                .load(user.avatarUrl)
                .circleCrop()
                .into(imgItemPhoto)
            tvItemName.text = user.name
            tvItemUsername.text = user.login
            tvItemFollowers.text = getString(R.string.followers, user.followers)
            tvItemFollowing.text = getString(R.string.following, user.following)
            tvItemCompany.text = user.company
            tvItemLocation.text = user.location
            tvItemRepository.text = getString(R.string.repo, user.publicRepos)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun swapFavButton(isFav: Boolean): Int {
        return if (isFav) View.VISIBLE else View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val searchView = menu.findItem(R.id.search)
        searchView.isVisible = false

        val favBtn = menu.findItem(R.id.favorite)
        favBtn.isVisible = false

        val switchTheme = menu.findItem(R.id.change_theme)

        detailUserViewModel.getThemeSettings().observe(this@DetailUserActivity) { darkMode ->
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
                detailUserViewModel.saveThemeSetting(!item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }
}
