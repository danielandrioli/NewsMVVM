package com.dboy.newsmvvm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dboy.newsmvvm.api.response.Article

@TypeConverters(Converters::class)
@Database(entities = [Article::class], version = 1)
abstract class NewsDatabase : RoomDatabase(){

    abstract fun getArticleDao(): ArticleDao
}