package org.kimp.cellular.automation

import androidx.annotation.NonNull

class IterationThread : Thread {
    private val delayTime: Long = 300

    private lateinit var cellularAutomationView: CellularAutomationView
    var isRunning: Boolean = false

    constructor(@NonNull cellularAutomationView: CellularAutomationView) {
        this.cellularAutomationView = cellularAutomationView
    }

    fun finish() {
        isRunning = false
    }

    override fun start() {
        isRunning = true
        super.start()
    }

    override fun run() {
        while (isRunning) {
            Thread.sleep(delayTime)
            cellularAutomationView.processAutomation()
        }
    }
}
