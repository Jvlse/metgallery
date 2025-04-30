package de.example.met_gallery.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.example.met_gallery.network.ArtworkApi
import retrofit2.Retrofit
import de.example.met_gallery.network.ArtworkDataSource
import de.example.met_gallery.network.ArtworkDataSourceImpl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.moshi.MoshiConverterFactory

interface AppContainer {
    val artworkDataSource: ArtworkDataSource
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://collectionapi.metmuseum.org/public/collection/v1/"

    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val retrofitArtworkApi: ArtworkApi by lazy {
        retrofit.create(ArtworkApi::class.java)
    }

    override val artworkDataSource: ArtworkDataSource by lazy {
        ArtworkDataSourceImpl(retrofitArtworkApi)
    }
}
