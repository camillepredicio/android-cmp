package io.predic.cmp_sdk.utils

import android.app.Application
import com.orhanobut.hawk.Hawk

internal class CMPApp : Application() {

    companion object {
        val appContext = CMPApp()
    }

    internal var appKey: String
        set(value) {
            Hawk.put(Constants.APP_KEY_EXTRA, value)
        }
        get() = Hawk.get(Constants.APP_KEY_EXTRA)

    internal var aaid: String?
        set(value) {
            Hawk.put(Constants.USER_AAID, value)
        }
        get() = Hawk.get(Constants.USER_AAID)

    internal var consentTitleTextSize: Int?
        set(value) {
            Hawk.put(Constants.TITLE_TEXT_SIZE, value)
        }
        get() = Hawk.get(Constants.TITLE_TEXT_SIZE)

    internal var consentTitleTextColor: Int?
        set(value) {
            Hawk.put(Constants.TITLE_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.TITLE_TEXT_COLOR)

    internal var consentDescriptionTextSize: Int?
        set(value) {
            Hawk.put(Constants.DESCRIPTION_TEXT_SIZE, value)
        }
        get() = Hawk.get(Constants.DESCRIPTION_TEXT_SIZE)

    internal var consentDescriptionTextColor: Int?
        set(value) {
            Hawk.put(Constants.DESCRIPTION_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.DESCRIPTION_TEXT_COLOR)

    internal var consentAcceptButtonTextColor: Int?
        set(value) {
            Hawk.put(Constants.ACCEPT_BUTTON_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.ACCEPT_BUTTON_TEXT_COLOR)

    internal var consentAcceptButtonBackground: Int?
        set(value) {
            Hawk.put(Constants.ACCEPT_BUTTON_BACKGROUND, value)
        }
        get() = Hawk.get(Constants.ACCEPT_BUTTON_BACKGROUND)

    internal var consentDeclineButtonTextColor: Int?
        set(value) {
            Hawk.put(Constants.DECLINE_BUTTON_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.DECLINE_BUTTON_TEXT_COLOR)

    internal var consentDeclineButtonBackground: Int?
        set(value) {
            Hawk.put(Constants.DECLINE_BUTTON_BACKGROUND, value)
        }
        get() = Hawk.get(Constants.DECLINE_BUTTON_BACKGROUND)

    internal var consentSettingsButtonTextColor: Int?
        set(value) {
            Hawk.put(Constants.SETTINGS_BUTTON_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.SETTINGS_BUTTON_TEXT_COLOR)

    internal var consentSettingsButtonBackground: Int?
        set(value) {
            Hawk.put(Constants.SETTINGS_BUTTON_BACKGROUND, value)
        }
        get() = Hawk.get(Constants.SETTINGS_BUTTON_BACKGROUND)

    internal var consentSettingsButtonVisibility: Constants.Visibility?
        set(value) {
            Hawk.put(Constants.SETTINGS_BUTTON_VISIBILITY, value)
        }
        get() = Hawk.get(Constants.SETTINGS_BUTTON_VISIBILITY)

    internal var predicioLogoVisibility: Constants.Visibility?
        set(value) {
            Hawk.put(Constants.PREDICIO_LOGO_VISIBILITY, value)
        }
        get() = Hawk.get(Constants.PREDICIO_LOGO_VISIBILITY)

    internal var consentBackgroundColor: Int?
        set(value) {
            Hawk.put(Constants.CONSENT_BACKGROUND_COLOR, value)
        }
        get() = Hawk.get(Constants.CONSENT_BACKGROUND_COLOR)

    internal var partnersNameTextSize: Float?
        set(value) {
            Hawk.put(Constants.PARTNER_NAME_TEXT_SIZE, value)
        }
        get() = Hawk.get(Constants.PARTNER_NAME_TEXT_SIZE)

    internal var partnersNameTextColor: Int?
        set(value) {
            Hawk.put(Constants.PARTNER_NAME_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.PARTNER_NAME_TEXT_COLOR)

    internal var partnersDescriptionTextSize: Float?
        set(value) {
            Hawk.put(Constants.PARTNER_DESCRIPTION_TEXT_SIZE, value)
        }
        get() = Hawk.get(Constants.PARTNER_DESCRIPTION_TEXT_SIZE)

    internal var partnersDescriptionTextColor: Int?
        set(value) {
            Hawk.put(Constants.PARTNER_DESCRIPTION_TEXT_COLOR, value)
        }
        get() = Hawk.get(Constants.PARTNER_DESCRIPTION_TEXT_COLOR)

    internal val informationStorageIcon: Int?
        get() = Hawk.get(Purposes.InformationStorage.name)

    internal val personalizationIcon: Int?
        get() = Hawk.get(Purposes.Personalization.name)

    internal val adSelectionIcon: Int?
        get() = Hawk.get(Purposes.AdSelection.name)

    internal val contentSelectionIcon: Int?
        get() = Hawk.get(Purposes.ContentSelection.name)

    internal val measurementIcon: Int?
        get() = Hawk.get(Purposes.Measurement.name)

    internal val locationBasedAdvertisingIcon: Int?
        get() = Hawk.get(Purposes.LocationBasedAdvertising.name)

    internal val informationStorageVisibility: Boolean?
        get() = Hawk.get(Purposes.InformationStorage.name + Constants.HIDE_TAG)

    internal val personalizationVisibility: Boolean?
        get() = Hawk.get(Purposes.Personalization.name + Constants.HIDE_TAG)

    internal val adSelectionVisibility: Boolean?
        get() = Hawk.get(Purposes.AdSelection.name + Constants.HIDE_TAG)

    internal val contentSelectionVisibility: Boolean?
        get() = Hawk.get(Purposes.ContentSelection.name + Constants.HIDE_TAG)

    internal val measurementVisibility: Boolean?
        get() = Hawk.get(Purposes.Measurement.name + Constants.HIDE_TAG)

    internal val locationBasedAdvertisingVisibility: Boolean?
        get() = Hawk.get(Purposes.LocationBasedAdvertising.name + Constants.HIDE_TAG)
}