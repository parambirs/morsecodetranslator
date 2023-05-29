package io.github.parambirs.morse

import java.awt.*
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextArea
import javax.swing.SwingConstants
import javax.swing.SwingUtilities

class MorseCodeTranslator {
    private val englishToMorseLib = mapOf(
        "A" to ".-",
        "B" to "-...",
        "C" to "-.-.",
        "D" to "-..",
        "E" to ".",
        "F" to "..-.",
        "G" to "--.",
        "H" to "....",
        "I" to "..",
        "J" to ".---",
        "K" to "-.-",
        "L" to ".-..",
        "M" to "--",
        "N" to "-.",
        "O" to "---",
        "P" to ".--.",
        "Q" to "--.-",
        "R" to ".-.",
        "S" to "...",
        "T" to "-",
        "U" to "..-",
        "V" to "...-",
        "W" to ".--",
        "X" to "-..-",
        "Y" to "-.--",
        "Z" to "--..",

        "0" to "-----",
        "1" to ".----",
        "2" to "..---",
        "3" to "...--",
        "4" to "....-",
        "5" to ".....",
        "6" to "-....",
        "7" to "--...",
        "8" to "---..",
        "9" to "----.",

        "." to ".-.-.-",
        "," to "--..--",
        "?" to "..--..",
        ":" to "---...",
        "-" to "-....-",
        "@" to ".--.-.",
        "eror" to "........",
    )

    // fill the morseToEnglishLib
    private val morseToEnglishLib = englishToMorseLib.map { (key, value) -> value to key }.toMap()

    private val info =
        """
    Morse Translator
    Note:
    Morse code words are separated by /
    Morse code alphabets are separated by single whitespace
    Press space bar for auto translation
    """.trimIndent()

    init {
        val englishTextArea = JTextArea(20, 20).apply {
            text = "Hello World"
            lineWrap = true
            wrapStyleWord = true
            margin = Insets(5, 5, 5, 5)
        }

        val englishTextLabel = JLabel("English Text").apply {
            horizontalAlignment = SwingConstants.CENTER
        }

        val clearEnglishText = JButton("<< Clear Text")
        val englishToMorseBt = JButton("English >> Morse")

        val englishControlPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(clearEnglishText)
            add(englishToMorseBt)
        }

        val englishTextPanel = JPanel().apply {
            layout = BorderLayout()
            add(englishTextLabel, BorderLayout.NORTH)
            add(JScrollPane(englishTextArea), BorderLayout.CENTER)
            add(englishControlPanel, BorderLayout.SOUTH)
        }

        val morseTextArea = JTextArea().apply {
            text = ".-"
            lineWrap = true
            wrapStyleWord = true
            margin = Insets(5, 5, 5, 5)
            font = Font("", 0, 20)
        }

        val morseTextLabel = JLabel("Morse Code").apply {
            horizontalAlignment = SwingConstants.CENTER
        }

        val morseToEnglishBt = JButton("Morse >> English")
        val clearMorseText = JButton("Clear Text >>")

        val morseControlPanel = JPanel().apply {
            layout = FlowLayout(FlowLayout.RIGHT)
            add(morseToEnglishBt)
            add(clearMorseText)
        }

        val morseTextPanel = JPanel().apply {
            layout = BorderLayout()
            add(morseTextLabel, BorderLayout.NORTH)
            add(JScrollPane(morseTextArea), BorderLayout.CENTER)
            add(morseControlPanel, BorderLayout.SOUTH)
        }

        val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, englishTextPanel, morseTextPanel).apply {
            border = BorderFactory.createLineBorder(Color.BLACK)
        }

        val infoTextArea = JTextArea().apply {
            lineWrap = true
            wrapStyleWord = true
            text = info
            background = Color(241, 241, 241)
            isEditable = false
            margin = Insets(5, 5, 5, 5)
        }

        val infoPanel = JPanel(BorderLayout()).apply {
            add(infoTextArea, BorderLayout.CENTER)
        }

        val mainPanel = JPanel(BorderLayout()).apply {
            add(infoPanel, BorderLayout.NORTH)
            add(splitPane, BorderLayout.CENTER)
        }

//        splitPane.resizeWeight = 0.5

        englishToMorseBt.addActionListener {
            val english = englishTextArea.text?.trim() ?: ""
            morseTextArea.text = englishWordToMorseWord(english)
        }

        morseToEnglishBt.addActionListener {
            val morse = morseTextArea.text?.trim() ?: ""
            englishTextArea.text = morseWordToEnglishWord(morse)
        }

        englishTextArea.addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                // when space bar (or backspace) is pressed, do the conversion
                if (e.keyChar.isWhitespace() || e.keyCode == KeyEvent.VK_BACK_SPACE) {
                    morseTextArea.text = englishWordToMorseWord(englishTextArea.text)
                }
            }
        })

        morseTextArea.addKeyListener(object : KeyAdapter() {
            override fun keyTyped(e: KeyEvent) {
                if (e.keyChar.isWhitespace() || e.keyCode == KeyEvent.VK_BACK_SPACE) {
                    englishTextArea.text = morseWordToEnglishWord(morseTextArea.text)
                }
            }
        })

        clearEnglishText.addActionListener {
            englishTextArea.text = ""
        }

        clearMorseText.addActionListener {
            morseTextArea.text = ""
        }

        JFrame().apply {
            title = "Morse Translator"
            layout = BorderLayout()
            add(mainPanel, BorderLayout.CENTER)
            size = Dimension(800, 650)
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setLocationRelativeTo(null)
            isResizable = false
            isVisible = true
        }
    }

    private fun englishWordToMorseWord(englishWord: String): String {
        val buffer = StringBuilder()
        for (word in englishWord.split(Regex("[ \n]"))) {
            for (c in word.toCharArray()) {
                buffer.append(englishToMorseLib[c.uppercase()] ?: "?? ")
                buffer.append(" ")
            }
            buffer.append(" / ")
        }
        return buffer.toString()
    }

    private fun morseWordToEnglishWord(morseWord: String): String {
        val buffer = StringBuilder()
        for (word in morseWord.split(Regex("[\\s\\n]"))) {
            when {
                word.isEmpty() -> continue
                word == "/" || word == "|" -> buffer.append(" ")
                else ->
                    buffer.append(morseToEnglishLib[word]?.lowercase() ?: "?? ")
            }
        }
        return buffer.toString()
    }
}


fun main() {
    SwingUtilities.invokeLater {
        MorseCodeTranslator()
    }
}
