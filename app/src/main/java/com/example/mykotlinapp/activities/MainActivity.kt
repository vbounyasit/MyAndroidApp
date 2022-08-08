package com.example.mykotlinapp.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.example.mykotlinapp.R
import com.example.mykotlinapp.databinding.ActivityMainBinding
import com.example.mykotlinapp.databinding.HeaderNavigationDrawerBinding
import com.example.mykotlinapp.ui.components.drawer.BottomDrawerUI
import com.example.mykotlinapp.ui.screens.chats.ChatFragmentDirections.actionChatFragmentToSettingsFragment
import com.example.mykotlinapp.ui.screens.chats.ChatFragmentDirections.actionChatFragmentToUserProfileFragment
import com.example.mykotlinapp.ui.screens.community.CommunityFragmentDirections.actionCommunityFragmentToSettingsFragment
import com.example.mykotlinapp.ui.screens.community.CommunityFragmentDirections.actionCommunityFragmentToUserProfileFragment
import com.example.mykotlinapp.ui.screens.discover.DiscoverFragmentDirections.actionDiscoverFragmentToSettingsFragment
import com.example.mykotlinapp.ui.screens.discover.DiscoverFragmentDirections.actionDiscoverFragmentToUserProfileFragment
import com.example.mykotlinapp.ui.screens.events.EventsFragmentDirections.actionEventsFragmentToSettingsFragment
import com.example.mykotlinapp.ui.screens.events.EventsFragmentDirections.actionEventsFragmentToUserProfileFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var headerNavViewBinding: HeaderNavigationDrawerBinding
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        headerNavViewBinding = HeaderNavigationDrawerBinding.bind(binding.userNavView.getHeaderView(0))
        headerNavViewBinding.lifecycleOwner = this
        headerNavViewBinding.viewModel = viewModel
        registerComponents()
        registerObservers()
        registerListeners()
        handleStartFromNotification()
        viewModel.retrieveInitialData()
        viewModel.showBottomNav()
    }

    override fun onDestroy() {
        viewModel.disconnectSocket()
        viewModel.cancelBackgroundWork()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        currentFocus?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun registerComponents() {
        viewModel.initializeAndConnectSocket()
        viewModel.notificationComponent.createChatChannel(getString(R.string.chat_notification_channel_key), getString(R.string.chat_notification_channel_name))
        viewModel.bottomDrawerManager.registerNavigationUI(this, bottomDrawerUI)
        viewModel.dialogFormFragmentManager.registerLifeCycle(this, supportFragmentManager)
    }

    private fun registerObservers() {
        viewModel.bottomNavigationDestination.observe(this) {
            it?.let {
                binding.navHostFragment.findNavController().navigate(it)
                viewModel.onBottomNavigation()
            }
        }
        viewModel.isLoggedOut.observe(this) {
            if (it) {
                val userActivityIntent = Intent(this, UserActivity::class.java)
                    .apply { putExtra(getString(R.string.skip_welcome_page), true) }
                viewModel.clearUserData()
                viewModel.onLogUserOut()
                startActivity(userActivityIntent)
                finish()
            }
        }
        viewModel.openUserDrawer.observe(this) {
            if (it) {
                binding.userDrawer.open()
                viewModel.onOpenUserDrawer()
            }
        }
        viewModel.darkMode.observe(this) {
            setDefaultNightMode(if (it) MODE_NIGHT_YES else MODE_NIGHT_NO)
        }
        viewModel.navigateToChatRemoteId.observe(this) {
            it?.let {
                viewModel.setBottomNavigationDestination(R.id.chatFragment)
            }
        }
        viewModel.isBottomNavVisible.observe(this) { visible ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                window.setSoftInputMode(
                    if (visible)
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
                    else
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                )
            else window.setDecorFitsSystemWindows(!visible)
        }
    }

    private fun registerListeners() {
        headerNavViewBinding.viewMode.setOnClickListener { view ->
            if (view is SwitchMaterial) viewModel.toggleViewMode(view.isChecked)
        }
        binding.userNavView.setNavigationItemSelectedListener { menuItem ->
            val result = when (menuItem.itemId) {
                R.id.menu_item_logout -> viewModel.logUserOut()
                R.id.menu_item_settings -> showDestinationFragment { it.userSettingsDirections }
                R.id.menu_item_my_profile -> showDestinationFragment { it.userProfileDirections }
                else -> null
            }
            result != null
        }
        binding.bottomNavigation.setOnItemSelectedListener {
            val destination = bottomNavigationDestinations[it.itemId]
            viewModel.setBottomNavigationDestination(destination)
            destination != null
        }
    }

    private fun handleStartFromNotification() {
        intent.getStringExtra(getString(R.string.chat_remote_id))?.let { chatRemoteId ->
            viewModel.setNavigationToChatRemoteId(chatRemoteId)
        }
    }

    private fun showDestinationFragment(getNavDirections: (SideNavigationDirections) -> NavDirections) {
        val currentDestination = binding.navHostFragment.findNavController().currentDestination?.id
        navigationMap[currentDestination]?.let {
            binding.userDrawer.close()
            binding.navHostFragment.findNavController().navigate(getNavDirections(it))
        }
    }

    private val bottomDrawerUI by lazy {
        object : BottomDrawerUI() {
            override val scrimView: View = binding.mainPageScrim
            override val navigationView: NavigationView = binding.mainBottomNavView
        }
    }

    private val bottomNavigationDestinations = mapOf(
        R.id.discover to R.id.discoverFragment,
        R.id.community to R.id.communityFragment,
        R.id.chat to R.id.chatFragment,
        R.id.events to R.id.eventsFragment
    )

    //TODO create a manual transition instead of defining one for each tabs
    private val navigationMap = mapOf(
        R.id.discoverFragment to SideNavigationDirections(
            actionDiscoverFragmentToUserProfileFragment(),
            actionDiscoverFragmentToSettingsFragment()
        ),
        R.id.communityFragment to SideNavigationDirections(
            actionCommunityFragmentToUserProfileFragment(),
            actionCommunityFragmentToSettingsFragment()
        ),
        R.id.chatFragment to SideNavigationDirections(
            actionChatFragmentToUserProfileFragment(),
            actionChatFragmentToSettingsFragment()
        ),
        R.id.eventsFragment to SideNavigationDirections(
            actionEventsFragmentToUserProfileFragment(),
            actionEventsFragmentToSettingsFragment()
        )
    )

    companion object {
        private data class SideNavigationDirections(
            val userProfileDirections: NavDirections,
            val userSettingsDirections: NavDirections,
        )
    }

}