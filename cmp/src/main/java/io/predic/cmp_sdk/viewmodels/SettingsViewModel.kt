package io.predic.cmp_sdk.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import io.predic.cmp_sdk.R
import io.predic.cmp_sdk.api.NetApi
import io.predic.cmp_sdk.api.NetService
import io.predic.cmp_sdk.api.PartnerRequest
import io.predic.cmp_sdk.api.responses.GetPartnersResponse
import io.predic.cmp_sdk.api.responses.GetSetContentDataResponse
import io.predic.cmp_sdk.api.responses.Localization
import io.predic.cmp_sdk.api.responses.Localization.PurposesLocalization
import io.predic.cmp_sdk.api.responses.PartnerList
import io.predic.cmp_sdk.models.Partner
import io.predic.cmp_sdk.models.Purpose
import io.predic.cmp_sdk.utils.CMPApp.Companion.appContext
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.utils.Encryption
import io.predic.cmp_sdk.utils.boolToString
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.*


internal class SettingsViewModel : ViewModel() {

    companion object {
        lateinit var purposeRecyclerViewList: List<Purpose>
        var partnerRecyclerViewList: List<Partner>? = null
        var partnerTemporaryData: List<Partner>? = null
    }

    private var compositeDisposable = CompositeDisposable()
    var isLocalizationUsed = false
    internal lateinit var resources: Resources
    internal lateinit var AAID: String
    internal lateinit var apiKey: String

    val openPartners: MutableLiveData<Constants.OpenPartnersStatus> by lazy {
        MutableLiveData<Constants.OpenPartnersStatus>()
    }
    val closeSettings: MutableLiveData<Constants.CloseSettingsStatus> by lazy {
        MutableLiveData<Constants.CloseSettingsStatus>()
    }
    val purposesSwitchState: MutableLiveData<Constants.SwitchStateStatus> by lazy {
        MutableLiveData<Constants.SwitchStateStatus>()
    }
    val localizationChanged: MutableLiveData<List<PurposesLocalization>> by lazy {
        MutableLiveData<List<PurposesLocalization>>()
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

    /**
     * Called when Partners button clicked.
     */
    fun onPartnersButtonClick(view: View) {
        if (partnerTemporaryData != null) {
            partnerRecyclerViewList = partnerTemporaryData
        }
        openPartners.value = Constants.OpenPartnersStatus.OpenPartners
    }

    /**
     * Called when Save Preferences button clicked.
     */
    fun onSavePreferencesButtonClick(view: View) {
        requestSetConsentData(AAID)
        closeSettings.value = Constants.CloseSettingsStatus.CloseSettings
    }

    fun onSwitchCheckedChange(isChecked: Boolean) {
        if (isChecked) {
            purposesSwitchState.value = Constants.SwitchStateStatus.SwitchChecked
        } else {
            purposesSwitchState.value = Constants.SwitchStateStatus.SwitchUnchecked
        }
    }

    /**
     * Used for PurposesRecyclerView configuration. Purposes has 6 items by default.
     *
     * @return List of purposes
     */
    fun configPurposesList(
        enabled: Boolean = false,
        purposes: List<PurposesLocalization> = emptyList()
    ): List<Purpose> {

        val list = mutableListOf<Purpose>()
        val purposesVisibilityList = purposesVisibility()
        val purposesIconsList = purposesImages()

        if (!purposes.isEmpty()) {
            purposes.forEachIndexed { index, purpose ->
                list.add(
                    Purpose(
                        purposesImages()[index],
                        purpose.name,
                        purpose.description,
                        enabled,
                        visibility = purposesVisibilityList[index]
                    )
                )
            }
            isLocalizationUsed = true
            return list.toList()
        }

        // information storage and access
        list.add(
            Purpose(
                purposesIconsList[0],
                resources.getString(R.string.informationStorageAndAccessTitle),
                resources.getString(R.string.informationStorageAndAccessContent),
                enabled,
                visibility = purposesVisibilityList[0]
            )
        )
        // personalisation
        list.add(
            Purpose(
                purposesIconsList[1],
                resources.getString(R.string.personalisationTitle),
                resources.getString(R.string.personalisationContent),
                enabled,
                visibility = purposesVisibilityList[1]
            )
        )
        // ad selection, delivery, reporting
        list.add(
            Purpose(
                purposesIconsList[2],
                resources.getString(R.string.adSelectionDeliveryReportingTitle),
                resources.getString(R.string.adSelectionDeliveryReportingContent),
                enabled,
                visibility = purposesVisibilityList[2]
            )
        )
        // content selection, delivery, reporting
        list.add(
            Purpose(
                purposesIconsList[3],
                resources.getString(R.string.contentSelectionDeliveryReportingTitle),
                resources.getString(R.string.contentSelectionDeliveryReportingContent),
                enabled,
                visibility = purposesVisibilityList[3]
            )
        )
        // measurement
        list.add(
            Purpose(
                purposesIconsList[4],
                resources.getString(R.string.measurementTitle),
                resources.getString(R.string.measurementContent),
                enabled,
                visibility = purposesVisibilityList[4]
            )
        )
        // location-based advertisement
        list.add(
            Purpose(
                purposesIconsList[5],
                resources.getString(R.string.locationBasedAdvertisingTitle),
                resources.getString(R.string.locationBasedAdvertisingContent),
                enabled,
                visibility = purposesVisibilityList[5]
            )
        )
        return list.toList()
    }

    /**
     * Configures RecyclerView with partners from server.
     *
     * @return List of partners.
     */
    fun configPartnersList(): List<Partner> {
        val list = mutableListOf<Partner>()
        requestPartnersData { listOfPartners ->
            listOfPartners?.forEach { partner ->
                list.add(Partner(partner.key ?: "", partner.name ?: "", partner.text ?: ""))
            }
        }
        return list
    }

    /**
     * Used to check if user need localization. If yes - makes request for localization.
     *
     * @return If needlocalization returns true
     */
    internal fun checkLocalization(): Boolean {
        Log.e(Constants.TAG, getCurrentCountry())
        for (country in Constants.CountryCodes.values()) {
            if (country.code == getCurrentCountry()) {
                requestLocalizationData(country.locale)
                return true
            }
        }
        return false
    }

    /**
     * Request for localization.
     *
     * @param locale String
     */
    private fun requestLocalizationData(locale: String) {
        val locationApi: NetApi = NetService.getLocalizationApi()
        compositeDisposable.add(
            locationApi.getLocalization(locale)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<Localization>() {
                    override fun onComplete() {}
                    override fun onNext(localization: Localization) {
                        Log.d(Constants.TAG, "Localization get successfully")
                        localizationChanged.value = localization.purposes
                    }

                    override fun onError(e: Throwable) {
                        Log.e(Constants.TAG, e.toString())
                    }
                })
        )
    }

    /**
     * Request used to get list of partners.
     *
     * @param block Callback with partners list.
     */
    private fun requestPartnersData(block: (data: List<PartnerList>?) -> Unit?) {
        val aaid = appContext.aaid ?: ""
        if (aaid.isEmpty()) {
            Log.e(Constants.TAG, "AAID Not found")
            return
        }
        val md5Value = Encryption.getMD5(aaid)
        val aaidEncrypted = if (md5Value != null) md5Value else {
            Log.e(Constants.TAG, "MD5 AAID unwrap failed")
            return
        }

        val api: NetApi = NetService.getNetApi(aaidEncrypted)
        compositeDisposable.add(
            api.getPartnersData(appContext.appKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<GetPartnersResponse>() {
                    override fun onComplete() {}
                    override fun onNext(partners: GetPartnersResponse) {
                        block(partners.partners)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(Constants.TAG, e.toString())
                    }
                })
        )
    }

    /**
     * Takes country, where user currently placed
     *
     * @return Country full name
     */
    private fun getCurrentCountry(): String {
        return Locale.getDefault().country
    }

    /**
     * Defines all purposes icons.
     *
     * @return List of icons.
     */
    private fun purposesImages(): List<Drawable> {
        return listOf(
            resources.getDrawable(appContext.informationStorageIcon ?: R.drawable.ic_folder_key),
            resources.getDrawable(appContext.personalizationIcon ?: R.drawable.ic_heart),
            resources.getDrawable(appContext.adSelectionIcon ?: R.drawable.ic_bullhorn),
            resources.getDrawable(appContext.contentSelectionIcon ?: R.drawable.ic_image),
            resources.getDrawable(appContext.measurementIcon ?: R.drawable.ic_chart_line),
            resources.getDrawable(appContext.locationBasedAdvertisingIcon ?: R.drawable.ic_earth)
        )
    }

    /**
     * Defines all purposes visibility
     *
     * @return List<Boolean>
     */
    private fun purposesVisibility(): List<Int> {
        return listOf(
            if (appContext.informationStorageVisibility != false) Constants.Visibility.Visible.status else Constants.Visibility.Gone.status,
            if (appContext.personalizationVisibility != false) Constants.Visibility.Visible.status else Constants.Visibility.Gone.status,
            if (appContext.adSelectionVisibility != false) Constants.Visibility.Visible.status else Constants.Visibility.Gone.status,
            if (appContext.contentSelectionVisibility != false) Constants.Visibility.Visible.status else Constants.Visibility.Gone.status,
            if (appContext.measurementVisibility != false) Constants.Visibility.Visible.status else Constants.Visibility.Gone.status,
            if (appContext.locationBasedAdvertisingVisibility != false) Constants.Visibility.Visible.status else Constants.Visibility.Gone.status
        )
    }

    /**
     * Requests to save consent data.
     * @param AAID Advertising ID.
     */
    private fun requestSetConsentData(AAID: String) {
        val aaidEncrypted = Encryption.encryptAAID(AAID)

        val purposes = mutableListOf<String>()
        val partners = mutableListOf<PartnerRequest>()

        val tempPurposes: List<Purpose> = purposeRecyclerViewList
        for (purpose in tempPurposes) {
            purposes.add(purpose.enabled.boolToString())
        }
        if (isLocalizationUsed) purposes.add("0")

        val tempPartners = partnerRecyclerViewList ?: PartnersViewModel.configPartnersList()
        for (partner in tempPartners) {
            partners.add(PartnerRequest(partner.key, partner.state))
        }

        val predicioApi: NetApi = NetService.getNetApi(aaidEncrypted)
        compositeDisposable.add(
            predicioApi.setConsentData(
                apiKey, AAID,
                NetService.getUrlencodedApi(purposes.toList(), partners.toList())
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<GetSetContentDataResponse>() {
                    override fun onComplete() {}
                    override fun onNext(t: GetSetContentDataResponse) {
                        Log.d(Constants.TAG, "Consent data set successfully")
                    }

                    override fun onError(e: Throwable) {
                        Log.e(Constants.TAG, e.toString())
                    }
                })
        )
    }
}

