package io.predic.cmp_sdk.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import io.predic.cmp_sdk.databinding.ListItemPurposesBinding
import io.predic.cmp_sdk.models.Purpose
import io.predic.cmp_sdk.viewmodels.SettingsViewModel

internal class SettingsAdapter(private val dataSource: List<Purpose>) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPurposesBinding.inflate(inflater)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataSource.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSource[position])
        SettingsViewModel.purposeRecyclerViewList = dataSource

        holder.binding.purposeSwitch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> dataSource[position].enabled = true
                false -> dataSource[position].enabled = false
            }
        }

        holder.binding.purposeContentTextView.setOnClickListener {
            val expanded = holder.binding.purpose?.expanded ?: false
            holder.binding.purpose?.expanded = !expanded
            notifyItemChanged(position)
        }
    }

    inner class ViewHolder(val binding: ListItemPurposesBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Purpose) {

            binding.purposeContentTextView.maxLines = when (item.expanded) {
                true -> Int.MAX_VALUE
                false -> 3
            }

            binding.purpose = item
            binding.executePendingBindings()
        }
    }
}