import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

const val instancesCount = 1000
var finishedSuccessfullyCount = 0

fun main() {
    repeat(instancesCount) { number ->
        SharedFlowApp(number)
    }
    keepProcessAlive()
}

/**
 * Shared flow works great out of the box. Even better with:
 * > yield() -> ~870/1000 app instances finish successfully
 * > yield() + delay(1) -> 1000/1000 app instances finish successfully
 * */
class SharedFlowApp(private val number: Int) : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

    private val sharedFlow: MutableSharedFlow<SampleItem> = MutableSharedFlow()

    private val flow: SharedFlow<SampleItem>
        get() = sharedFlow

    init {
        launch {
            logger.info("app init")
            subscribe()
            publish()
        }
    }

    private var hasReceived = false

    private var shouldEmit = true

    private fun subscribe() {
        flow
            .onSubscription {
                logger.info("flow onSubscription")
            }
            .onStart {
                logger.info("flow onStart")
            }
            .onEach {
                logger.info("item collected $it")
                if (it.i == 0) {
                    hasReceived = true
                    shouldEmit = false
                    finishedSuccessfullyCount++
                    logger.info("ALL OKAY at Instance $number")
                    if (finishedSuccessfullyCount == instancesCount) {
                        logger.info("ALL INSTANCES WERE SUCCESSFUL")
                        exitProcess(0)
                    }
                } else {
                    if (!hasReceived) {
                        logger.error("ERROR at Instance $number. Finished successfully count: $finishedSuccessfullyCount")
                        exitProcess(500)
                    }
                }
            }
            .launchIn(this)
        logger.info("launchIn called")
    }

    private suspend fun publish() {
        yield()
//        delay(1)
        repeat(1000) { i ->
            if (!shouldEmit) return@repeat
            val sampleItem = SampleItem(i = i)
            logger.info("publishing item: $sampleItem")
            sharedFlow.emit(sampleItem)
        }
    }
}