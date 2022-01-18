package org.kimp.cellular.automation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import org.kimp.cellular.automation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cellularAutomationView: CellularAutomationView

    private lateinit var iterationThread: IterationThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        cellularAutomationView = binding.cellularAutomation
        iterationThread = IterationThread(cellularAutomationView)

        binding.iterateButton.setOnClickListener(View.OnClickListener { _ ->
            cellularAutomationView.processAutomation()
        })

        binding.startButton.setOnClickListener(View.OnClickListener { _ ->
            if (iterationThread.isRunning) {
                var retry: Boolean = true
                iterationThread.finish()

                while (retry) {
                    try {
                        iterationThread.join()
                        retry = false
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                binding.startButton.setText(R.string.start_btn)
            }
            else {
                iterationThread = IterationThread(cellularAutomationView)
                iterationThread.start()
                binding.startButton.setText(R.string.stop_btn)
            }
        })
    }
}