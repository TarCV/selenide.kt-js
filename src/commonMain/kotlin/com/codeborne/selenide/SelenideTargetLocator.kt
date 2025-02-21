package com.codeborne.selenide

import com.codeborne.selenide.ex.AlertNotFoundException
import com.codeborne.selenide.ex.FrameNotFoundException
import com.codeborne.selenide.ex.UIAssertionError
import com.codeborne.selenide.ex.WindowNotFoundException
import com.codeborne.selenide.impl.windows.FrameByIdOrName
import com.codeborne.selenide.impl.windows.WindowByIndex
import com.codeborne.selenide.impl.windows.WindowByNameOrHandle
import org.openqa.selenium.Alert
import org.openqa.selenium.InvalidArgumentException
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriver.TargetLocator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic
import kotlin.time.Duration

class SelenideTargetLocator(private val driver: Driver) : TargetLocator {
    private val webDriver: org.openqa.selenium.WebDriver = driver.webDriver
    private val config: Config = driver.config()
    private val delegate: TargetLocator = webDriver.switchTo()

    @kotlin.time.ExperimentalTime
    override suspend fun frame(index: Int): org.openqa.selenium.WebDriver {
        return try {
            Wait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index))
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            throw frameNotFoundError("No frame found with index: $index", e)
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw frameNotFoundError("No frame found with index: $index", e)
        } catch (e: org.openqa.selenium.InvalidArgumentException) {
            if (isFirefox62Bug(e) || isChrome75Error(e)) {
                throw frameNotFoundError("No frame found with index: $index", e)
            }
            throw e
        }
    }

    @kotlin.time.ExperimentalTime
    override suspend fun frame(nameOrId: String): org.openqa.selenium.WebDriver {
        return try {
            Wait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(nameOrId))
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            throw frameNotFoundError("No frame found with id/name: $nameOrId", e)
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw frameNotFoundError("No frame found with id/name: $nameOrId", e)
        } catch (e: org.openqa.selenium.InvalidArgumentException) {
            if (isFirefox62Bug(e)) {
                throw frameNotFoundError("No frame found with id/name: $nameOrId", e)
            }
            throw e
        }
    }

    @kotlin.time.ExperimentalTime
    override suspend fun frame(frameElement: org.openqa.selenium.WebElement): org.openqa.selenium.WebDriver {
        return try {
            Wait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameElement))
        } catch (e: org.openqa.selenium.NoSuchElementException) {
            throw frameNotFoundError("No frame found with element: $frameElement", e)
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw frameNotFoundError("No frame found with element: $frameElement", e)
        } catch (e: org.openqa.selenium.InvalidArgumentException) {
            if (isFirefox62Bug(e)) {
                throw frameNotFoundError("No frame found with element: $frameElement", e)
            }
            throw e
        }
    }

    private fun isFirefox62Bug(e: org.openqa.selenium.InvalidArgumentException): Boolean {
        return e.message?.contains("untagged enum FrameId") == true
    }

    private fun isChrome75Error(e: org.openqa.selenium.InvalidArgumentException): Boolean {
        return e.message?.contains("invalid argument: 'id' out of range") == true
    }

    override suspend fun parentFrame(): org.openqa.selenium.WebDriver {
        return delegate.parentFrame()
    }

    override suspend fun defaultContent(): org.openqa.selenium.WebDriver {
        return delegate.defaultContent()
    }

    override suspend fun activeElement(): org.openqa.selenium.WebElement {
        return delegate.activeElement()
    }

    @kotlin.time.ExperimentalTime
    override suspend fun alert(): org.openqa.selenium.Alert {
        return try {
            Wait().until(ExpectedConditions.alertIsPresent())
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw alertNotFoundError(e)
        }
    }

    /**
     * Switch to the inner frame (last child frame in given sequence)
     */
    @kotlin.time.ExperimentalTime
    suspend fun innerFrame(vararg frames: String): org.openqa.selenium.WebDriver {
        delegate.defaultContent()
        for (frame in frames) {
            try {
                Wait().until(FrameByIdOrName(frame))
            } catch (e: org.openqa.selenium.NoSuchElementException) {
                throw frameNotFoundError("No frame found with id/name = $frame", e)
            } catch (e: org.openqa.selenium.TimeoutException) {
                throw frameNotFoundError("No frame found with id/name = $frame", e)
            }
        }
        return webDriver
    }

    /**
     * Switch to window/tab by index
     * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
     *
     * @param index index of window (0-based)
     */
    @kotlin.time.ExperimentalTime
    suspend fun window(index: Int): org.openqa.selenium.WebDriver? {
        return try {
            Wait().until(WindowByIndex(index))
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw windowNotFoundError("No window found with index: $index", e)
        }
    }

    /**
     * Switch to window/tab by index with a configurable timeout
     * NB! Order of windows/tabs can be different in different browsers, see Selenide tests.
     *
     * @param index    index of window (0-based)
     * @param duration the timeout duration. It overrides default Config.timeout()
     */
    @kotlin.time.ExperimentalTime
    suspend fun window(index: Int, duration: Duration): org.openqa.selenium.WebDriver {
        return try {
            Wait(duration).until(WindowByIndex(index))
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw windowNotFoundError("No window found with index: $index", e)
        }
    }

    /**
     * Switch to window/tab by name/handle/title
     *
     * @param nameOrHandleOrTitle name or handle or title of window/tab
     */
    @kotlin.time.ExperimentalTime
    override suspend fun window(nameOrHandleOrTitle: String): org.openqa.selenium.WebDriver {
        return try {
            Wait().until(WindowByNameOrHandle(nameOrHandleOrTitle))
        } catch (e: org.openqa.selenium.TimeoutException) {
            throw windowNotFoundError("No window found with name or handle or title: $nameOrHandleOrTitle", e)
        }
    }

    /**
     * Switch to window/tab by name/handle/title with a configurable timeout
     *
     * @param nameOrHandleOrTitle name or handle or title of window/tab
     * @param duration            the timeout duration. It overrides default Config.timeout()
     */
    @kotlin.time.ExperimentalTime
    suspend fun window(nameOrHandleOrTitle: String, duration: Duration): org.openqa.selenium.WebDriver {
        return try {
            Wait(duration).until(WindowByNameOrHandle(nameOrHandleOrTitle))
        } catch (e: TimeoutException) {
            throw windowNotFoundError("No window found with name or handle or title: $nameOrHandleOrTitle", e)
        }
    }

    @kotlin.time.ExperimentalTime
    private fun Wait(): SelenideWait {
        return SelenideWait(webDriver, config.timeout(), config.pollingInterval())
    }

    @kotlin.time.ExperimentalTime
    private fun Wait(timeout: Duration): SelenideWait {
        return SelenideWait(webDriver, timeout.toLongMilliseconds(), config.pollingInterval())
    }

    private fun frameNotFoundError(message: String, cause: Throwable): Error {
        val error = FrameNotFoundException(driver, message, cause)
        return UIAssertionError.wrap(driver, error, config.timeout())
    }

    private fun windowNotFoundError(message: String, cause: Throwable): Error {
        val error = WindowNotFoundException(driver, message, cause)
        return UIAssertionError.wrap(driver, error, config.timeout())
    }

    private fun alertNotFoundError(cause: Throwable): Error {
        val error = AlertNotFoundException(driver, "Alert not found", cause)
        return UIAssertionError.wrap(driver, error, config.timeout())
    }

}
