package com.codeborne.selenide.commands

import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.WebElementSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.WithAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
internal class GetDataAttributeCommandTest : WithAssertions {
    private val proxy = Mockito.mock(SelenideElement::class.java)
    private val locator = Mockito.mock(WebElementSource::class.java)
    private val mockedElement = Mockito.mock(SelenideElement::class.java)
    private val getDataAttributeCommand = GetDataAttribute()
    @BeforeEach
    fun setup() = runBlockingTest {
        Mockito.`when`<Any>(locator.getWebElement()).thenReturn(mockedElement)
    }

    @Test
    fun testExecuteMethodWithDataAttribute() = runBlockingTest {
        val argument = "class"
        val elementAttribute = "hello"
        Mockito.`when`(mockedElement.getAttribute("data-$argument")).thenReturn(elementAttribute)
        assertThat<Any>(getDataAttributeCommand.execute(proxy, locator, arrayOf<Any>(argument, "something more")))
            .isEqualTo(elementAttribute)
    }

    @Test
    fun testExecuteMethodWithNoDataAttribute() = runBlockingTest {
        val argument = "class"
        Mockito.`when`(mockedElement.getAttribute("data-$argument")).thenReturn(null)
        assertThat<Any>(getDataAttributeCommand.execute(proxy, locator, arrayOf<Any>(argument, "something more")))
            .isNull()
    }
}
