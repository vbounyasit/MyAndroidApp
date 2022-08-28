package com.example.mykotlinapp.ui.components.drawer

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

/**
 * Bottom drawer manager allowing the activity component to configure and open a bottom menu
 * See: [Materiel Design UI](https://material.io/components/navigation-drawer#bottom-drawer)
 * @Injected in an activity viewModel
 */
class BottomDrawerManager {

    private val bottomDrawerMenuMenuMap = mutableMapOf<String, BottomDrawerMenu>()
    private val _navigationDrawerData = MutableLiveData<Bundle?>()
    private val _navigationDrawerMenuItemSelected = MutableLiveData<Int?>()
    private val _navigationDrawerInflatedMenu = MutableLiveData<List<Int>>()
    private val _currentOpenDrawer = MutableLiveData<String>()

    private fun resetNavigationDrawerData() {
        _navigationDrawerMenuItemSelected.value = null
        _navigationDrawerData.value = null
    }

    /**
     * - Usually called on the main activity creation
     * - Registers the different components observers
     * - Sets the behaviors for opening/closing drawer and inflating the menu
     *
     * @param lifecycleOwner The lifecycle owner to observe the components on
     * @param bottomDrawerUI a BottomDrawerUI containing information on the drawer behavior
     */
    fun registerNavigationUI(lifecycleOwner: LifecycleOwner, bottomDrawerUI: BottomDrawerUI) {
        bottomDrawerUI.initializeNavigationDrawer()
        bottomDrawerUI.scrimView.setOnClickListener { resetNavigationDrawerData() }
        bottomDrawerUI.navigationView.setNavigationItemSelectedListener {
            _navigationDrawerMenuItemSelected.value = it.itemId
            true
        }
        _navigationDrawerData.observe(lifecycleOwner) {
            it?.let { bottomDrawerUI.showDrawer() } ?: bottomDrawerUI.hideDrawer()
        }
        _navigationDrawerInflatedMenu.observe(lifecycleOwner) {
            bottomDrawerUI.navigationView.menu.clear()
            it.forEach { menuId ->
                bottomDrawerUI.navigationView.inflateMenu(menuId)
            }
        }
    }

    /**
     * - Usually called on the main activity creation
     * - Registers the different components observers to the activity
     * - Sets the behaviors for the drawer's menu actions
     *
     * @param activity The activity lifecycle owner
     * @param bottomDrawerMenu a BottomDrawerMenu containing information on the drawer's menu
     */
    fun registerNavigationDrawerMenu(
        activity: FragmentActivity,
        bottomDrawerMenu: BottomDrawerMenu
    ) {
        bottomDrawerMenuMenuMap[bottomDrawerMenu.tag] = bottomDrawerMenu
        _navigationDrawerMenuItemSelected.removeObservers(activity)
        _navigationDrawerMenuItemSelected.observe(activity) {
            it?.let { menuItemId ->
                _currentOpenDrawer.value?.let { tag ->
                    _navigationDrawerData.value?.let { bundle ->
                        bottomDrawerMenuMenuMap[tag]?.onMenuItemSelected(menuItemId)?.invoke(bundle)
                    }
                }
                resetNavigationDrawerData()
            }
        }
    }

    /**
     * Opens a given drawer defined by its BottomDrawerMenu, and creates a bundle to pass on to the menu item click listener
     *
     * @param bottomDrawerMenu The menu to open
     * @param createBundle The creation function for the bundle to pass to menu to open
     */
    fun openDrawer(bottomDrawerMenu: BottomDrawerMenu, createBundle: Bundle.() -> Unit = {}) {
        bottomDrawerMenuMenuMap[bottomDrawerMenu.tag]?.let {
            _navigationDrawerInflatedMenu.value = it.menuResourceIds
            _navigationDrawerData.value = Bundle().apply(createBundle)
            _currentOpenDrawer.value = bottomDrawerMenu.tag
        }
    }

}