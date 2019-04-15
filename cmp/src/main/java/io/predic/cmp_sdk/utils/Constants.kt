/*
 *
 * Created by Vadym Osovets on 2/25/19 10:31 AM
 * Copyright (c) 2019 Vadym Osovets . All rights reserved.
 */

package io.predic.cmp_sdk.utils

import android.view.View

internal class Constants {
    companion object {
        const val TAG = "PREDICIO"
        const val HIDE_TAG = "HIDE"
        const val APP_KEY_EXTRA = "appKey"
        const val USER_AAID = "aaid"
        const val TITLE_TEXT_SIZE = "consentTitleTextSize"
        const val DESCRIPTION_TEXT_SIZE = "consentDescriptionTextSize"
        const val PARTNER_NAME_TEXT_SIZE = "partnerNameTextSize"
        const val PARTNER_DESCRIPTION_TEXT_SIZE = "partnerDescriptionTextSize"
        const val TITLE_TEXT_COLOR = "consentTitleTextColor"
        const val DESCRIPTION_TEXT_COLOR = "consentDescriptionTextColor"
        const val ACCEPT_BUTTON_TEXT_COLOR = "acceptButtonTextColor"
        const val DECLINE_BUTTON_TEXT_COLOR = "declineButtonTextColor"
        const val SETTINGS_BUTTON_TEXT_COLOR = "settingsButtonTextColor"
        const val PARTNER_NAME_TEXT_COLOR = "partnerNameTextColor"
        const val PARTNER_DESCRIPTION_TEXT_COLOR = "partnerDescriptionTextColor"
        const val ACCEPT_BUTTON_BACKGROUND = "acceptButtonBackground"
        const val DECLINE_BUTTON_BACKGROUND = "declineButtonBackground"
        const val SETTINGS_BUTTON_BACKGROUND = "settingsButtonBackground"
        const val SETTINGS_BUTTON_VISIBILITY = "settingsButtonVisibility"
        const val PREDICIO_LOGO_VISIBILITY = "predicioLogoVisibility"
        const val CONSENT_BACKGROUND_COLOR = "consentBackgroundColor"
    }

    enum class CountryCodes(val code: String, val locale: String) {
        Austria("AT", "purposes-de"),
        Belgium("BE", "purposes-fr"),
        Bulgaria("BG", "purposes-bg"),
        Cyprus("CY", "purposes-el"),
        Czechia("CZ", "purposes-cs"),
        Denmark("DK", "purposes-da"),
        Finland("FI", "purposes-fi"),
        France("FR", "purposes-fr"),
        Germany("DE", "purposes-de"),
        Greece("GR", "purposes-el"),
        Hungary("HU", "purposes-hu"),
        Ireland("IE", "purposes-ga"),
        Italy("IT", "purposes-it"),
        Latvia("LV", "purposes-lv"),
        Lithuania("LT", "purposes-lt"),
        Luxembourg("LU", "purposes-fr"),
        Malta("MT", "purposes-mt"),
        Netherlands("NL", "purposes-nl"),
        Poland("PL", "purposes-pl"),
        Portugal("PT", "purposes-pt"),
        Romania("RO", "purposes-ro"),
        Slovakia("SK", "purposes-sk"),
        Slovenia("SI", "purposes-sl"),
        Spain("ES", "purposes-es"),
        Sweden("SE", "purposes-sv")
    }

    enum class EUStatus {
        Inside, Outside
    }

    enum class OpenSettingsStatus {
        OpenSettings, CloseSettings
    }

    enum class OpenPredicioStatus {
        OpenPredicio, ClosePredicio
    }

    enum class CloseConsentStatus {
        CloseConsent
    }

    enum class Visibility(val status: Int) {
        Visible(View.VISIBLE),
        Gone(View.GONE)
    }

    enum class OpenPartnersStatus {
        OpenPartners
    }

    enum class CloseSettingsStatus {
        CloseSettings
    }

    enum class SwitchStateStatus {
        SwitchChecked, SwitchUnchecked
    }
}

enum class Purposes {
    InformationStorage,
    Personalization,
    AdSelection,
    ContentSelection,
    Measurement,
    LocationBasedAdvertising
}