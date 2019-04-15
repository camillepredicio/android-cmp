package io.predic.cmp_sdk.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.predic.cmp_sdk.R
import io.predic.cmp_sdk.adapters.PartnersAdapter
import io.predic.cmp_sdk.databinding.ActivityPartnersBinding
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.viewmodels.PartnersViewModel
import io.predic.cmp_sdk.viewmodels.PartnersViewModel.Companion.configPartnersList
import io.predic.cmp_sdk.viewmodels.SettingsViewModel.Companion.partnerRecyclerViewList
import io.predic.cmp_sdk.viewmodels.SettingsViewModel.Companion.partnerTemporaryData
import kotlinx.android.synthetic.main.activity_partners.*

internal class PartnersActivity : AppCompatActivity() {

    private lateinit var viewModel: PartnersViewModel
    private lateinit var partnersRecyclerView: RecyclerView

    /**
     * Lifecycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityPartnersBinding = DataBindingUtil.setContentView(this, R.layout.activity_partners)
        viewModel = ViewModelProviders.of(this).get(PartnersViewModel::class.java)

        binding.viewModel = viewModel


        partnersRecyclerView = binding.partnersRecyclerView
        partnersRecyclerView.layoutManager = LinearLayoutManager(this)
        partnersRecyclerView.adapter = configAdapter()

        setSupportActionBar(partnersToolbar)
        partnersToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_left)
        partnersToolbar.setNavigationOnClickListener {
            partnerTemporaryData = partnerRecyclerViewList
            onBackPressed()
        }
        configObserver()
    }

    /**
     * Used to configure adapter
     *
     * @param enabled Define partner state
     * @return PartnersAdapter
     */
    private fun configAdapter(enabled: Boolean = false): PartnersAdapter {
        return PartnersAdapter(configPartnersList(enabled), applicationContext)
    }

    /**
     * Used to configure adapter
     *
     * @return PartnersAdapter
     */
    private fun configAdapter(): PartnersAdapter {
        return PartnersAdapter(partnerRecyclerViewList ?: emptyList(), applicationContext)
    }

    /**
     * Used to setup all observers.
     */
    private fun configObserver() {
        val switchStateObserver = Observer<Constants.SwitchStateStatus> { status ->
            if (status == Constants.SwitchStateStatus.SwitchChecked) {
                partnersRecyclerView.swapAdapter(
                    configAdapter(true),
                    false
                )
            } else {
                partnersRecyclerView.swapAdapter(
                    configAdapter(false),
                    false
                )
            }
        }

        viewModel.partnersSwitchState.observe(this, switchStateObserver)
    }
}
