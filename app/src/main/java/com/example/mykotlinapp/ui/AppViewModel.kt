package com.example.mykotlinapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.mykotlinapp.domain.pojo.ApiRequestState
import com.example.mykotlinapp.ui.AppViewModel.BackgroundWorkConfig.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * base class implemented by all viewModels
 */
open class AppViewModel : ViewModel() {

    private val _httpRequestState = MutableLiveData<ApiRequestState>()

    val httpRequestState: LiveData<ApiRequestState>
        get() = _httpRequestState

    fun setHttpRequestState(httpRequestState: ApiRequestState) {
        _httpRequestState.value = httpRequestState
    }

    protected fun <T> submitHttpRequest(action: suspend () -> Result<T>): Job {
        return submitHttpRequest(action) {}
    }

    protected inline fun <T> submitHttpRequest(
        noinline action: suspend () -> Result<T>,
        crossinline handleResult: (T) -> Unit
    ): Job {
        setHttpRequestState(ApiRequestState.LOADING)
        return viewModelScope.launch {
            with(
                try {
                    action()
                } catch (exception: Exception) {
                    Result.failure(exception)
                }
            ) {
                onSuccess {
                    setHttpRequestState(ApiRequestState.SUCCESS)
                    handleResult(it)
                }
                onFailure { exception ->
                    setHttpRequestState(ApiRequestState.FAILED)
                    exception.message?.let { Log.v("AppViewModel: HTTP request", it) }
                }
            }
        }
    }

    override fun onCleared() {
        setHttpRequestState(ApiRequestState.FINISHED)
        super.onCleared()
    }

    companion object {

        fun getWorkRequest(
            builder: WorkRequest.Builder<*, out WorkRequest>,
            inputData: Data?,
            initialDelay: Duration?
        ): WorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            var requestBuilder = builder
            requestBuilder.setConstraints(constraints)
            initialDelay?.let { requestBuilder = requestBuilder.setInitialDelay(it) }
            inputData?.let { requestBuilder = requestBuilder.setInputData(it) }
            return requestBuilder.build()
        }

        inline fun <reified T : ListenableWorker> WorkManager.launchNetworkBackgroundTask(
            workConfig: BackgroundWorkConfig,
            inputData: Data? = null,
            initialDelay: Duration? = null
        ) {
            when (workConfig) {
                is UniquePeriodicBackgroundTask -> run {
                    val workRequest = getWorkRequest(
                        PeriodicWorkRequestBuilder<T>(
                            workConfig.repeatInterval,
                            workConfig.repeatIntervalTimeUnit
                        ), inputData, initialDelay
                    )
                    this.enqueueUniquePeriodicWork(
                        workConfig.workName,
                        workConfig.existingWorkPolicy,
                        workRequest as PeriodicWorkRequest
                    )
                }
                is UniqueBackgroundTask -> run {
                    val workRequest = getWorkRequest(OneTimeWorkRequestBuilder<T>(), inputData, initialDelay)
                    this.beginUniqueWork(
                        workConfig.workName,
                        workConfig.existingWorkPolicy,
                        workRequest as OneTimeWorkRequest
                    ).enqueue()
                }
                is RegularBackgroundTask -> run {
                    val workRequest = getWorkRequest(OneTimeWorkRequestBuilder<T>(), inputData, initialDelay)
                    this.beginWith(workRequest as OneTimeWorkRequest).enqueue()
                }
            }
        }

    }

    sealed interface BackgroundWorkConfig {
        data class UniquePeriodicBackgroundTask(
            val workName: String,
            val repeatInterval: Long,
            val repeatIntervalTimeUnit: TimeUnit,
            val existingWorkPolicy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
        ) : BackgroundWorkConfig

        data class UniqueBackgroundTask(
            val workName: String,
            val existingWorkPolicy: ExistingWorkPolicy = ExistingWorkPolicy.KEEP,
        ) : BackgroundWorkConfig

        object RegularBackgroundTask : BackgroundWorkConfig
    }

}