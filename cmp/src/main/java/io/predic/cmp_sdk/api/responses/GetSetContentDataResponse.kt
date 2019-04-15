package io.predic.cmp_sdk.api.responses

class GetSetContentDataResponse(status: Status) : BaseResponse<Status>(status)

data class Status(val status: String)