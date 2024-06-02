package com.github.hubcreator.emojihub

import java.io.FileNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * @author Hyunseung Jung
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmojiCheckerTest {
    private val tmp = "hello"

    @ParameterizedTest
    @MethodSource("emojiProvider")
    @DisplayName("containsEmoji")
    fun containsEmoji(emoji: String) {
        assertThat(EmojiChecker.containsEmoji("$emoji $tmp")).isTrue()
    }

    @ParameterizedTest
    @MethodSource("emojiProvider")
    @DisplayName("removeEmojis")
    fun removeEmojis(emoji: String) {
        assertThat(EmojiChecker.removeEmojis("$tmp$emoji$tmp")).isEqualTo("$tmp$tmp")
    }

    @ParameterizedTest
    @MethodSource("emojiProvider")
    @DisplayName("extractEmojis")
    fun extractEmojis(emoji: String) {
        assertThat(EmojiChecker.extractEmojis("$tmp$emoji$tmp")).isEqualTo(emoji)
    }

    private fun emojiProvider(): List<String> {
        val inputStream = javaClass.getResourceAsStream("/emoji-test-latest.txt") ?: throw FileNotFoundException()

        val result = mutableListOf<String>()
        inputStream.bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                if (line.isNotEmpty() && !line.startsWith("#")) {
                    val unicode = line.takeWhile { it != ';' }.trim()
                    val emoji = unicode.split(" ").map { Integer.parseInt(it, 16) }
                        .flatMap { Character.toChars(it).asIterable() }
                        .joinToString("")
                    result.add(emoji)
                }
            }
        }
        return result
    }
}
