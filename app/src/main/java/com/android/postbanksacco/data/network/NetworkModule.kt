package com.android.postbanksacco.data.network

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.android.postbanksacco.BuildConfig
import com.android.postbanksacco.utils.Constants
import com.android.postbanksacco.utils.tagTracker
import okhttp3.CipherSuite
import okhttp3.ConnectionSpec
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.Collections
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * 1. @Module Annotation:
 * @Module annotation is used to create a special class that helps Dagger-Hilt understand how to create and provide instances of different objects
 * Modules are responsible for providing dependencies(instances of objects) that can be injected into other classes.
 * In the module class, you define methods that provide the dependencies.
 *
 * 2. @InstallIn(SingletonComponent::class) Annotation:
 *  @InstallIn is used to specify which Dagger-Hilt component this module should be installed in. In this case, we installl NetworkModule in
 *  the  SingletonComponent,which means that this module will be available for injection throughout the entire application as the
 *  SingletonComponent is a Dagger Hilt component that has a lifecycle tied to the application.
 *
 * 3. The @Singleton annotation is used to tell Dagger-Hilt to only create a single instance of the specific object,eg a single instance of Retrofit object
 *
 * 4. @Provides
 * @Provides annotation is used tell Dagger-Hilt that this method annotated with with @Provides provides an instance of the object eg: Retrofit
 *
 * */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideBaseUrl():String = Constants.baseUrl

    @Singleton
    @Provides
    fun provideOkHttpClient(spec: ConnectionSpec, liveNetworkMonitor: NetworkMonitor, @ApplicationContext appContext: Context) =

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor { message -> Log.e(tagTracker,message) }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .hostnameVerifier { _, _ -> true }
                .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor(loggingInterceptor)
                .addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor))
                .addInterceptor{chain: Interceptor.Chain ->
                    val originalRequest = chain.request()
                    val requestBuilder = originalRequest.newBuilder()
                        .header("Content-Type","application/json")
                        .addHeader("Accept", "application/json")
                        .header("Connection", "close")
                    val request = requestBuilder.build()
                    Log.e(tagTracker,"Request Headers:::: ${request.headers}")
                    chain.proceed(request)
                }
                .build()
        } else {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                 .connectionSpecs(Collections.singletonList(spec))
                .addInterceptor(NetworkMonitorInterceptor(liveNetworkMonitor))
                .addInterceptor{chain: Interceptor.Chain ->
                    val originalRequest = chain.request()
                    val requestBuilder = originalRequest.newBuilder()
                        .addHeader("Accept", "application/json")
                        .header("Content-Type","application/json")
                        .header("Connection", "close")
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()
        }


    @Provides
    fun provideNetworkMonitor(
        @ApplicationContext appContext: Context
    ): NetworkMonitor {
        return LiveNetworkMonitor(appContext)
    }

    @Singleton
    @Provides
    fun provideConnectionSpec(): ConnectionSpec {
        return ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_2)
            .cipherSuites(
                CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
            )
            .build()
    }


    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, BaseURL: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BaseURL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }



}