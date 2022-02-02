package commands.config

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.optionalString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.publicSlashCommand
import data.repository.GlobalGuildRepository
import dev.kord.common.entity.Permission
import org.koin.core.component.inject
import utils.bold
import utils.isEmptyOrBlank
import utils.respond

class InviteLink : Extension() {

    override val name: String
        get() = "invite-link"

    inner class InviteLinkArgument : Arguments() {
        val inviteLink by optionalString(
            "url",
            "Provide Permanent invite link for this server.",
        )
    }

    override suspend fun setup() {
        val globalGuildRepository: GlobalGuildRepository by inject()
        publicSlashCommand(::InviteLinkArgument) {
            name = "invite-link"
            description = "Setup invite link for this server"
            check { hasPermission(Permission.Administrator) }
            guild(395550925340540930)
            action {
                val guildId = guild?.id?.asString.orEmpty()
                val guildName = guild?.asGuild()?.name.bold()
                val doesGuildConfigExist =
                    globalGuildRepository.checkIfConfigExistsForGuild(guildId)
                val wasLinkProvidedInArguments = arguments.inviteLink.isNullOrBlank().not()

                if (wasLinkProvidedInArguments && doesGuildConfigExist) {
                    globalGuildRepository.updateInviteLinkForGuild(guildId, arguments.inviteLink!!)
                    respond("Invite link updated for $guildName")
                } else {
                    globalGuildRepository.getGlobalConfigForGuild(guildId)?.let {
                        if (it.inviteLink.isEmptyOrBlank())
                            respond(
                                "No invite link has been set for $guildName.\n You can set it by executing same " +
                                        "command, but followed by **URL** of the invite link."
                            )
                        else respond("Invite link for $guildName: ${it.inviteLink}")
                    }
                }
            }
        }
    }
}
