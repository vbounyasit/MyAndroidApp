package com.example.mykotlinapp.ui.screens.chats.history.group.posts

import android.content.Context
import android.os.Bundle
import com.example.mykotlinapp.R
import com.example.mykotlinapp.ui.components.drawer.BottomDrawerMenu
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PostDrawers {

    val postDrawerTags = DrawerTags("PostCreatorMenu", "PostAdminMenu", "PostMenu")
    val commentDrawerTags = DrawerTags("CommentCreatorMenu", "CommentAdminMenu", "CommentMenu")
    val postDrawerResources = DrawerResources(R.string.post_remote_id, R.string.remove_post_alert, R.string.remove_post_alert_message)
    val commentDrawerResources = DrawerResources(R.string.comment_remote_id, R.string.remove_comment_alert, R.string.remove_comment_alert_message)

    data class DrawerTags(val creatorMenuTag: String, val adminMenuTag: String, val menuTag: String)
    data class DrawerResources(val itemBundleKey: Int, val removeAlertTitle: Int, val removeAlertMessage: Int)
    data class DrawerMenus(val creatorDrawerMenu: CreatorDrawerMenu, val adminDrawerMenu: AdminDrawerMenu, val drawerMenu: StandardDrawerMenu)

    data class DrawerMenusDefinition(
        private val context: Context,
        private val editPost: () -> Unit,
        private val removePost: (String) -> Unit,
        private val enableNotifications: (Bundle) -> Unit,
        private val drawerTags: DrawerTags,
        private val drawerResources: DrawerResources,
    ) {
        fun getDrawerMenus(): DrawerMenus {
            val drawerMenu = object : StandardDrawerMenu(enableNotifications) {
                override val tag: String = drawerTags.menuTag
            }
            val adminDrawerMenu = object : AdminDrawerMenu(context, removePost, { itemId, bundle -> drawerMenu.onMenuItemSelected(itemId)(bundle) }, drawerResources) {
                override val tag: String = drawerTags.adminMenuTag
            }
            val creatorDrawerMenu = object : CreatorDrawerMenu(editPost, { itemId, bundle -> adminDrawerMenu.onMenuItemSelected(itemId)(bundle) }) {
                override val tag: String = drawerTags.creatorMenuTag
            }
            return DrawerMenus(creatorDrawerMenu, adminDrawerMenu, drawerMenu)
        }
    }

    abstract class CreatorDrawerMenu(
        private val editPost: () -> Unit,
        private val defaultBehavior: (Int, Bundle) -> Unit,
    ) : BottomDrawerMenu {
        override val menuResourceIds: List<Int> = listOf(
            R.menu.post_settings_menu,
            R.menu.post_creator_options_menu,
            R.menu.post_admin_options_menu
        )

        override fun onMenuItemSelected(menuItemId: Int): (Bundle) -> Unit = { bundle ->
            when (menuItemId) {
                R.id.edit_post ->
                    editPost()
                else -> defaultBehavior(menuItemId, bundle)
            }
        }
    }

    abstract class AdminDrawerMenu(
        private val context: Context,
        private val removePost: (String) -> Unit,
        private val defaultBehavior: (Int, Bundle) -> Unit,
        private val drawerResources: DrawerResources,
    ) : BottomDrawerMenu {
        override val menuResourceIds: List<Int> = listOf(R.menu.post_settings_menu, R.menu.post_admin_options_menu)

        override fun onMenuItemSelected(menuItemId: Int): (Bundle) -> Unit = { bundle ->
            when (menuItemId) {
                R.id.remove_post ->
                    bundle.getString(context.getString(drawerResources.itemBundleKey))?.let {
                        MaterialAlertDialogBuilder(context)
                            .setTitle(context.getString(drawerResources.removeAlertTitle))
                            .setMessage(context.getString(drawerResources.removeAlertMessage))
                            .setNeutralButton(context.resources.getString(R.string.cancel_label), null)
                            .setPositiveButton(context.resources.getString(R.string.remove_post_alert_label)) { _, _ -> removePost(it) }
                            .show()

                    }
                else -> defaultBehavior(menuItemId, bundle)
            }
        }
    }

    abstract class StandardDrawerMenu(private val enableNotifications: (Bundle) -> Unit) : BottomDrawerMenu {

        override val menuResourceIds: List<Int> = listOf(R.menu.post_creator_options_menu)

        override fun onMenuItemSelected(menuItemId: Int): (Bundle) -> Unit = { bundle ->
            when (menuItemId) {
                R.id.turn_on_notification -> enableNotifications(bundle)
                else -> {}
            }
        }
    }
}