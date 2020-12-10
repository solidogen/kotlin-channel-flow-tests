import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

fun main() {
    SharedFlowShareInLazily()
    keepProcessAlive()
}

/**
 * This works good as well, but requires additional setup and shareIn scope.
 * SharedFlowApp approach is enough with yield()
 * */
class SharedFlowShareInLazily : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

    private val publishFlow: Flow<SampleItem> = flow {
        repeat(1000) { i ->
            val sampleItem = SampleItem(i = i)
            logger.info("publishing item: $sampleItem")
            emit(sampleItem)
        }
    }

    init {
        launch {
            logger.info("app init")
            subscribe()
        }
    }

    private fun subscribe() {
        publishFlow.shareIn(this, SharingStarted.Lazily)
            .onSubscription {
                logger.info("flow onSubscription")
            }
            .onStart {
                logger.info("flow onStart")
            }
            .onEach {
                logger.info("item collected $it")
            }
            .launchIn(this)
        logger.info("launchIn called")
    }
}