package com.immotef.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.immotef.authorization.AuthorizationProvider
import com.immotef.authorization.LoggedState
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

/**
 *
 */

private const val KEY_AUTHORIZATION = "Authorization"

private const val TIMEOUT = 60L


fun networkingModule(url: String, isDebug: Boolean, header: String = KEY_AUTHORIZATION) = module {

    single { HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY } }

    single {
        val interceptor = Interceptor { chain ->
            val preferences = get<AuthorizationProvider>()

            val request = chain.request()

            val auth = request.newBuilder().let {
                runBlocking {
                    if (preferences.isLogged() == LoggedState) {
                        it.addHeader(header, preferences.provideToken() ?: "")
                    }
                }
                it.build()
            }
            chain.proceed(auth)
        }
        interceptor
    }

    single {
        OkHttpClient.Builder().apply {
                if (isDebug) {
                    addInterceptor(get<HttpLoggingInterceptor>())
                }
                addInterceptor(get())
            }.writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    single<Gson> {
        GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateDeserializer(listOf("yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd'T'HH:mm:ss")))
            .setLenient()
            .create()
    }
    single {
        GsonConverterFactory.create(
            get<Gson>()
        )
    }

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(url)
            .client(get())
            .addConverterFactory(get<GsonConverterFactory>())
            .build()
    }
}

inline fun <reified T> Scope.getApi(): T = get<Retrofit>().create(T::class.java)

