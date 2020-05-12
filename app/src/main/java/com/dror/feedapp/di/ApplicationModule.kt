package com.dror.feedapp.di

import com.dror.feedapp.api.APIRepository
import com.dror.feedapp.api.APIService
import dagger.Module
import dagger.Provides
import okhttp3.*
import javax.inject.Singleton


@Module
class ApplicationModule {
    @Provides
    @Singleton
    fun provideAPIService(okHttpClient: OkHttpClient): APIService {
        return APIService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideAPIRepository(apiService: APIService): APIRepository {
        return APIRepository(apiService)
    }
}