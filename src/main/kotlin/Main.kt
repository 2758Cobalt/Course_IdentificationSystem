package org.cobaltumapps



/** Точка входу */
fun main() {
    val mainProgram = MainProgram()

    mainProgram.launch()
}

/** Очищення консолі */
fun clearConsole() {
    repeat(50) { println() }
}