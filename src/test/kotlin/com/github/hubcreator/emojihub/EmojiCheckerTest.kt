package com.github.hubcreator.emojihub

import java.io.FileNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

/**
 * @author Hyunseung Jung
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmojiCheckerTest {
    private val hello = "hello"

    @ParameterizedTest
    @MethodSource("emojiProvider")
    fun containsEmoji(emoji: String) {
        assertThat(EmojiChecker.containsEmoji(emoji + hello)).isTrue()
    }

    @ParameterizedTest
    @MethodSource("emojiProvider")
    fun removeEmojis(emoji: String) {
        assertThat(EmojiChecker.removeEmojis(hello + emoji + hello)).isEqualTo(hello + hello)
    }

    @Test
    fun test() {
        assertThat(EmojiChecker.removeEmojis("#⃣" + "hello")).isEqualTo("hello")
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
