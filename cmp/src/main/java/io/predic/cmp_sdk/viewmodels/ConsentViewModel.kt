package io.predic.cmp_sdk.viewmodels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.util.Log
import android.view.View
import io.predic.cmp_sdk.R
import io.predic.cmp_sdk.activities.ConsentActivity.Companion.consentResources
import io.predic.cmp_sdk.api.NetApi
import io.predic.cmp_sdk.api.NetService
import io.predic.cmp_sdk.api.PartnerRequest
import io.predic.cmp_sdk.api.responses.GetPartnersResponse
import io.predic.cmp_sdk.api.responses.GetSetContentDataResponse
import io.predic.cmp_sdk.api.responses.PartnerList
import io.predic.cmp_sdk.utils.CMPApp.Companion.appContext
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.utils.Encryption
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

internal class ConsentViewModel : ViewModel() {

    internal lateinit var apiKey: String
    private var partnersList = listOf<PartnerRequest>()

    val openSettings: MutableLiveData<Constants.OpenSettingsStatus> by lazy {
        MutableLiveData<Constants.OpenSettingsStatus>()
    }
    val openPredicio: MutableLiveData<Constants.OpenPredicioStatus> by lazy {
        MutableLiveData<Constants.OpenPredicioStatus>()
    }
    val closeConsent: MutableLiveData<Constants.CloseConsentStatus> by lazy {
        MutableLiveData<Constants.CloseConsentStatus>()
    }
    private var compositeDisposable = CompositeDisposable()

    val titleText = consentResources.getString(R.string.about_personal_data)
    var titleTextSize = appContext.consentTitleTextSize ?: 20
    var titleTextColor = appContext.consentTitleTextColor ?: consentResources.getColor(R.color.blackColor)

    var consentText = consentResources.getString(R.string.privacyPolicyText)
    var consentTextSize = appContext.consentDescriptionTextSize ?: 14
    var consentTextColor = appContext.consentDescriptionTextColor ?: consentResources.getColor(R.color.blackColor)

    val acceptButtonTitle = consentResources.getString(R.string.accept_all)
    var acceptButtonTextColor = appContext.consentAcceptButtonTextColor ?: consentResources.getColor(R.color.whiteColor)
    var acceptButtonBackground =
        consentResources.getDrawable(appContext.consentAcceptButtonBackground ?: R.drawable.rounded_button)

    val declineButtonTitle = consentResources.getString(R.string.no_thanks)
    var declineButtonTextColor =
        appContext.consentDeclineButtonTextColor ?: consentResources.getColor(R.color.whiteColor)
    var declineButtonBackground =
        consentResources.getDrawable(appContext.consentDeclineButtonBackground ?: R.drawable.rounded_button)
    var declineButtonVisibility = Constants.Visibility.Visible.status

    val settingsButtonTitle = consentResources.getString(R.string.privacy_settings)
    var settingsButtonTextColor =
        appContext.consentSettingsButtonTextColor ?: consentResources.getColor(R.color.buttonColor)
    var settingsBackground =
        consentResources.getDrawable(appContext.consentSettingsButtonBackground ?: R.drawable.border_button)

    var settingsButtonVisibility =
        appContext.consentSettingsButtonVisibility?.status ?: Constants.Visibility.Visible.status
    var predicioLogoVisibility = appContext.predicioLogoVisibility?.status ?: Constants.Visibility.Visible.status
    var backgroundColor = appContext.consentBackgroundColor ?: consentResources.getColor(R.color.whiteColor)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    /**
     * Called when Decline button clicked.
     */
    fun onDeclineButtonClick() {
        configPartnersList(false)
        Handler().postDelayed({
            requestSetConsentData(appContext.aaid ?: "", false)
            closeConsent.value = Constants.CloseConsentStatus.CloseConsent
        }, 1000)
    }

    /**
     * Called when Accept button clicked.
     */
    fun onAcceptButtonClick() {
        configPartnersList(true)
        Handler().postDelayed({
            requestSetConsentData(appContext.aaid ?: "", true)
            closeConsent.value = Constants.CloseConsentStatus.CloseConsent
        }, 1000)
    }

    /**
     * Called when Privacy Settings button clicked.
     * @param view Button
     */
    fun onPrivacySettingsButtonClick(view: View) {
        openSettings.value = Constants.OpenSettingsStatus.OpenSettings
    }

    /**
     * Called when image with Predicio logo clicked.
     * @param view ImageView
     */
    fun onPredicioLogoClick(view: View) {
        openPredicio.value = Constants.OpenPredicioStatus.OpenPredicio
    }

    /**
     * Requests to save consent data.
     * @param AAID Advertising ID.
     */
    private fun requestSetConsentData(AAID: String, agreement: Boolean) {
        val md5Value = Encryption.getMD5(AAID)
        val aaidEncrypted = if (md5Value != null) md5Value else {
            Log.e(Constants.TAG, "MD5 AAID unwrap failed")
            return
        }

        val purposes = if (agreement) listOf("1", "1", "1", "1", "1", "1") else listOf("0", "0", "0", "0", "0", "0")

        val predicioApi: NetApi = NetService.getNetApi(aaidEncrypted)
        compositeDisposable.add(
            predicioApi.setConsentData(
                apiKey, AAID,
                NetService.getUrlencodedApi(purposes, partnersList)
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


    /**
     * Request used to get list of partners.
     *
     * @param block Callback with partners list.
     */
    private fun requestPartnersData(block: (data: List<PartnerList>?) -> List<PartnerRequest>) {
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
        compositeDisposable.add(api.getPartnersData(appContext.appKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<GetPartnersResponse>() {
                override fun onComplete() {}
                override fun onNext(partners: GetPartnersResponse) {
                    partnersList = block(partners.partners)
                }

                override fun onError(e: Throwable) {
                    Log.e(Constants.TAG, e.toString())
                }
            })
        )
    }

    /**
     * Configures RecyclerView with partners from server.
     *
     * @return List of partners.
     */
    private fun configPartnersList(state: Boolean = false) {
        val list = mutableListOf<PartnerRequest>()
        requestPartnersData { listOfPartners ->
            listOfPartners?.forEach { partner ->
                list.add(PartnerRequest(partner.key ?: "", state))
            }
            return@requestPartnersData list
        }
    }

    /**
     * Handles response for location status. Need to find out that user inside/outside of EU.
     */
    internal fun handleLocationStatus(status: Constants.EUStatus) {
        when (status) {
            Constants.EUStatus.Inside -> consentText = consentResources.getString(R.string.privacyPolicyText)
            Constants.EUStatus.Outside -> {
                consentText = consentResources.getString(R.string.privacyPolicyTextLight)
                settingsButtonVisibility = Constants.Visibility.Gone.status
                declineButtonVisibility = Constants.Visibility.Gone.status
            }
        }
    }

    companion object {
        var privacyPolicyHyperlink: String? = null
        var privacyPolicyHyperlinkColor: Int? = null
        val checkEUStatus: MutableLiveData<Constants.EUStatus> by lazy {
            MutableLiveData<Constants.EUStatus>()
        }
    }
}

