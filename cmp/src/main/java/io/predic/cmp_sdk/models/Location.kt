/*
 *
 * Created by Vadym Osovets on 2/24/19 4:37 PM
 * Copyright (c) 2019 Vadym Osovets . All rights reserved.
 */

package io.predic.cmp_sdk.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Location {

    @SerializedName("is_request_in_eea_or_unknown")
    @Expose
    var isRequestInEeaOrUnknown: Boolean? = null

}