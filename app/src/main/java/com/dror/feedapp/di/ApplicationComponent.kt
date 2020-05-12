package com.dror.feedapp.di

import com.dror.feedapp.api.APIRepository
import com.dror.feedapp.api.APIService
import com.dror.feedapp.viewmodel.APIViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {
    fun inject(apiService: APIService)
    fun inject(apiService: APIRepository)
    fun inject(apiViewModel: APIViewModel)

}