package com.andreacioci.weatherwidget.data.remote

import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Converter.Factory minimale per kotlinx.serialization: mappa esplicitamente i due soli
 * tipi di risposta usati dalle API Open-Meteo, senza dipendere da una libreria bridge di
 * terze parti (né dalla reflection su KType che servirebbe per un converter generico).
 */
class SimpleJsonConverterFactory(private val json: Json) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *>? {
        val serializer = when (type) {
            OpenMeteoForecastResponse::class.java -> OpenMeteoForecastResponse.serializer()
            GeocodingResponse::class.java -> GeocodingResponse.serializer()
            else -> return null
        }
        return Converter<ResponseBody, Any> { body -> json.decodeFromString(serializer, body.string()) }
    }
}
