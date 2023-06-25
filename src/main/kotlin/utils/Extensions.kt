package utils

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.application.slash.PublicSlashCommandContext
import com.kotlindiscord.kord.extensions.components.forms.ModalForm
import com.kotlindiscord.kord.extensions.types.respond
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.Message
import dev.kord.rest.builder.message.EmbedBuilder
import `in`.technowolf.linksDetekt.detector.LinksDetektor
import `in`.technowolf.linksDetekt.detector.LinksDetektorOptions
import io.getunleash.DefaultUnleash
import io.getunleash.Unleash
import io.getunleash.util.UnleashConfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException

fun Message.isBot(): Boolean = author?.isBot ?: true
fun Message.isNotBot(): Boolean = isBot().not()
fun Message.containsF(): Boolean = content.lowercase() == "f"
fun Message.containsNigga(): Boolean = content.lowercase().contains("nigga")
fun Message.containsTableFlip(): Boolean = content.lowercase().contains("(╯°□°）╯︵ ┻━┻")
fun Message.containsBs(): Boolean = checkContainsMultiple("bullshit", "BullSheet", "bull-shit")
fun Snowflake.isOwner(): Boolean = toString() == env(Environment.OWNER_ID)
fun Snowflake.isGirlfriend(): Boolean = toString() == env(Environment.GIRLFRIEND_ID)

fun Message.checkContainsMultiple(vararg check: String): Boolean {
    val message = this.content.lowercase()
    return check.toList().any { it.contains(message) }
}

suspend fun <T> HttpClient.requestAndCatch(
    block: suspend HttpClient.() -> T,
    errorHandler: suspend ResponseException.() -> T
): T = runCatching { block() }
    .getOrElse {
        when (it) {
            is ResponseException -> it.errorHandler()
            else -> throw it
        }
    }

suspend fun Message.getEmbedFooter(): EmbedBuilder.Footer {
    val footer = EmbedBuilder.Footer()
    footer.text = "Powered by ${this.kord.getUser(this.kord.selfId)?.username}"
    footer.icon = this.kord.getUser(this.kord.selfId)?.avatar?.cdnUrl?.toUrl()
    return footer
}

suspend fun Kord.getEmbedFooter(): EmbedBuilder.Footer {
    val footer = EmbedBuilder.Footer()
    footer.text = "Powered by ${this.getUser(this.selfId)?.username}"
    footer.icon = this.getUser(this.selfId)?.avatar?.cdnUrl?.toUrl()
    return footer
}

fun getTestGuildSnowflake(): Snowflake {
    return Snowflake(
        env(Environment.TEST_GUILD_ID).toLong(),
    )
}

fun provideUnleashClient(): Unleash? {
    if (env(Environment.IS_DEBUG).toBoolean().not()) {
        val config = UnleashConfig.builder()
            .appName("Troy")
            .instanceId(env(Environment.UNLEASH_INSTANCE_ID))
            .unleashAPI(env(Environment.UNLEASH_URL))
            .build()
        return DefaultUnleash(config)
    }
    return null
}

fun String?.bold(): String {
    return "**$this**"
}

fun String?.italic(): String {
    return "*$this*"
}

fun String.isEmptyOrBlank(): Boolean {
    return this.isBlank() || this.isEmpty()
}

fun Int?.orZero(): Int = this ?: 0

suspend fun <T : Arguments, M : ModalForm> PublicSlashCommandContext<T, M>.respond(text: String) = this.respond { content = text }

fun String.extractLinksFromMessage(): List<String?> {
    val linksWithDefaultOption = LinksDetektor(this, LinksDetektorOptions.Default).detect().map { it.host }
    val linksWithBracketMatch = LinksDetektor(this, LinksDetektorOptions.BRACKET_MATCH).detect().map { it.host }
    val linksWithQuoteMatch = LinksDetektor(this, LinksDetektorOptions.QUOTE_MATCH).detect().map { it.host }
    val linksWithSingleQuoteMatch =
        LinksDetektor(this, LinksDetektorOptions.SINGLE_QUOTE_MATCH).detect().map { it.host }
    val linksWithJsonMatch = LinksDetektor(this, LinksDetektorOptions.JSON).detect().map { it.host }
    val linksWithJavascriptMatch = LinksDetektor(this, LinksDetektorOptions.JAVASCRIPT).detect().map { it.host }
    val linksWithXmlMatch = LinksDetektor(this, LinksDetektorOptions.XML).detect().map { it.host }
    val linksWithHtmlMatch = LinksDetektor(this, LinksDetektorOptions.HTML).detect().map { it.host }

    val finalList = mutableListOf<String?>().apply {
        addAll(linksWithDefaultOption)
        addAll(linksWithBracketMatch)
        addAll(linksWithQuoteMatch)
        addAll(linksWithSingleQuoteMatch)
        addAll(linksWithJsonMatch)
        addAll(linksWithJavascriptMatch)
        addAll(linksWithXmlMatch)
        addAll(linksWithHtmlMatch)
    }.distinct()

    return finalList
}
