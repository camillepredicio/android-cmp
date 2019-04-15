package io.predic.cmp_sdk.api

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


const val LOCALE_BASE_URL = "https://vendorlist.consensu.org"
const val EU_BASE_URL = "http://adservice.google.com"

internal class NetService {

    companion object {
        fun getNetApi(md5Id: String): NetApi {
            return getApi("https://$md5Id.trkr.predic.io")
        }

        fun getLocalizationApi(): NetApi {
            return getApi(LOCALE_BASE_URL)
        }

        fun getLocationApi(): NetApi {
            return getApi(EU_BASE_URL)
        }

        private fun getApi(url: String): NetApi {
            val httpClient = OkHttpClient.Builder()

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
                level = HttpLoggingInterceptor.Level.BODY
            }

            httpClient.addNetworkInterceptor(logging)
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder().apply {
                    header("Content-Type", "application/json;charset=utf-8")
                    method(original.method(), original.body())
                }

                chain.proceed(request.build())
            }
            httpClient.addInterceptor(logging)

            httpClient.readTimeout(40, TimeUnit.SECONDS)

            val gson = Gson()

            return Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build().create(NetApi::class.java)
        }

        fun getUrlencodedApi(purposes: List<String>, partners: List<PartnerRequest>): RequestBody {
            val purpose = Gson().toJson(purposes)
            val partner = Gson().toJson(partners)
            return FormBody.Builder()
                .add("purposes", purpose)
                .add("partners", partner)
                .build()
        }
    }
}