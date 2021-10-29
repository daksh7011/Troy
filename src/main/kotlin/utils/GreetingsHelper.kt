package utils

import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import models.OpenWeatherModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.logger.Level
import utils.Extensions.getTestGuildSnowflake
import utils.Extensions.requestAndCatch
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

// TODO: 28-Oct-21 Add dynamic subscription for this via bot command for particular guilds.
object GreetingsHelper : KoinComponent {

    private val kordClient: Kord by inject()

    private val httpClient = HttpClient {
        install(JsonFeature) {
            serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })
        }
    }
    @OptIn(ExperimentalTime::class)
     suspend fun setupGreetings(){
        Scheduler().schedule(
            delay = kotlin.time.Duration.hours(6),
            callback = {
                setupGreetingsForTechnoTrojans()
            },
        )
    }

    private suspend fun setupGreetingsForTechnoTrojans() {
        val ayodhyaWeatherUrl =
            "https://api.openweathermap.org/data/2.5/weather?q=Ajodhya&appid=${env(Environment.OPEN_WEATHER_API_KEY)}"
        var ayodhyaWeather: OpenWeatherModel? = null
        ayodhyaWeather = requestForWeather(ayodhyaWeather, ayodhyaWeatherUrl)
        ayodhyaWeather?.let {
            scheduleGreetings(it)
        }
    }

    private suspend fun requestForWeather(
        ayodhyaWeather: OpenWeatherModel?,
        ayodhyaWeatherUrl: String
    ): OpenWeatherModel? {
        var ayodhyaWeather1 = ayodhyaWeather
        httpClient.requestAndCatch({
            ayodhyaWeather1 = get<OpenWeatherModel>(ayodhyaWeatherUrl)
        }, {
            getKoin().logger.log(Level.ERROR, localizedMessage)
        })
        return ayodhyaWeather1
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun scheduleGreetings(ayodhyaWeather: OpenWeatherModel) {
        val technoTrojansGuild =
            kordClient.guilds.filter { it.id == getTestGuildSnowflake() }.first().asGuildOrNull()
        val chitChatsChannel =
            technoTrojansGuild.channels.filter { it.name == "chit-chats" }.first().asChannel()
        val channelBehaviour = MessageChannelBehavior(chitChatsChannel.id, kordClient)
        ayodhyaWeather.let {
            val morningDate =
                Instant.ofEpochMilli(it.sys.sunrise.toLong()).atZone(ZoneId.of("UTC")).toLocalDateTime()
            val morningDateDelay = Duration.between(LocalDateTime.now(), morningDate)
            val nightDate =
                Instant.ofEpochMilli(it.sys.sunset.toLong()).atZone(ZoneId.of("UTC")).toLocalDateTime()
            val nightDateDelay = Duration.between(LocalDateTime.now(), nightDate)
            Scheduler().schedule(
                delay = morningDateDelay.toKotlinDuration(),
                callback = {
                    channelBehaviour.createMessage("Good Morning! Jay Shree Ram")
                },
            )
            Scheduler().schedule(
                delay = nightDateDelay.toKotlinDuration(),
                callback = {
                    channelBehaviour.createMessage("Good Night! Jay Shree Ram")
                },
            )
        }
    }
}
