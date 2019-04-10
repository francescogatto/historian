package net.francescogatto.catlog.internal

/**
 * @author Francesco Gatto <f.gatto@eco-mind.eu>
 */
interface LogWriter {

    fun log(log: LogEntity)
    fun delete()

}