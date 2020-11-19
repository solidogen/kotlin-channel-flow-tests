import kotlinx.coroutines.*

fun main() {
    program()
    keepProcessAlive()
}

fun program() {
    val coroutineExceptionHandler = CoroutineExceptionHandler {
            coroutineContext, throwable -> println("exception: $throwable")
    }
    val coroutineScope = CoroutineScope(Dispatchers.Default + coroutineExceptionHandler)
    coroutineScope.launch {
        launch {
            throw RuntimeException("child coroutine 1 failed")
        }
        delay(10)
        println("parent coroutine")
    }
}