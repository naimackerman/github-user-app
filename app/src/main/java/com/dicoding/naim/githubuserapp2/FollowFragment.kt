package com.dicoding.naim.githubuserapp2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.naim.githubuserapp2.adapter.ListUserAdapter
import com.dicoding.naim.githubuserapp2.database.User
import com.dicoding.naim.githubuserapp2.databinding.FragmentFollowBinding
import com.dicoding.naim.githubuserapp2.response.ItemsItem
import com.dicoding.naim.githubuserapp2.viewmodel.FollowViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

class FollowFragment : Fragment() {

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding

    private val followViewModel by viewModels<FollowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARG_USERNAME, "")
        val index = arguments?.getInt(ARG_SECTION_NUMBER, 0)

        if (index == 0) followViewModel.findUserFollower(username.toString())
        else followViewModel.findUserFollowing(username.toString())

        val layoutManager = LinearLayoutManager(requireActivity())
        binding?.apply {
            rvUsers.apply {
                setHasFixedSize(true)
                this.layoutManager = layoutManager
                addItemDecoration(
                    DividerItemDecoration(
                        requireActivity(),
                        layoutManager.orientation
                    )
                )
            }
        }

        followViewModel.apply {
            listUser.observe(requireActivity()) { users ->
                setUserData(users)
            }
            isLoading.observe(requireActivity()) {
                showLoading(it)
            }
            snackText.observe(requireActivity()) {
                it.getContentIfNotHandled()?.let { snackText ->
                    binding?.let { it1 ->
                        Snackbar.make(
                            it1.root,
                            snackText,
                            Snackbar.LENGTH_SHORT
                        ).setAction("TRY AGAIN") {
                            if (index == 0) followViewModel.findUserFollower(username.toString())
                            else followViewModel.findUserFollowing(username.toString())
                        }.setDuration(BaseTransientBottomBar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setUserData(users: List<ItemsItem?>) {
        val listUser = ArrayList<User>()
        for (user in users) {
            val userData = User(
                name = user?.login.toString(),
                username = user?.login.toString(),
                avatar = user?.avatarUrl.toString()
            )
            listUser.add(userData)
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
        val detailUserIntent = Intent(requireActivity(), DetailUserActivity::class.java)
        detailUserIntent.putExtra(DetailUserActivity.EXTRA_USER, user)
        startActivity(detailUserIntent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        const val ARG_USERNAME = "username"
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}