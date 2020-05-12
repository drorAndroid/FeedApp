package com.dror.feedapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dror.feedapp.api.APIRepository
import com.dror.feedapp.di.DaggerApplicationComponent
import com.dror.feedapp.model.FeedMessage
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class APIViewModel: ViewModel() {
    @Inject
    lateinit var repository: APIRepository
    private val disposable = CompositeDisposable()
    val feedMessage = MutableLiveData<FeedMessage?>()
    val feedError = MutableLiveData<String?>()
    var filter = MutableLiveData("")
    private val messagesSubscription: Subscription? = null

    init {
        DaggerApplicationComponent.create().inject(this)
        repository.start()
    }

    fun setSearchFilter(text: String) {
        filter.value = text
    }

    fun getFilter() : String? {
        return filter.value
    }

    fun start() {
        disposable.add(
            repository.getFeedMessages()
                .delay(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<String>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: String) {
                        disposable.add(
                            createFeetMessage(t)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeWith(object: DisposableSingleObserver<FeedMessage>() {
                                    override fun onSuccess(t: FeedMessage) {
                                        feedMessage.value = t
                                    }

                                    override fun onError(e: Throwable) {
                                        feedError.value = "error creating feed message"
                                    }

                                }))
                    }

                    override fun onError(e: Throwable) {
                        feedError.value = e.message
                    }

                })
        )

        disposable.add(
            repository.getFeedFailure()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object: DisposableObserver<String>() {
                    override fun onComplete() {
                    }

                    override fun onNext(t: String) {
                        feedError.value = t
                    }

                    override fun onError(e: Throwable) {
                        feedError.value = e.message
                    }

                })
        )
    }

    fun stop() {
        repository.stop()
    }

    private fun createFeetMessage(message: String): Single<FeedMessage> {
        return Single.create {
            val messageObject = JSONObject(message)
            val name = messageObject.getString("name")
            val color = messageObject.getString("bagColor")
            val weightAndUnitsString = messageObject.getString("weight")

            val sbNumber = StringBuilder()
            val sbUnits = StringBuilder()
            for (c in weightAndUnitsString.toCharArray()) {
                if ('.' == c || Character.isDigit(c)) sbNumber.append(c)
                else sbUnits.append(c)
            }


            val weight = sbNumber.toString().toDouble()
            val units = sbUnits.toString()

            try {
                it.onSuccess(FeedMessage(name, color, weight, units))
            }
            catch(e: Exception) {
                it.onError(e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        disposable.clear()
    }
}