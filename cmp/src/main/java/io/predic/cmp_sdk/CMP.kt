package io.predic.cmp_sdk

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.orhanobut.hawk.Hawk

import io.predic.cmp_sdk.activities.ConsentActivity
import io.predic.cmp_sdk.api.NetApi
import io.predic.cmp_sdk.api.NetService
import io.predic.cmp_sdk.api.responses.Consent
import io.predic.cmp_sdk.api.responses.GetConsentDataResponse
import io.predic.cmp_sdk.models.Location
import io.predic.cmp_sdk.utils.CMPApp.Companion.appContext
import io.predic.cmp_sdk.utils.Constants
import io.predic.cmp_sdk.utils.Encryption
import io.predic.cmp_sdk.utils.Purposes
import io.predic.cmp_sdk.viewmodels.ConsentViewModel.Companion.checkEUStatus
import io.predic.cmp_sdk.viewmodels.ConsentViewModel.Companion.privacyPolicyHyperlink
import io.predic.cmp_sdk.viewmodels.ConsentViewModel.Companion.privacyPolicyHyperlinkColor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable

class CMP {

    companion object {
        private val instance = CMP()

        fun instance(): CMP {
            return instance
        }
    }

    private var compositeDisposable = CompositeDisposable()

    /**
     * Base initializer for CMP lib. If "checkEU" enabled - GDPR consent sets only for EU users.
     *
     * @param context Base app context.
     * @param apiKey ApiKey used for connection to Predicio server, where stored consent data.
     */
    fun initialize(context: Context, apiKey: String, checkEU: Boolean = true) {
        Hawk.init(context).build()

        // Fetch AAID for server requests
        fetchAAID(context)

        when (checkEU) {
            true -> requestLocationStatus { isUserInEU ->
                when (isUserInEU) {
                    true -> checkEUStatus.value = Constants.EUStatus.Inside
                    false -> checkEUStatus.value = Constants.EUStatus.Outside
                }
                openConsent(context, apiKey)
            }
            false -> {
                checkEUStatus.value = Constants.EUStatus.Inside
                openConsent(context, apiKey)
            }
        }
    }

    /**
     * Initializer for CMP lib with block. Use to handle your own flow, if user is not in EU.
     *
     * @param context Base app context.
     * @param apiKey ApiKey used for connection to Predicio server, where stored consent data.
     * @param block Used for execute another flow, when user is not in EU.
     */
    fun initialize(context: Context, apiKey: String, block: () -> Unit) {
        Hawk.init(context).build()

        // Fetch AAID for server requests
        fetchAAID(context)

        requestLocationStatus { isUserInEU ->
            when (isUserInEU) {
                true -> openConsent(context, apiKey)
                false -> block()
            }
        }
    }

    /**
     * Used to set hyperlink to your Privacy Policy on main screen.
     *
     * @param url Takes url in String.
     * @param color Color of hyperlink.
     */
    fun setPrivacyPolicy(url: String, color: Int = Color.parseColor("#0097FF")) {
        privacyPolicyHyperlink = url
        privacyPolicyHyperlinkColor = color
    }

    /**
     * Used to set icons for purposes. Use Purposes to define what exactly icon you want to change.
     * Takes ID of Drawable from resources in your project.
     *
     * @param purpose Enum with Purposes definition
     * @param id Id of Drawable
     */
    fun setPurposesIcon(purpose: Purposes, id: Int) {
        Hawk.put(purpose.name, id)
    }

    /**
     * Used to hide single purpose. Use Purposes to to define what exactly purpose you want to hide.
     * Takes Boolean value.
     *
     * @param purpose Enum with Purposes definition
     * @param visible Visibility status
     */
    fun setPurposeVisibility(purpose: Purposes, visible: Boolean) {
        Hawk.put(purpose.name + Constants.HIDE_TAG, visible)
    }

    /**
     * Used to get user's consent data.
     *
     * @param context Base app context.
     * @param block Takes actions when result is OK.
     */
    fun getConsent(context: Context, block: (consent: Consent?) -> Unit) {
        try {
            // Fetch AAID for server requests
            fetchAAID(context)
            requestGetConsentData {
                block(it)
            }
        } catch (e: java.lang.Exception) {
            Log.e(Constants.TAG, "User data fetch failed")
            block(null)
        }
    }

    /**
     * Request to handle user location. Define is user in EU or not.
     *
     * @param block Location handler.
     */
    private fun requestLocationStatus(block: (inEU: Boolean) -> Unit) {
        val locationApi: NetApi = NetService.getLocationApi()
        compositeDisposable.add(
            locationApi.getLocationInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : DisposableObserver<Location>() {
                    override fun onComplete() {}
                    override fun onNext(location: Location) {
                        block(location.isRequestInEeaOrUnknown ?: false)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(Constants.TAG, e.toString())
                    }
                })
        )
    }

    /**
     * Used to get users Advertizing ID.
     *
     * @throws Exception: If we can't get users Advertizing ID.
     */
    private fun fetchAAID(context: Context) {
        compositeDisposable.add(
            Observable.fromCallable(object : Callable<String> {
                @Throws(Exception::class)
                override fun call(): String {
                    return AdvertisingIdClient.getAdvertisingIdInfo(context).id
                }
            }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<String>() {
                    override fun onComplete() {}
                    override fun onNext(id: String) {
                        appContext.aaid = id
                    }

                    override fun onError(e: Throwable) {
                        Log.e(Constants.TAG, e.toString())
                    }
                })
        )
    }

    /**
     * Requests for consent data.
     * @param AAID Advertising ID.
     */
    private fun requestGetConsentData(block: (data: Consent?) -> Unit) {

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
        val predicioApi: NetApi = NetService.getNetApi(aaidEncrypted)
        compositeDisposable.add(predicioApi.getConsentData(appContext.appKey, aaid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableObserver<GetConsentDataResponse>() {
                override fun onComplete() {}
                override fun onNext(response: GetConsentDataResponse) {
                    block(response.consent)
                }

                override fun onError(e: Throwable) {
                    Log.e(Constants.TAG, e.toString())
                }
            })
        )
    }

    /**
     * Opens consent activity.
     *
     * @param context Base app context
     * @param apiKey AppKey used for connection to Predicio server, where stored consent data.
     */
    private fun openConsent(context: Context, apiKey: String) {
        // Save appkey
        appContext.appKey = apiKey
        // Open Consent Activity
        val intent = Intent(context, ConsentActivity::class.java)
        context.startActivity(intent)
    }

    /**
     * Property to set text size of Title on Main screen with consent.
     */
    var consentTitleTextSize: Int?
        set(value) {
            appContext.consentTitleTextSize = value
        }
        get() = appContext.consentTitleTextSize

    /**
     * Property to set text color of Title on Main screen with consent.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var consentTitleTextColorInt: Int?
        set(value) {
            appContext.consentTitleTextColor = value
        }
        get() = appContext.consentTitleTextColor

    /**
     * Property to set text color of Title on Main screen with consent.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var consentTitleTextColorString: String? = null
        set(value) {
            appContext.consentTitleTextColor = Color.parseColor(value)
        }

    /**
     * Property to set text size of consent on Main screen.
     */
    var consentDescriptionTextSize: Int?
        set(value) {
            appContext.consentDescriptionTextSize = value
        }
        get() = appContext.consentDescriptionTextSize

    /**
     * Property to set text color of consent on Main screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var consentDescriptionTextColorInt: Int?
        set(value) {
            appContext.consentDescriptionTextColor = value
        }
        get() = appContext.consentDescriptionTextColor

    /**
     * Property to set text color of consent on Main screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var consentDescriptionTextColorString: String? = null
        set(value) {
            appContext.consentDescriptionTextColor = Color.parseColor(value)
        }

    /**
     * Property to set text color of "Accept" button on Main screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var consentAcceptButtonTextColorInt: Int?
        set(value) {
            appContext.consentAcceptButtonTextColor = value
        }
        get() = appContext.consentAcceptButtonTextColor

    /**
     * Property to set text color of "Accept" button on Main screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var consentAcceptButtonTextColorString: String? = null
        set(value) {
            appContext.consentAcceptButtonTextColor = Color.parseColor(value)
        }

    /**
     * Property to set background of "Accept" button on Main screen.
     * It takes only id of Drawables.
     */
    var consentAcceptButtonBackground: Int?
        set(value) {
            appContext.consentAcceptButtonBackground = value
        }
        get() = appContext.consentAcceptButtonBackground

    /**
     * Property to set text color of "Decline" button on Main screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var consentDeclineButtonTextColorInt: Int?
        set(value) {
            appContext.consentDeclineButtonTextColor = value
        }
        get() = appContext.consentDeclineButtonTextColor

    /**
     * Property to set text color of "Decline" button on Main screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var consentDeclineButtonTextColorString: String? = null
        set(value) {
            appContext.consentDeclineButtonTextColor = Color.parseColor(value)
        }

    /**
     * Property to set background of "Decline" button on Main screen.
     * It takes only id of Drawables.
     */
    var consentDeclineButtonBackground: Int?
        set(value) {
            appContext.consentDeclineButtonBackground = value
        }
        get() = appContext.consentDeclineButtonBackground

    /**
     * Property to set text color of "Privacy settings" button on Main screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var consentSettingsButtonTextColorInt: Int?
        set(value) {
            appContext.consentSettingsButtonTextColor = value
        }
        get() = appContext.consentSettingsButtonTextColor

    /**
     * Property to set text color of "Privacy settings" button on Main screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var consentSettingsButtonTextColorString: String? = null
        set(value) {
            appContext.consentSettingsButtonTextColor = Color.parseColor(value)
        }

    /**
     * Property to set text color of partner name on Partners screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var partnersNameTextColorInt: Int?
        set(value) {
            appContext.partnersNameTextColor = value
        }
        get() = appContext.partnersNameTextColor

    /**
     * Property to set text color of partner name on Partners screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var partnersNameTextColorString: String? = null
        set(value) {
            appContext.partnersNameTextColor = Color.parseColor(value)
        }

    /**
     * Property to set text size of partner name on Partners screen.
     */
    var partnersNameTextSize: Int?
        set(value) {
            appContext.partnersNameTextSize = value?.toFloat()
        }
        get() = appContext.partnersNameTextSize?.toInt()

    /**
     * Property to set text color of partner description on Partners screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var partnersDescriptionTextColorInt: Int?
        set(value) {
            appContext.partnersDescriptionTextColor = value
        }
        get() = appContext.partnersDescriptionTextColor

    /**
     * Property to set text color of partner description on Partners screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var partnersDescriptionTextColorString: String? = null
        set(value) {
            appContext.partnersDescriptionTextColor = Color.parseColor(value)
        }

    /**
     * Property to set text size of partner description on Partners screen.
     */
    var partnersDescriptionTextSize: Int?
        set(value) {
            appContext.partnersDescriptionTextSize = value?.toFloat()
        }
        get() = appContext.partnersDescriptionTextSize?.toInt()

    /**
     * Property to set background of "Privacy settings" button on Main screen.
     * It takes only id of Drawables.
     */
    var consentSettingsButtonBackground: Int?
        set(value) {
            appContext.consentSettingsButtonBackground = value
        }
        get() = appContext.consentSettingsButtonBackground

    /**
     * Property to set visibility of "Privacy settings" button on Main screen.
     */
    var consentSettingsButtonVisibility: Boolean?
        set(value) {
            appContext.consentSettingsButtonVisibility =
                if (value != false) Constants.Visibility.Visible else Constants.Visibility.Gone
        }
        get() = appContext.consentSettingsButtonVisibility == Constants.Visibility.Visible

    /**
     * Property to set visibility of "Powered by Predicio" image on Main screen.
     */
    var predicioLogoVisibility: Boolean?
        set(value) {
            appContext.predicioLogoVisibility =
                if (value != false) Constants.Visibility.Visible else Constants.Visibility.Gone
        }
        get() = appContext.predicioLogoVisibility == Constants.Visibility.Visible

    /**
     * Property to set text color of "Privacy settings" button on Main screen.
     * It takes only color described in Int. You can easily set it with color from your resources.
     */
    var consentBackgroundColorInt: Int?
        set(value) {
            appContext.consentBackgroundColor = value
        }
        get() = appContext.consentBackgroundColor

    /**
     * Property to set text color of "Privacy settings" button on Main screen.
     * It takes only color described in String. You can easily set it with any HEX color.
     */
    var consentBackgroundColorString: String? = null
        set(value) {
            appContext.consentBackgroundColor = Color.parseColor(value)
        }
}