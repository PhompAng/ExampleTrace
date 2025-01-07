package com.pichai.example.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import junit.framework.TestCase.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalMetricApi::class)
class ExampleBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun simpleViewClick() {
        var firstStart = true
        benchmarkRule.measureRepeated(
            packageName = "com.pichai.example.trace",
            metrics = listOf(TraceSectionMetric("ClickTrace")),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = 5,
            setupBlock = {
                if (firstStart) {
                    startActivityAndWait()
                    firstStart = false
                }
            }
        ) {
            clickOnId("clickButton")
            waitForTextShown("Hello World")
            device.pressBack()
            waitForTextGone("Hello World")
        }
    }

    @Test
    fun simpleFrameTrace() {
        var firstStart = true
        benchmarkRule.measureRepeated(
            packageName = "com.pichai.example.trace",
            metrics = listOf(FrameTimingMetric()),
            compilationMode = CompilationMode.Full(),
            startupMode = null,
            iterations = 5,
            setupBlock = {
                if (firstStart) {
                    startActivityAndWait()
                    firstStart = false
                }
            }
        ) {
            clickOnId("clickButton")
            waitForTextShown("Hello World")
            device.pressBack()
            waitForTextGone("Hello World")
        }
    }

    private fun MacrobenchmarkScope.clickOnId(resourceId: String) {
        val selector = By.res("com.pichai.example.trace", resourceId)
        if (!device.wait(Until.hasObject(selector), 2_500)) {
            fail("Did not find object with id $resourceId")
        }

        device
            .findObject(selector)
            .click()
        // Chill to ensure we capture the end of the click span in the trace.
        Thread.sleep(100)
    }

    private fun MacrobenchmarkScope.waitForTextShown(text: String) {
        check(device.wait(Until.hasObject(By.text(text)), 500)) {
            "View showing '$text' not found after waiting 500 ms."
        }
    }

    private fun MacrobenchmarkScope.waitForTextGone(text: String) {
        check(device.wait(Until.gone(By.text(text)), 500)) {
            "View showing '$text' not found after waiting 500 ms."
        }
    }
}
