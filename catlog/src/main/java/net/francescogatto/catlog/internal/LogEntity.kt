package net.francescogatto.catlog.internal

/**
 * Entity class representing log
 */

class LogEntity {
    val priority: String
    val message: String
    val timestamp: Long
    var tag: String? = ""
    val stackException: String

    private constructor(priority: String, tag: String?, message: String, timestamp: Long) {
        this.priority = priority
        this.tag = tag
        this.message = message
        this.timestamp = timestamp
        this.stackException = ""
    }

    private constructor(priority: String, tag: String?, message: String, timestamp: Long, t: Throwable) {
        this.priority = priority
        this.tag = tag
        this.message = message
        this.timestamp = timestamp
        this.stackException = t.stackTrace.toString()
    }

    companion object {

        fun create(priority: Int, tag: String, message: String, timestamp: Long): LogEntity {
            return LogEntity(Util.priorityString(priority), tag, message, timestamp)
        }

        fun create(priority: Int, tag: String, message: String, timestamp: Long, t: Throwable): LogEntity {
            return LogEntity(Util.priorityString(priority), tag, message, timestamp, t)
        }
    }
}
