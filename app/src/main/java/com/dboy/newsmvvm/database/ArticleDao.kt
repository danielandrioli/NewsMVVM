package com.dboy.newsmvvm.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dboy.newsmvvm.api.response.Article

@Dao
interface ArticleDao {

    @Query("SELECT * FROM article")
    fun getAllArticles() : LiveData<List<Article>>  //Função de query não precisa ser suspend. E o LiveData não funciona com suspend.

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertArticle(article: Article) : Long //retorna o ID

    @Delete
    suspend fun deleteArticle(article: Article)
}