package commands.mod

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.utils.dm
import dev.kord.core.Kord
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import org.koin.core.component.inject
import utils.Extensions.isOwner

class Reboot : Extension() {

    private val kordClient: Kord by inject()

    override val name: String
        get() = "reboot"

    override suspend fun setup() {
        chatCommand {
            name = "reboot"
            description = "Reboots the bot."
            action {
                if (message.author?.id?.isOwner()?.not() != false) {
                    message.channel.createMessage("This is owner only command.")
                } else {
                    message.addReaction(Emojis.wave.toReaction())
                    message.channel.createMessage("Commencing reboot.")
                    message.author?.dm(
                        "Commencing restart process!\n" +
                                "**Beware, If I am not running via pm2, You need to do start the process manually.**"
                    )
                    kordClient.logout()
                    kordClient.shutdown()
                }
            }
        }
    }
}
