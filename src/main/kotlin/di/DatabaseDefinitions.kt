package di

import com.kotlindiscord.kord.extensions.utils.env
import data.repository.GlobalGuildConfig
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import utils.Environment

fun provideMongoDatabase(): CoroutineDatabase {
    val url = env(Environment.MONGO_URL)
    val userName = env(Environment.MONGO_USERNAME)
    val password = env(Environment.MONGO_PASSWORD)
    val queryParams = "retryWrites=true&w=majority"
    val databaseName = if (env(Environment.IS_DEBUG).toBoolean()) "Troy_Dev" else "Troy_Prod"
    val connectionString = "mongodb+srv://$userName:$password@$url/$databaseName?$queryParams"
    return KMongo.createClient(connectionString).coroutine.getDatabase(databaseName)
}

fun provideGlobalGuildCollection(database: CoroutineDatabase): CoroutineCollection<GlobalGuildConfig> {
    return database.getCollection()
}
