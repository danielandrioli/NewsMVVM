package com.dboy.newsmvvm.di

import android.content.Context
import androidx.room.Room
import com.dboy.newsmvvm.api.BASE_URL
import com.dboy.newsmvvm.api.NewsApi
import com.dboy.newsmvvm.database.ArticleDao
import com.dboy.newsmvvm.database.NewsDatabase
import com.dboy.newsmvvm.repositories.DefaultNewsRepository
import com.dboy.newsmvvm.repositories.NewsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val logging =
            HttpLoggingInterceptor()  //Essa instância veio da biblioteca do retrofit logging-interceptor
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(logging).build()
        //Esse Logging e o client não são obrigatórios. Servem apenas para criar logs da retrofit e ajudar no debugging.
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun providesNewsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): NewsDatabase = Room
        .databaseBuilder(context, NewsDatabase::class.java, "article_db.db")
        .build()

    @Singleton //acho que não é necessário o dao ser singleton
    @Provides
    fun providesArticleDao(db: NewsDatabase) = db.getArticleDao()

    @Singleton
    @Provides
    fun providesNewsRepository(articleDao: ArticleDao, newsApi: NewsApi): NewsRepository =
        DefaultNewsRepository(articleDao, newsApi)
    //Se eu tivesse apenas criado uma classe para o repositório (e não uma interface que ajudará no polimorfismo e teste)
    //então não haveria necessidade de criar essa função provides no módulo. Então eu injetaria a dependência diretamente
    // na classe do repositório utilizando o @Inject, e o Hilt aprenderia a como criar esse repositório.
}