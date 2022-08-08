package com.example.mykotlinapp.ui.screens.side_nav.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.mykotlinapp.R
import com.example.mykotlinapp.activities.MainActivityViewModel
import com.example.mykotlinapp.databinding.FragmentUserProfilePageBinding
import com.example.mykotlinapp.model.dto.inputs.form.user.UpdateUserInput
import com.example.mykotlinapp.ui.AppFragment
import com.example.mykotlinapp.ui.components.drawer.BottomDrawerMenu
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : AppFragment() {

    private val viewModel by viewModels<UserProfileViewModel>()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var binding: FragmentUserProfilePageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserProfilePageBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        sharedViewModel.hideBottomNav()
        return binding.root
    }

    override fun onDestroy() {
        sharedViewModel.showBottomNav()
        super.onDestroy()
    }

    override fun registerComponents() {
        super.registerComponents()
        sharedViewModel.bottomDrawerManager.registerNavigationDrawerMenu(requireActivity(), bottomDrawerMenu)
        sharedViewModel.dialogFormFragmentManager.registerDialogForm(userProfileEditDialog)
    }

    override fun registerListeners() {
        super.registerListeners()
        binding.userProfilePageTopBar.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.userProfileEditPictureButton.setOnClickListener {
            sharedViewModel.bottomDrawerManager.openDrawer(bottomDrawerMenu)
        }
        binding.userProfileEditButton.setOnClickListener {
            viewModel.userData.value?.let { sharedViewModel.dialogFormFragmentManager.openDialogForm(userProfileEditDialog.dialogFragmentTag) }
        }
    }

    override fun registerObservers() {
        super.registerObservers()
        viewModel.userData.observe(viewLifecycleOwner) {
            it?.let { userDTO ->
                userProfileEditDialog.initialInput = UpdateUserInput(
                    userDTO.firstName,
                    userDTO.lastName,
                    userDTO.email,
                    userDTO.description
                )
            }
        }
    }

    private val userProfileEditDialog by lazy {
        UserProfileEditDialogFragment(
            confirmFunction = { updateInput ->
                viewModel.sendUserProfileUpdate(updateInput)
            }
        )
    }

    private val bottomDrawerMenu = object : BottomDrawerMenu {
        override val tag: String = "navigationDrawerMenu"
        override val menuResourceIds: List<Int> = listOf(R.menu.upload_picture_menu)

        override fun onMenuItemSelected(menuItemId: Int): (Bundle) -> Unit {
            return when (menuItemId) {
                R.id.select_picture -> { _ -> selectImageFromGallery() }
                else -> { _ -> }
            }
        }
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {

        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

}