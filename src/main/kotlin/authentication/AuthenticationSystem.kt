package org.cobaltumapps.authentication

import org.cobaltumapps.clearConsole
import java.io.File

/**
 * Клас, що відповідає за систему автентифікації користувача
 * @author StanislavKolomoiets */
class AuthenticationSystem: AuthenticationRegisterAction {
    private val step = 1 // Крок періоду перевірки (T)
    private val stepMax = 2 // Максимальна кількість кроків до автентифікації

    private var stepCounter = 0 // Лічильник кроків

    var currentListener: AuthenticationStatusListener? = null // Слухач для автентифікації

    // Функція для запуску автентифікації
    private fun launchAuthentication(onAuth: (onAuth: Boolean) -> Unit) {
        println("--- Автентифікація користувача ---")
        println("\nДля успішної автентифікації дайте відповідь на рівняння:\n")

        val question = requestQuestion() // Отримання питання
        println(question.first) // Виведення питання

        val result = readlnOrNull() ?: "" // Читання введеної відповіді

        // Перевірка відповіді на правильність
        if (result.isNotEmpty() && result.toDouble() == question.second) {
            clearConsole()
            println("Відповідь вірна. Автентифікацію пройдено.")
            onAuth.invoke(true) // Викликається функція з параметром true при успіху
        } else {
            clearConsole()
            println("Відповідь невірна. Автентифікацію скасовано.")
            onAuth.invoke(false) // Викликається функція з параметром false при помилці
        }
    }

    // Реалізація методу для реєстрації користувача
    override fun registerUserAction() {
        if (stepCounter <= stepMax) {
            stepCounter += step // Поступовий крок реєстрації
        } else {
            resetCounter() // Скидання лічильника після досягнення максимального кроку

            // Запуск автентифікації
            launchAuthentication {
                if (it)
                    currentListener?.authenticationOnSuccessful() // Якщо успішно, викликаємо onSuccessful
                else
                    currentListener?.authenticationOnFailed() // Якщо неуспішно, викликаємо onFailed
            }
        }
    }

    // Функція для скидання лічильника
    override fun resetCounter() {
        stepCounter = 0
    }

    // Отримання випадкового питання
    private fun requestQuestion(): Pair<String, Double> {
        val questions = getQuestionsList() // Отримуємо список питань
        return questions.random() // Повертаємо випадкове питання з відповіді
    }

    // Читання питань з файлу
    private fun getQuestionsList(): List<Pair<String, Double>> {
        val questions = mutableMapOf<String, Double>()
        File("H:\\Code\\Repos\\intelliProjects\\Course_IdentificationSystem\\src\\main\\kotlin\\authentication\\ask.txt").forEachLine { line ->
            val parts = line.split(":") // Розділяємо питання та відповідь по символу ":"
            if (parts.size == 2) {
                questions[parts[0].trim()] = parts[1].toDouble() // Додаємо питання та відповідь до мапи
            }
        }
        return questions.toList() // Повертаємо список питань
    }
}

// Інтерфейс для слухачів статусу автентифікації
interface AuthenticationStatusListener {
    fun authenticationOnFailed() // Метод для обробки невдалої автентифікації
    fun authenticationOnSuccessful() // Метод для обробки успішної автентифікації
}

// Інтерфейс для дій реєстрації користувача
interface AuthenticationRegisterAction {
    fun registerUserAction() // Метод для запуску дії реєстрації
    fun resetCounter() // Метод для скидання лічильника
}
