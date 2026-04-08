package com.example.myapplication.model.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class RoomConverters {

    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val locationAdapter = moshi.adapter(ApiLocation::class.java)

    private val listType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(listType)

    // ApiLocation <-> String
    @TypeConverter
    fun locationToJson(value: ApiLocation?): String? {
        return value?.let { locationAdapter.toJson(it) }
    }

    @TypeConverter
    fun jsonToLocation(value: String?): ApiLocation? {
        return value?.let { locationAdapter.fromJson(it) }
    }

    // List<String> <-> String
    @TypeConverter
    fun stringListToJson(value: List<String>?): String? {
        return value?.let { stringListAdapter.toJson(it) }
    }

    @TypeConverter
    fun jsonToStringList(value: String?): List<String>? {
        return value?.let { stringListAdapter.fromJson(it) }
    }
}