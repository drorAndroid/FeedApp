package com.dror.feedapp.api

import com.dror.feedapp.di.DaggerApplicationComponent
import io.reactivex.Observable

class APIRepository(private val apiService: APIService) {
    init {
        DaggerApplicationComponent.create().inject(this)

    }

    fun start() {
        apiService.start()
    }

    fun stop() {
        apiService.stop()
    }

    fun getFeedMessages(): Observable<String> {
        return apiService.onMessageEvent
    }

    fun getFeedFailure(): Observable<String> {
        return apiService.onFailureEvent
    }
}