import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

fun main() {
    SharedFlowApp()
    keepProcessAlive()
}

class SharedFlowApp : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

    private val sharedFlow: MutableSharedFlow<SampleItem> = MutableSharedFlow()
    private val flow: Flow<SampleItem> = sharedFlow

    init {
        launch {
            logger.info("app init")
            subscribe()
            publish()
        }
    }

    private fun subscribe() {
        flow
            .onStart {
                logger.info("flow subscribed")
            }
            .onEach {
                logger.info("item collected $it")
                exitProcess(0)
            }
            .launchIn(this)
        logger.info("launchIn called")
    }

    private suspend fun publish() {
        while (true) {
            val sampleItem = SampleItem()
            logger.info("publishing item: $sampleItem")
            sharedFlow.emit(sampleItem)
        }
    }
}