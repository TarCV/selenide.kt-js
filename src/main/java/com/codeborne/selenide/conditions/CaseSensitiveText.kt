package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import com.codeborne.selenide.impl.Html
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class CaseSensitiveText(private val expectedText: String) : Condition("textCaseSensitive") {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return Html.text.containsCaseSensitive(element.text, expectedText)
    }

    override fun toString(): String {
        return String.format("%s '%s'", name, expectedText)
    }
}
