package io.predic.cmp_sdk.api

import io.predic.cmp_sdk.api.responses.GetConsentDataResponse
import io.predic.cmp_sdk.api.responses.GetPartnersResponse
import io.predic.cmp_sdk.api.responses.GetSetContentDataResponse
import io.predic.cmp_sdk.api.responses.Localization
import io.predic.cmp_sdk.models.Location
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetApi {

    @GET("/getconfig/pubvendors")
    fun getLocationInfo() : Observable<Location>

    @GET("/cmp/get/{app_key}/{AAID}")
    fun getConsentData(@Path("app_key") appKey: String,
                       @Path("AAID") AAID: String) : Observable<GetConsentDataResponse>

    @GET("/cmp/list/{app_key}")
    fun getPartnersData(@Path("app_key") appKey: String) : Observable<GetPartnersResponse>

    @POST("/cmp/save/{app_key}/{AAID}")
    fun setConsentData(@Path("app_key") appKey: String,
                       @Path("AAID") AAID: String,
                       @Body setConsentDataRequest: RequestBody) : Observable<GetSetContentDataResponse>

    @GET("/{locale}.json")
    fun getLocalization(@Path("locale") locale: String): Observable<Localization>
}