package com.github.shiraji.yaemoji.contributor

import com.intellij.testFramework.builders.ModuleFixtureBuilder
import com.intellij.testFramework.fixtures.CodeInsightFixtureTestCase
import org.junit.jupiter.api.Test

class CommentCompletionContributorTest : CodeInsightFixtureTestCase<ModuleFixtureBuilder<*>>() {
    @Test
    fun foo() {
        setUp()

        myFixture.configureByText("foo.txt", ":<caret>")
        val lookupElements = myFixture.completeBasic()
        println(lookupElements)

        tearDown()
    }

}