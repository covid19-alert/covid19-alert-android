package com.immotef.network

import com.google.gson.*
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 *
 */
class DateDeserializer(listOfFormats: List<String>) : JsonDeserializer<Date?>, JsonSerializer<Date> {

    private val listOfSimpleDateFormats = listOfFormats.map {
        SimpleDateFormat(it).apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
        return try {
            val j = json.asJsonPrimitive.asString
            parseDate(j)
        } catch (e: ParseException) {
            throw JsonParseException(e.message, e)
        }
    }

    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(listOfSimpleDateFormats[0].format(src));
    }

    @Throws(ParseException::class)
    private fun parseDate(dateString: String?): Date? {
        return if (dateString != null && dateString.trim { it <= ' ' }.isNotEmpty()) {
            listOfSimpleDateFormats.forEach {
                try {
                    return it.parse(dateString)
                } catch (pe: ParseException) {

                }
            }
            return null
        } else {
            null
        }
    }
}