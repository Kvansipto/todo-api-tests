package utils

import java.util.concurrent.atomic.AtomicLong

object IdGenerator {
    private val counter = AtomicLong(System.currentTimeMillis())

    fun nextId(): Long = counter.incrementAndGet()

    fun nextRange(n: Int): List<Long> =
        List(n) { counter.getAndIncrement() }
}
