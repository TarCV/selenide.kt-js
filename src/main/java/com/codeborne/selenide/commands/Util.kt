package com.codeborne.selenide.commands

import com.codeborne.selenide.Condition
import java.time.Duration
import java.util.Arrays
import javax.annotation.CheckReturnValue
import javax.annotation.ParametersAreNonnullByDefault

@ParametersAreNonnullByDefault
internal object Util {
    @CheckReturnValue
    fun <T> firstOf(args: Array<Any>?): T {
        require(!(args == null || args.isEmpty())) { "Missing arguments" }
        return args[0] as T
    }

    @JvmStatic
    @CheckReturnValue
    fun argsToConditions(args: Array<Any>?): List<Condition> {
        if (args == null) return emptyList()
        val conditions: MutableList<Condition> = ArrayList(args.size)
        for (arg in args) {
          arg.let { when {
              it is Condition -> conditions.add(it)
              it is Array<*> && it[0] is Condition -> conditions.addAll( // TODO: why check in Java code was incomplete?
                (it as Array<Condition>).toList()
              )
              else -> require(
                it is String || it is Long || it is Duration
              ) { "Unknown parameter: $it" }
          } }
        }
        return conditions
    }
}
