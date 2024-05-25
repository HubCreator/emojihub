package com.github.hubcreator.emojihub

import java.io.FileNotFoundException
import org.junit.jupiter.api.Test

/**
 * @author Hyunseung Jung
 */
class EmojiCheckerTest {
    @Test
    fun hello() {
        val inputStream = javaClass.getResourceAsStream("/emoji-data-1.txt") ?: throw FileNotFoundException()

        inputStream.bufferedReader().use { reader ->
            reader.lineSequence().forEach { line ->
                if (line.isNotEmpty()) {
                    val unicode = line.takeWhile { it != ';' }.trim()
                    println(unicode)
                }
            }
        }
    }
}
