package io.predic.cmp_sdk.api.responses

class Localization(
    val purposes: List<PurposesLocalization>,
    val features: List<FeaturesLocalization>
) : BaseListResponse<Localization>() {
    data class PurposesLocalization(val id: Int, val name: String, val description: String)

    data class FeaturesLocalization(val id: Int, val name: String, val description: String)
}


