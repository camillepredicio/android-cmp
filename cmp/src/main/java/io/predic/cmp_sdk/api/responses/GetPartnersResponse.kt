package io.predic.cmp_sdk.api.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetPartnersResponse {

    @SerializedName("state")
    @Expose
    var state: String? = null
    @SerializedName("partners")
    @Expose
    var partners: List<PartnerList>? = null

}

class PartnerList {

    @SerializedName("key")
    @Expose
    var key: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("text")
    @Expose
    var text: String? = null

}