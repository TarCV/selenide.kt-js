package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class NamedCondition(private val prefix: String, private val delegate: Condition) : Condition(
    delegate.name, delegate.missingElementSatisfiesCondition()
) {
    override fun apply(driver: Driver, element: WebElement): Boolean {
        return delegate.apply(driver, element)
    }

    override fun actualValue(driver: Driver, element: WebElement): String? {
        return delegate.actualValue(driver, element)
    }

    override fun negate(): Condition {
        return delegate.negate()
    }

    override fun toString(): String {
        return "$prefix $delegate"
    }
}
