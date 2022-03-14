package com.dboy.newsmvvm.database

import androidx.room.TypeConverter
import com.dboy.newsmvvm.api.response.Source

class Converters {

    @TypeConverter  //função para salvar um source no db
    fun sourceToString(source: Source): String {
        return source.name
    }

    @TypeConverter
    fun stringToSource(name: String): Source {
        return Source(null, name)
    }
}