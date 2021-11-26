package models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoodsModelItem(
    @SerialName("categoryName")
    val categoryName: List<String>,
    @SerialName("fullName")
    val fullName: String,
    @SerialName("value")
    val value: List<String>
)
