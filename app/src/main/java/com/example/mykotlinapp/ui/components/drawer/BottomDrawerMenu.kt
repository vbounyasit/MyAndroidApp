package com.example.mykotlinapp.ui.components.drawer

import android.os.Bundle

/**
 * Interface defining a bottom drawer's menu
 */
interface BottomDrawerMenu {

    /**
     * Unique tag for a specific drawer's menu
     */
    val tag: String

    /**
     * Menu resource ids to inflate to the drawer
     */
    val menuResourceIds: List<Int>

    /**
     * On menu item selected listener
     *
     * @param menuItemId The menu item id selected
     * @return The action to execute when menuItemId is selected, with the input bundle (@see [BottomDrawerManager.openDrawer])
     *
     */
    fun onMenuItemSelected(menuItemId: Int): (Bundle) -> Unit
}