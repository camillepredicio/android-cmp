package io.predic.cmp_sdk.api.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetConsentDataResponse {

    @SerializedName("state")
    @Expose
    var state: String? = null
    @SerializedName("consent")
    @Expose
    var consent: Consent? = null

}

class Consent {

    @SerializedName("date")
    @Expose
    var date: String? = null
    @SerializedName("purposes")
    @Expose
    var purposes: List<String>? = null
    @SerializedName("partners")
    @Expose
    var partners: List<Partner>? = null

}

class Partner {

    @SerializedName("key")
    @Expose
    var key: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("privacy_url")
    @Expose
    var privacyUrl: String? = null
    @SerializedName("state")
    @Expose
    var state: Boolean? = null
    @SerializedName("partners_url")
    @Expose
    var partnersUrl: String? = null
}



