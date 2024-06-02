package com.github.hubcreator.emojihub

object EmojiChecker {
    private val emojiRanges = listOf(
        0x1F600..0x1F64F, // Emoticons
        0x1F300..0x1F5FF, // Miscellaneous Symbols and Pictographs
        0x1F680..0x1F6FF, // Transport and Map Symbols
        0x1F700..0x1F77F, // Alchemical Symbols
        0x1F780..0x1F7FF, // Geometric Shapes Extended
        0x1F800..0x1F8FF, // Supplemental Arrows-C
        0x1F900..0x1F9FF, // Supplemental Symbols and Pictographs
        0x1FA00..0x1FAFF, // Symbols and Pictographs Extended-A
        0x2600..0x26FF,   // Miscellaneous Symbols
        0x2700..0x27BF,   // Dingbats
        0xFE00..0xFE0F,   // Variation Selectors
        0x1F1E6..0x1F1FF, // Regional Indicator Symbols
        0x2300..0x23FF,   // Miscellaneous Technical
        0x2B00..0x2BFF,   // Miscellaneous Symbols and Arrows
        0x2900..0x297F,   // Supplemental Arrows-B
        0x20A0..0x20CF,   // Currency Symbols
        0x1F000..0x1F02F, // Mahjong Tiles and Domino Tiles
        0x2000..0x206F,   // General Punctuation
        0x2100..0x214F,   // Letterlike Symbols
        0x2190..0x21FF,   // Arrows
        0x2400..0x243F,   // Control Pictures
        0x2500..0x257F,   // Box Drawing
        0x2580..0x259F,   // Block Elements
        0x25A0..0x25FF,   // Geometric Shapes
        0x2C60..0x2C7F,   // Latin Extended-C
        0x2E00..0x2E7F,   // Supplemental Punctuation
        0x3000..0x303F,   // CJK Symbols and Punctuation
        0x3200..0x32FF,   // Enclosed CJK Letters and Months
        0xA490..0xA4CF,   // Yi Radicals
        0x1F004..0x1F004, // Mahjong Tile Red Dragon
        0x1F0CF..0x1F0CF, // Playing Card Black Joker
        0x1F170..0x1F171, // Negative Squared Latin Capital Letters A and B
        0x1F17E..0x1F17F, // Negative Squared Latin Capital Letters O and P
        0x1F18E..0x1F18E, // Negative Squared AB
        0x1F191..0x1F19A, // Squared CL to Squared VS
        0x1F201..0x1F202, // Squared Katakana Koko and Sa
        0x1F21A..0x1F21A, // Squared CJK Unified Ideograph-7121
        0x1F22F..0x1F22F, // Squared CJK Unified Ideograph-6307
        0x1F232..0x1F23A, // Squared CJK Unified Ideographs
        0x1F250..0x1F251, // Circled Ideograph Advantage and Accept
        0x00A9..0x00AE,   // © COPYRIGHT SIGN, ® REGISTERED SIGN
        0x24C2..0x24C2    // Ⓜ CIRCLED LATIN CAPITAL LETTER M
    )

    private val keycapSequences = setOf(
        Pair(0x0023, 0x20E3), // #⃣
        Pair(0x002A, 0x20E3), // *⃣
        Pair(0x0030, 0x20E3), // 0⃣
        Pair(0x0031, 0x20E3), // 1⃣
        Pair(0x0032, 0x20E3), // 2⃣
        Pair(0x0033, 0x20E3), // 3⃣
        Pair(0x0034, 0x20E3), // 4⃣
        Pair(0x0035, 0x20E3), // 5⃣
        Pair(0x0036, 0x20E3), // 6⃣
        Pair(0x0037, 0x20E3), // 7⃣
        Pair(0x0038, 0x20E3), // 8⃣
        Pair(0x0039, 0x20E3)  // 9⃣
    )

    fun containsEmoji(input: String): Boolean {
        var i = 0
        while (i < input.length) {
            val codePoint = input.codePointAt(i)
            if (isEmoji(codePoint)) {
                return true
            }
            // Check for keycap sequences
            if (i + 1 < input.length) {
                val nextCodePoint = input.codePointAt(i + 1)
                if (isKeycapSequence(codePoint, nextCodePoint)) {
                    return true
                }
            }
            i += Character.charCount(codePoint)
        }
        return false
    }

    fun removeEmojis(input: String): String {
        val result = StringBuilder()
        var i = 0
        while (i < input.length) {
            val codePoint = input.codePointAt(i)
            val charCount = Character.charCount(codePoint)

            // 특수 국기 이모지 시퀀스 검사 및 처리
            if (codePoint == 0x1F3F4 && i + charCount < input.length) { // 흑색 깃발 시작 확인
                var sequenceEnd = i + charCount
                var isFlagSequence = true

                // 최대 국기 시퀀스 길이까지 확인
                while (sequenceEnd < input.length && isFlagSequence) {
                    val nextCodePoint = input.codePointAt(sequenceEnd)
                    if (nextCodePoint !in 0xE0067..0xE007F) { // 특정 지역 지시자 코드 범위를 벗어나면 중단
                        isFlagSequence = false
                        continue
                    }
                    sequenceEnd += Character.charCount(nextCodePoint)
                }

                // 전체 국기 시퀀스를 건너뛰기
                if (isFlagSequence) {
                    i = sequenceEnd
                    continue
                }
            }

            // keycap 시퀀스 검사 및 처리
            if (i + charCount < input.length) {
                val nextCodePoint = input.codePointAt(i + charCount)
                val nextCharCount = Character.charCount(nextCodePoint)

                // Variation Selector가 있는 경우 다음 코드 포인트를 확인
                if (nextCodePoint == 0xFE0F && i + charCount + nextCharCount < input.length) {
                    val thirdCodePoint = input.codePointAt(i + charCount + nextCharCount)
                    if (isKeycapSequence(codePoint, thirdCodePoint)) {
                        i += charCount + nextCharCount + Character.charCount(thirdCodePoint)
                        continue
                    }
                }

                // 일반 keycap 시퀀스 검사
                if (isKeycapSequence(codePoint, nextCodePoint)) {
                    i += charCount + nextCharCount
                    continue
                }
            }

            // 일반 이모지 검사 및 처리
            if (isEmoji(codePoint)) {
                i += charCount
                continue
            }

            // keycap 이나 이모지가 아닌 경우 문자 추가
            result.append(Character.toChars(codePoint))
            i += charCount
        }
        return result.toString()
    }

    fun extractEmojis(input: String): String {
        val result = StringBuilder()
        var i = 0
        while (i < input.length) {
            val codePoint = input.codePointAt(i)
            var charCount = Character.charCount(codePoint)

            // 키캡 시퀀스를 확인하고 추출
            if (i + charCount < input.length) {
                val nextCodePoint = input.codePointAt(i + charCount)
                val nextCharCount = Character.charCount(nextCodePoint)

                // 변형 선택자를 포함하여 키캡 시퀀스 확인
                if (nextCodePoint == 0xFE0F && i + charCount + nextCharCount < input.length) {
                    val thirdCodePoint = input.codePointAt(i + charCount + nextCharCount)
                    if (keycapSequences.contains(Pair(codePoint, thirdCodePoint))) {
                        result.append(String(Character.toChars(codePoint)))
                        result.append(String(Character.toChars(nextCodePoint))) // 변형 선택자 추가
                        result.append(String(Character.toChars(thirdCodePoint)))
                        i += charCount + nextCharCount + Character.charCount(thirdCodePoint)
                        continue
                    }
                }

                // 일반 키캡 시퀀스 확인
                if (keycapSequences.contains(Pair(codePoint, nextCodePoint))) {
                    result.append(String(Character.toChars(codePoint)))
                    result.append(String(Character.toChars(nextCodePoint)))
                    i += charCount + nextCharCount
                    continue
                }
            }

            // 일반 이모지 확인하고 추출
            if (emojiRanges.any { codePoint in it }) {
                result.append(String(Character.toChars(codePoint)))
                i += charCount
                continue
            }

            i += charCount
        }
        return result.toString()
    }

    private fun isEmoji(codePoint: Int): Boolean {
        return emojiRanges.any { range -> codePoint in range }
    }

    private fun isKeycapSequence(first: Int, second: Int): Boolean {
        return keycapSequences.contains(Pair(first, second))
    }
}
