package com.example.mykotlinapp.ui.screens.chats.creation

import android.widget.CheckBox
import com.example.mykotlinapp.databinding.ItemContactSelectionBinding
import com.example.mykotlinapp.databinding.ItemContactSelectorBinding
import com.example.mykotlinapp.model.dto.ui.user.UserContactDTO
import com.example.mykotlinapp.model.dto.ui.user.UserContactSelectionDTO
import com.example.mykotlinapp.ui.components.recycler_view.AppListAdapter
import com.example.mykotlinapp.ui.components.recycler_view.ClickListener

object ListAdapters {
    class ContactSelectionListAdapter(listener: ClickListener<UserContactDTO>) :
        AppListAdapter<UserContactDTO, ItemContactSelectionBinding>(
            bindData = { property, binding ->
                binding.property = property
                binding.listener = listener
            },
            inflate = { it, _ -> ItemContactSelectionBinding.inflate(it) }
        )

    class ContactSelectorListAdapter(onCheckedChange: (UserContactDTO, Boolean) -> Unit) :
        AppListAdapter<UserContactSelectionDTO, ItemContactSelectorBinding>(
            bindData = { property, binding ->
                binding.property = property
                binding.contactSelectionCheckbox.setOnClickListener { view ->
                    if (view is CheckBox) onCheckedChange(property.dto, view.isChecked)
                }
            },
            inflate = { it, _ -> ItemContactSelectorBinding.inflate(it) }
        )
}