package com.lindsaylevine.bytecode

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsProvider
import org.junit.jupiter.params.provider.ArgumentsSource
import java.util.stream.Stream

class IntegerUtilsTest {
    @ParameterizedTest
    @BothMethods
    fun `null -- always -- is not positive`(function: PositivityTest) {
        assertThat(function(null)).isFalse
    }

    @ParameterizedTest
    @BothMethods
    fun `negative number -- always -- is not positive`(function: PositivityTest) {
        assertThat(function(-1)).isFalse
    }

    @ParameterizedTest
    @BothMethods
    fun `zero -- always -- is not positive`(function: PositivityTest) {
        assertThat(function(0)).isFalse
    }

    @ParameterizedTest
    @BothMethods
    fun `positive number -- always -- is positive`(function: PositivityTest) {
        assertThat(function(1)).isTrue
    }
}

typealias PositivityTest = (Int?) -> Boolean

/**
 * Used on a parameterized test, provides both the sane and insane implementations of isPositive.
 */
@ArgumentsSource(BothMethodsProvider::class)
annotation class BothMethods

class BothMethodsProvider: ArgumentsProvider {
    override fun provideArguments(context: ExtensionContext?): Stream<Arguments> = Stream.of(
        Arguments.of(IntegerUtils::isPositiveSane),
        Arguments.of(IntegerUtils::isPositiveInsane)
    )
}