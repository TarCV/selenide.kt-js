package com.codeborne.selenide.conditions

import com.codeborne.selenide.Condition
import com.codeborne.selenide.Driver
import org.openqa.selenium.WebElement

open class CustomMatch(description: String, protected val predicate: (org.openqa.selenium.WebElement) -> Boolean) : Condition(
  description
) {
    override suspend fun apply(driver: Driver, element: org.openqa.selenium.WebElement): Boolean {
        return predicate(element)
    }

    override fun toString(): String {
        return "match '${name}' predicate."
    }
}
