package utils

import apiModels.OpenWeatherModel
import com.kotlindiscord.kord.extensions.utils.env
import com.kotlindiscord.kord.extensions.utils.scheduling.Scheduler
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.MessageChannelBehavior
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.logger.Level
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.hours
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

    suspend fun scheduleRecurringGreetingsCall() {
        Scheduler().schedule(
            6.hours,
            callback = {
                pollForAyodhyaWeather()
                scheduleRecurringGreetingsCall()
            },
            name = "Greetings Scheduler"
        )
    }

    private suspend fun pollForAyodhyaWeather() {
        val ayodhyaWeatherUrl =
            "https://api.openweathermap.org/data/2.5/weather?q=Ajodhya&appid=${env(Environment.OPEN_WEATHER_API_KEY)}"
        val ayodhyaWeather = requestForWeather(ayodhyaWeatherUrl)
        ayodhyaWeather?.let {
            scheduleGreetings(it)
        }
    }

    private suspend fun requestForWeather(
        ayodhyaWeatherUrl: String
    ): OpenWeatherModel? {
        var ayodhyaWeather: OpenWeatherModel? = null
        httpClient.requestAndCatch({
            ayodhyaWeather = get<OpenWeatherModel>(ayodhyaWeatherUrl)
        }, {
            getKoin().logger.log(Level.ERROR, localizedMessage)
        })
        return ayodhyaWeather
    }

    private suspend fun scheduleGreetings(ayodhyaWeather: OpenWeatherModel) {
        val technoTrojansGuild =
            kordClient.guilds.filter { it.id == getTestGuildSnowflake() }.first().asGuildOrNull()
        val chitChatsChannel =
            technoTrojansGuild.channels.filter { it.name == "bot-spam" }.first().asChannel()
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
                name = "Morning Greetings",
            )
            Scheduler().schedule(
                delay = nightDateDelay.toKotlinDuration(),
                callback = {
                    channelBehaviour.createMessage("Good Night! Jay Shree Ram")
                },
                name = "Evening Greetings"
            )
        }
    }
}
