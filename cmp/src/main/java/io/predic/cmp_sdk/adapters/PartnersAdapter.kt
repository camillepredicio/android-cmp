package io.predic.cmp_sdk.adapters

import android.content.Context
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import io.predic.cmp_sdk.R
import io.predic.cmp_sdk.activities.ConsentActivity.Companion.consentResources
import io.predic.cmp_sdk.databinding.ListItemPartnersBinding
import io.predic.cmp_sdk.models.Partner
import io.predic.cmp_sdk.utils.CMPApp.Companion.appContext
import io.predic.cmp_sdk.viewmodels.SettingsViewModel.Companion.partnerRecyclerViewList

internal class PartnersAdapter(
    private val dataSource: List<Partner>,
    private val context: Context
) : RecyclerView.Adapter<PartnersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemPartnersBinding.inflate(inflater)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataSource.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSource[position])

        partnerRecyclerViewList = dataSource

        holder.binding.partnersSwitch.setOnCheckedChangeListener { _, isChecked ->
            when (isChecked) {
                true -> dataSource[position].state = true
                false -> dataSource[position].state = false
            }
        }
    }

    inner class ViewHolder(val binding: ListItemPartnersBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Partner) {
            binding.partner = item
            binding.partnersNameTextView.textSize = appContext.partnersNameTextSize ?: 18.toFloat()
            binding.privacyUrlTextView.textSize = appContext.partnersDescriptionTextSize ?: 14.toFloat()
            binding.partnersNameTextView.setTextColor(
                appContext.partnersNameTextColor ?: consentResources.getColor(R.color.blackColor)
            )
            binding.privacyUrlTextView.setTextColor(
                appContext.partnersDescriptionTextColor ?: consentResources.getColor(R.color.material_grey_600)
            )
            binding.executePendingBindings()

            setHyperlink(binding.privacyUrlTextView)
        }
    }

    /**
     * Parse HTML.
     *
     * @param html HTML code.
     * @return Spanned
     */
    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    /**
     * Used to set hyperlink in text.
     *
     * @param holder ViewHolder
     * @param position Position of current item
     */
    private fun setHyperlink(tv: TextView) {
        tv.text = fromHtml(tv.text.toString())
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.setLinkTextColor(context.resources.getColor(R.color.buttonColor))
    }
}