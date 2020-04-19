package com.immotef.core


import com.google.gson.Gson
import com.immotef.core.errors.ErrorWrapper
import com.immotef.core.errors.ExceptionWrapperFactory
import com.immotef.core.errors.ResponseErrorBody
import kotlinx.coroutines.*
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

/**
 *
 */


interface CoroutineUtils : ExceptionWrapperFactory {
    val main: CoroutineContext
    val io: CoroutineContext
    val computation: CoroutineContext
    val globalScope: CoroutineScope

}


internal class CoroutineUtilsImp(private val gson: Gson) : CoroutineUtils {
    override val main: CoroutineContext = Dispatchers.Main
    override val io: CoroutineContext = Dispatchers.IO

    @ObsoleteCoroutinesApi
    override val computation: CoroutineContext = newFixedThreadPoolContext(4, "Computation dispatcher")
    override val globalScope: CoroutineScope = GlobalScope
    override fun produce(throwable: Throwable?): ErrorWrapper {
        return if (throwable != null) {

            return try {
                throw throwable
            } catch (ex: HttpException) {
                var errorCode = -1
                val responseErrorMessage =
                    try {
                        val responseBody = ex.response()?.errorBody()?.string()?.let { gson.fromJson(it, ResponseErrorBody::class.java) }
                        errorCode = responseBody?.errorCode ?: -1
                        responseBody?.message ?: ex.message()
                    } catch (e: java.lang.Exception) {
                        ""
                    }
                ErrorWrapper(responseErrorMessage, "", errorCode = errorCode)
            } catch (ex: Exception) {
                ErrorWrapper(throwable.localizedMessage ?: throwable.cause?.localizedMessage ?: "", "")
            }

        } else
            ErrorWrapper("", "")
    }
}