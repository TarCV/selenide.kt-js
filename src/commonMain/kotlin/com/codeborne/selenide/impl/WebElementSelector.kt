package com.codeborne.selenide.impl

import com.codeborne.selenide.Driver
import com.codeborne.selenide.SelectorMode
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.filecontent.Sizzle.sizzleJs
import org.openqa.selenium.By.ByCssSelector
import org.openqa.selenium.By.ByXPath
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement

/**
 * Thanks to http://selenium.polteq.com/en/injecting-the-sizzle-css-selector-library/
 */
open class WebElementSelector {
    suspend fun findElement(driver: Driver, context: org.openqa.selenium.SearchContext, selector: org.openqa.selenium.By): org.openqa.selenium.WebElement {
        checkThatXPathNotStartingFromSlash(context, selector)
        if (driver.config().selectorMode() === SelectorMode.CSS || selector !is ByCssSelector) {
            return findElement(context, selector)
        }
        val webElements = evaluateSizzleSelector(driver, context, selector)
        if (webElements.isEmpty()) {
            throw NoSuchElementException("Cannot locate an element using $selector")
        }
        return webElements[0]
    }
    suspend fun findElements(driver: Driver, context: org.openqa.selenium.SearchContext, selector: org.openqa.selenium.By): List<org.openqa.selenium.WebElement> {
        checkThatXPathNotStartingFromSlash(context, selector)
        return if (driver.config().selectorMode() === SelectorMode.CSS || selector !is ByCssSelector) {
            findElements(context, selector)
        } else evaluateSizzleSelector(
            driver,
            context,
            selector
        )
    }

    private suspend fun findElement(context: org.openqa.selenium.SearchContext, selector: org.openqa.selenium.By): org.openqa.selenium.WebElement {
        return if (context is SelenideElement) context.toWebElement().findElement(selector) else context.findElement(
            selector
        )
    }

    private suspend fun findElements(context: org.openqa.selenium.SearchContext, selector: org.openqa.selenium.By): List<org.openqa.selenium.WebElement> {
        return if (context is SelenideElement) context.toWebElement().findElements(selector) else context.findElements(
            selector
        )
    }

    protected fun checkThatXPathNotStartingFromSlash(context: org.openqa.selenium.SearchContext?, selector: org.openqa.selenium.By) {
        if (context is org.openqa.selenium.WebElement) {
            if (selector is ByXPath) {
                require(!selector.toString().startsWith("By.xpath: /")) { "XPath starting from / searches from root" }
            }
        }
    }
    protected suspend fun evaluateSizzleSelector(
        driver: Driver,
        context: org.openqa.selenium.SearchContext?,
        sizzleCssSelector: ByCssSelector
    ): List<org.openqa.selenium.WebElement> {
        injectSizzleIfNeeded(driver)
        val sizzleSelector = sizzleCssSelector.toString()
            .replace("By.selector: ", "")
            .replace("By.cssSelector: ", "")
        return if (context is org.openqa.selenium.WebElement) driver.executeJavaScript(
            "return Sizzle(arguments[0], arguments[1])",
            sizzleSelector,
            context
        ) else driver.executeJavaScript("return Sizzle(arguments[0])", sizzleSelector)
    }

    protected suspend fun injectSizzleIfNeeded(driver: Driver) {
        if (!sizzleLoaded(driver)) {
            injectSizzle(driver)
        }
    }

    protected suspend fun sizzleLoaded(driver: Driver): Boolean {
        return try {
            driver.executeJavaScript("return typeof Sizzle != 'undefined'")
        } catch (e: org.openqa.selenium.WebDriverException) {
            false
        }
    }

    // TODO: was synchronized in Java code
    protected suspend fun injectSizzle(driver: Driver) = driver.executeJavaScript<Any>(sizzleJs)

    companion object {
        var instance = WebElementSelector()
    }
}
