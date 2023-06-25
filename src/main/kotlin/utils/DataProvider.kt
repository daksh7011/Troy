package utils

import apiModels.NoodsModel
import kotlinx.serialization.json.Json

object DataProvider {
    fun getBurnData(): List<String> {
        val burnJson = this::class.java.classLoader.getResource("burn.json")?.readText()
        return Json.decodeFromString(burnJson.orEmpty())
    }

    fun getNoodsData(): List<NoodsModel> {
        val noodJson = this::class.java.classLoader.getResource("noods.json")?.readText()
        return Json.decodeFromString(noodJson.orEmpty())
    }
}
