package de.yanneckreiss.mlkittutorial.ui.pointer.network

import com.squareup.moshi.Json


//data class PointerModel(
//    @Part val image: MultipartBody.Part
//)

//백엔드에서 받는 데이터 클래스
data class PointerBackendResponse(
    @field:Json(name = "statusCode") val statusCode: String? = null,
    @field:Json(name = "resultMsg") val resultMsg: String? = null,
    @field:Json(name = "resultData") val resultData: String? = null
)