package com.example.project.campusloop.util

import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SnowflakeIdGenerator (
    private val workerId: Long = 1L
){
    init {
        require(workerId in 0..MAX_WORKER_ID) { "Worker ID must be between 0 and $MAX_WORKER_ID" }
    }

    private var lastTimestamp = -1L
    private var sequence = 0L

    @Synchronized
    fun nextId(): Long {
        var currentTimestamp = timeGen()

        if (currentTimestamp < lastTimestamp) {
            throw IllegalStateException("Clock moved backwards. Refusing to generate id for ${lastTimestamp - currentTimestamp} milliseconds")
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) and SEQUENCE_MASK
            if (sequence == 0L) {
                currentTimestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0L
        }

        lastTimestamp = currentTimestamp

        return ((currentTimestamp - START_EPOCH) shl TIMESTAMP_LEFT_SHIFT) or
                (workerId shl WORKER_ID_SHIFT) or
                sequence
    }

    private fun tilNextMillis(lastTimestamp: Long): Long {
            var timestamp = timeGen()
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen()
            }
            return timestamp
    }

    private fun timeGen(): Long {
        return Instant.now().toEpochMilli()
    }

    companion object {
        private const val START_EPOCH = 1704067200000L // 01.01.2024

        private const val WORKER_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private const val MAX_WORKER_ID = (-1L xor (-1L shl WORKER_ID_BITS))

        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        private const val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
        private const val SEQUENCE_MASK = (-1L xor (-1L shl SEQUENCE_BITS))
    }
}