import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

@FlowPreview
@ExperimentalCoroutinesApi
fun main() {
    ArrayBroadcastApp()
    keepProcessAlive()
}

@FlowPreview
@ExperimentalCoroutinesApi
class ArrayBroadcastApp : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default

    private val channel: BroadcastChannel<SampleItem> = BroadcastChannel(BUFFERED)

    init {
        launch {
            logger.info("app init")
            subscribe()
            publish()
        }
    }

    private fun subscribe() {
        channel.asFlow()
            .onStart {
                logger.info("flow subscribed")
            }
            .onEach {
                logger.info("item collected: $it")
                coroutineContext.cancel()
                exitProcess(0)
            }
            .launchIn(this)
        logger.info("launchIn called")
    }

    private suspend fun publish() {
        while (true) {
            try {
                val sampleItem = SampleItem()
                logger.info("publishing item: $sampleItem")
                channel.send(sampleItem)
            } catch (e: Throwable) {
                logger.info("error publishing item")
            }
        }
    }
}