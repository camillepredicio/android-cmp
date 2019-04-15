package io.predic.cmp_sdk.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import io.predic.cmp_sdk.R
import io.predic.cmp_sdk.adapters.SettingsAdapter
import io.predic.cmp_sdk.api.responses.Localization.PurposesLocalization
import io.predic.cmp_sdk.databinding.ActivitySettingsBinding
import io.predic.cmp_sdk.utils.CMPApp.Companion.appContext
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.viewmodels.SettingsViewModel
import io.predic.cmp_sdk.viewmodels.SettingsViewModel.Companion.partnerRecyclerViewList
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.concurrent.Callable

internal class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var purposesRecyclerView: RecyclerView
    private var compositeDisposable = CompositeDisposable()

    /**
     * Lifecycle
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        viewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        setSupportActionBar(settingsToolbar)
        settingsToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_left)
        settingsToolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.viewModel = viewModel
        viewModel.resources = applicationContext.resources
        purposesRecyclerView = binding.purposesRecyclerView
        viewModel.apiKey = appContext.appKey
        partnerRecyclerViewList = viewModel.configPartnersList()

        // Disables blinking of image, but makes awful animation of text
        //(purposesRecyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        purposesRecyclerView.layoutManager = LinearLayoutManager(this)
        if (!viewModel.checkLocalization()) {
            purposesRecyclerView.adapter = configAdapter()
        }
        configObserver()
        fetchAAID()
    }

    /**
     * Used to setup all observers.
     */
    private fun configObserver() {
        val openPartnersObserver = Observer<Constants.OpenPartnersStatus> { status ->
            if (status == Constants.OpenPartnersStatus.OpenPartners) {
                val intent = Intent(this, PartnersActivity::class.java)
                startActivity(intent)
            }
        }
        val closeSettingsObserver = Observer<Constants.CloseSettingsStatus> { status ->
            if (status == Constants.CloseSettingsStatus.CloseSettings) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
        val switchStateObserver = Observer<Constants.SwitchStateStatus> { status ->
            if (viewModel.localizationChanged.value != null) {
                purposesRecyclerView.swapAdapter(
                    configAdapter(
                        status == Constants.SwitchStateStatus.SwitchChecked,
                        viewModel.localizationChanged.value ?: emptyList()
                    ), false
                )
            } else {
                purposesRecyclerView.swapAdapter(
                    configAdapter(status == Constants.SwitchStateStatus.SwitchChecked),
                    false
                )
            }
        }
        val purposesLocalizationObserver = Observer<List<PurposesLocalization>> { localization ->
            if (localization != null) {
                if (purposesRecyclerView.adapter != null) {
                    purposesRecyclerView.swapAdapter(configAdapter(purposes = localization), false)
                } else {
                    purposesRecyclerView.adapter = configAdapter(purposes = localization)
                }
            }
        }
        viewModel.openPartners.observe(this, openPartnersObserver)
        viewModel.closeSettings.observe(this, closeSettingsObserver)
        viewModel.purposesSwitchState.observe(this, switchStateObserver)
        viewModel.localizationChanged.observe(this, purposesLocalizationObserver)
    }

    /**
     * Used to configure adapter
     *
     * @param enabled Define purpose state
     * @param purposes Used to add purposes
     * @return SettingsAdapter
     */
    private fun configAdapter(
        enabled: Boolean = false,
        purposes: List<PurposesLocalization> = emptyList()
    ): SettingsAdapter {
        return SettingsAdapter(viewModel.configPurposesList(enabled, purposes))
    }

    /**
     * Used to get users Advertizing ID.
     *
     * @throws Exception: If we can't get users Advertizing ID.
     */
    private fun fetchAAID() {
        compositeDisposable.add(
            Observable.fromCallable(object : Callable<String> {
                @Throws(Exception::class)
                override fun call(): String {
                    return AdvertisingIdClient.getAdvertisingIdInfo(applicationContext).id
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onComplete() {}
                    override fun onNext(id: String) {
                        viewModel.AAID = id
                    }

                    override fun onError(e: Throwable) {
                        Log.e("PREDICIO", e.toString())
                    }
                })
        )
    }

    private fun checkLocalization(): Boolean {
        if (!viewModel.localizationChanged.value.isNullOrEmpty()) {
            return true
        }
        return false
    }
}
