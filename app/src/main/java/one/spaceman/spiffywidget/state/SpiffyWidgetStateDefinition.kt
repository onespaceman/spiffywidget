package one.spaceman.spiffywidget.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

object SpiffyWidgetStateDefinition : GlanceStateDefinition<SpiffyWidgetState> {
    private const val DATA_STORE_FILE_NAME = "spiffyWidgetState"
    private val Context.datastore by dataStore(DATA_STORE_FILE_NAME, SpiffyWidgetStateSerializer)

    override suspend fun getDataStore(
        context: Context, fileKey: String
    ): DataStore<SpiffyWidgetState> = context.datastore

    override fun getLocation(
        context: Context, fileKey: String
    ): File = context.dataStoreFile(DATA_STORE_FILE_NAME)

    object SpiffyWidgetStateSerializer : Serializer<SpiffyWidgetState> {
        override val defaultValue: SpiffyWidgetState = SpiffyWidgetState()

        override suspend fun readFrom(input: InputStream): SpiffyWidgetState = try {
            Json.decodeFromString(
                SpiffyWidgetState.serializer(), input.readBytes().decodeToString()
            )
        } catch (e: Exception) {
            SpiffyWidgetState()
        }

        override suspend fun writeTo(t: SpiffyWidgetState, output: OutputStream) {
            output.use {
                it.write(
                    Json.encodeToString(
                        SpiffyWidgetState.serializer(), t
                    ).encodeToByteArray()
                )
            }
        }
    }
}
