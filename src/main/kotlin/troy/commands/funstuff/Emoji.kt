package troy.commands.funstuff

import dev.kordex.core.commands.Arguments
import dev.kordex.core.commands.converters.impl.coalescingString
import dev.kordex.core.extensions.Extension
import dev.kordex.core.extensions.publicSlashCommand
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class Emoji : Extension() {

    override val name: String
        get() = "emoji"

    inner class EmojiArguments : Arguments() {
        val emoji by coalescingString {
            name = "emoji-name"
            description = "Which emoji would you like me to send? PS: Animated emoji are supported."
        }
    }

    override suspend fun setup() {
        publicSlashCommand(::EmojiArguments) {
            name = "emoji"
            description = "Sends server custom emoji, Also supports animated emojis."
            action {
                if (
                    guild?.emojis?.filter { it.name == arguments.emoji }?.count() != 0
                ) {
                    val emoji = guild?.emojis?.filter {
                        it.name == arguments.emoji
                    }?.first()
                    if (emoji != null) {
                        respond { content = emoji.mention }
                    }
                } else {
                    respond { content = "Can't find that emoji on **${guild?.asGuild()?.name}**" }
                }
            }
        }
    }
}