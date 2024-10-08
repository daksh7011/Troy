package troy.commands.funstuff

import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import io.ktor.client.call.body
import io.ktor.client.request.get
import troy.apiModels.PunsModel
import troy.utils.commonLogger
import troy.utils.httpClient
import troy.utils.requestAndCatch

class Pun : Extension() {

    private var punsModel: PunsModel? = null

    override val name: String
        get() = "pun"

    override suspend fun setup() {
        publicSlashCommand {
            name = "pun"
            description = "Sends a pun"
            action {
                val url = "https://icanhazdadjoke.com/"
                httpClient.requestAndCatch(
                    { punsModel = get(url).body() },
                    { commonLogger.error { localizedMessage } },
                )
                if (punsModel != null) {
                    respond { content = punsModel?.joke }
                }
            }
        }
    }
}
