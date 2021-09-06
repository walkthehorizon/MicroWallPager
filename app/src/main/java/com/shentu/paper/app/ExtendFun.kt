package com.shentu.paper.app

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.TimeUnit

fun ktCountDown(
    total: Int, onTick: (Int) -> Unit, onFinish: () -> Unit,
    scope: CoroutineScope = GlobalScope
): Job {
    return flow {
        for (i in total downTo 0) {
            emit(i)
            delay(1000)
        }
    }.flowOn(Dispatchers.Default)
        .onCompletion { onFinish.invoke() }
        .onEach { onTick.invoke(it) }
        .flowOn(Dispatchers.Main)
        .launchIn(scope)
}

fun ktInterval(
    interval: Long  =1, delay: Long = 0, timeUnit: TimeUnit = TimeUnit.MILLISECONDS, onTick: () -> Unit,
    scope: CoroutineScope = GlobalScope
): Job {
    return flow {
        delay(timeUnit.toMillis(delay))
        for (i in 0..Long.MAX_VALUE) {
            emit(null)
            delay(timeUnit.toMillis(interval))
        }
    }.flowOn(Dispatchers.Default)
        .onEach { onTick.invoke() }
        .flowOn(Dispatchers.Main)
        .launchIn(scope)
}