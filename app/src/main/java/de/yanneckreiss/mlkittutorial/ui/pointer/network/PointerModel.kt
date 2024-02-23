package de.yanneckreiss.mlkittutorial.ui.pointer.network

import com.squareup.moshi.Json

data class PointerModel(
    @field:Json(name = "statusCode") val statusCode: String? = null,
    @field:Json(name = "resultMsg") val resultMsg: String? = null,
    @field:Json(name = "resultData") val resultData: String? = null
)
