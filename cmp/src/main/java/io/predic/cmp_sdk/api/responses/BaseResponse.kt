package io.predic.cmp_sdk.api.responses

import com.google.gson.annotations.SerializedName

//For list data from server
open class BaseListResponse<out D> {
    @SerializedName("data")
    val list: List<D>? = null
    lateinit var status: String
}

// For object from server
open class BaseResponse<out D>(val data: D) {
    lateinit var status: String
}

// For Error from server
class ErrorResponse(@SerializedName("error") val errorData: ErrorData)

class ErrorData {
    @SerializedName("code")
    val errorCode: Int = 0
    @SerializedName("message")
    val errorMessage: List<String>? = null
}