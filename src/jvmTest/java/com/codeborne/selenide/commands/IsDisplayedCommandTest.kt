package com.codeborne.selenide.commands

import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.openqa.selenium.InvalidSelectorException
import org.openqa.selenium.NotFoundException
import org.openqa.selenium.WebDriverException

@ExperimentalCoroutinesApi
internal class IsDisplayedCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val isDisplayedCommand = IsDisplayed()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
    }

    @Test
    fun testExecuteMethodWhenElementIsNotPresent() = runBlockingTest {
        assertk.assertThat(isDisplayedCommand.execute(proxy, locator, arrayOf<Any>("something more")))
            .isFalse()
    }

    @Test
    fun testExecuteMethodWhenElementIsNotDisplayed() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`(mockedElement.isDisplayed).thenReturn(false)
        assertk.assertThat(isDisplayedCommand.execute(proxy, locator, arrayOf<Any>("something more")))
            .isFalse()
    }

    @Test
    fun testExecuteMethodWhenElementIsDisplayed() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.`when`(mockedElement.isDisplayed).thenReturn(true)
        assertk.assertThat(isDisplayedCommand.execute(proxy, locator, arrayOf<Any>("something more")))
            .isTrue()
    }

    @Test
    fun testExecuteMethodWhenWebDriverExceptionIsThrown() {
        catchExecuteMethodWithException(WebDriverException())
    }

    private fun <T : Throwable?> catchExecuteMethodWithException(exception: T) = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
        Mockito.doThrow(exception).`when`(mockedElement).isDisplayed
        assertk.assertThat(isDisplayedCommand.execute(proxy, locator, arrayOf<Any>("something more")))
            .isFalse()
    }

    @Test
    fun testExecuteMethodWhenNotFoundExceptionIsThrown() {
        catchExecuteMethodWithException(NotFoundException())
    }

    @Test
    fun testExecuteMethodWhenIndexOutOfBoundsExceptionIsThrown() {
        catchExecuteMethodWithException(IndexOutOfBoundsException())
    }

    @Test
    fun testExecuteMethodWhenExceptionWithInvalidSelectorException() {
        assertThatThrownBy { catchExecuteMethodWithException(NotFoundException("invalid selector")) }
            .isInstanceOf(InvalidSelectorException::class.java)
    }
}
