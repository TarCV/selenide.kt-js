package com.codeborne.selenide.commands

import com.codeborne.selenide.Command
import com.codeborne.selenide.ElementsCollection
import com.codeborne.selenide.SelenideElement
import com.codeborne.selenide.impl.BySelectorCollection
import com.codeborne.selenide.impl.WebElementSource
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
class FindAll : Command<ElementsCollection> {
    @CheckReturnValue
    override fun execute(proxy: SelenideElement, locator: WebElementSource, args: Array<Any>?): ElementsCollection {
        val selector = Util.firstOf<Any>(args)
        return ElementsCollection(
            BySelectorCollection(locator.driver(), proxy, WebElementSource.getSelector(selector))
        )
    }
}
