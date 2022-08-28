package com.example.mykotlinapp.ui.components.recycler_view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.example.mykotlinapp.domain.pojo.PayloadType
import com.example.mykotlinapp.model.dto.inputs.ui_item.InputUIDTO

/**
 * Helper class for creating listAdapters for recycler views
 *
 * @param Property The type of the items to display in the list (< RecyclerViewItem)
 * @param Binding The type of the view binder (< ViewDataBinding)
 * @property bindData The (Property, Binding) -> Unit function to bind the data to the views for each item
 * @property inflate The function to inflate a layout for each item
 * @property payloadsMetadata A list of PayloadMetaData parameters to handle dynamic data changes in the adapter
 */
open class AppListAdapter<Property : RecyclerViewItem, Binding : ViewDataBinding>(
    val bindData: (Property, Binding) -> Unit,
    val inflate: (LayoutInflater, Int) -> Binding,
    val payloadsMetadata: (List<PayloadMetaData<Property, Binding>>)? = null,
) : ListAdapter<Property, AppViewHolder<Binding>>(

    /**
     * Creating the DiffCheckCallback to check whether the list adapter data changed
     */
    object : ItemCallback<Property>() {
        /**
         * Creates a Payload (Bundle) to pass to the view holder to apply dynamic changes instead of recreating the whole layout
         *
         * @param oldItem The item before change
         * @param newItem The item after change
         * @return Either a bundle to pass to the view holder or the default value from the superclass
         */
        override fun getChangePayload(oldItem: Property, newItem: Property): Any? {
            return payloadsMetadata?.let {
                val bundle = Bundle()
                it.forEach { payloadMetaData ->
                    val newParcelableItem = payloadMetaData.toInputDTO(newItem)
                    if (payloadMetaData.toInputDTO(oldItem) != newParcelableItem) {
                        bundle.apply {
                            putParcelable(
                                payloadMetaData.payloadKey,
                                newParcelableItem
                            )
                        }
                    }
                }
                if (bundle.size() > 0) bundle else super.getChangePayload(oldItem, newItem)
            } ?: super.getChangePayload(oldItem, newItem)
        }

        override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem.remoteId == newItem.remoteId
        }

        override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder<Binding> {
        return AppViewHolder(inflate(LayoutInflater.from(parent.context), viewType))
    }

    override fun onBindViewHolder(holder: AppViewHolder<Binding>, position: Int) {
        val property: Property = getItem(position)
        holder.bind { binding ->
            bindData(property, binding)
            payloadsMetadata?.forEach { payloadMetaData ->
                payloadMetaData.applyChangesFromPayload(
                    payloadMetaData.toInputDTO(property),
                    binding
                )
            }
        }
    }

    override fun onBindViewHolder(
        holder: AppViewHolder<Binding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val item = payloads[0] as Bundle
            payloadsMetadata?.forEach { payloadMetaData ->
                holder.bind { binding ->
                    val inputDTO: InputUIDTO? = item.getParcelable(payloadMetaData.payloadKey)
                    inputDTO?.let {
                        payloadMetaData.applyChangesFromPayload(it, binding)
                    }
                }
            }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    companion object {
        /**
         * The Payload meta data
         *
         * @param Property The type of the items to display in the list (< RecyclerViewItem)
         * @param Binding The type of the view binder (< ViewDataBinding)
         * @property payloadType The type of payload to send
         * @property toInputDTO Converts the item to Input data
         * @property applyChangesFromPayload Applies the dynamic changes with the input payload
         */
        data class PayloadMetaData<Property : RecyclerViewItem, Binding : ViewDataBinding>(
            val payloadType: PayloadType,
            val toInputDTO: (Property) -> InputUIDTO,
            val applyChangesFromPayload: ((InputUIDTO, Binding) -> Unit),
        ) {
            val payloadKey = payloadType.name
        }
    }

}