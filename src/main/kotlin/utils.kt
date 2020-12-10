import mu.KotlinLogging

val logger = KotlinLogging.logger {}

data class SampleItem(val i: Int)

fun keepProcessAlive() {
    while (true) Unit
}