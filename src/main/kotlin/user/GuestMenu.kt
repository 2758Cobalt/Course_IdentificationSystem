package org.cobaltumapps.user

import org.cobaltumapps.authentication.AuthenticationRegisterAction
import org.cobaltumapps.authentication.AuthenticationStatusListener
import org.cobaltumapps.clearConsole
import org.cobaltumapps.encryption.EncryptionManager
import org.cobaltumapps.registration.User
import org.cobaltumapps.user.journal.UserJournalListener
import org.cobaltumapps.user.managers.UserManager

/**
 * Клас GuestMenu надає функціональність для управління меню користувача.
 * Підтримує автентифікацію користувача та запис подій в журнал.*/
class GuestMenu(
    private var userManager: UserManager? = null
): AuthenticationStatusListener {
    var journalListener: UserJournalListener? = null // Слухач для ведення журналу

    var authRegisterAction: AuthenticationRegisterAction? = null // Дія для реєстрації користувача

    var encryptionManager: EncryptionManager? = null

    private var isAuthenticated = true // Прапорець автентифікації

    // Функція для відкриття меню користувача
    fun openUserMenu(user: User) {
        clearConsole()
        isAuthenticated = true
        do {
            authRegisterAction?.registerUserAction() // Виконання реєстрації користувача, якщо необхідно

            // Якщо автентифікація не пройдена, виходимо з меню
            if (!isAuthenticated) break

            // Виведення меню користувача
            println("--- Меню користувача ---\n")
            println("1. Інформація про користувача (Ім'я, пароль, доступні директорії та права доступу)")
            println("2. Продивитися повідомлення від респондента")
            println("3. Вийти з системи")
            print("Оберіть пункт меню: ")

            // Читання введеного пункту меню
            val input = readlnOrNull() ?: ""
            when (input.trim()) {
                "1" -> {
                    clearConsole()
                    displayUserInfo(user) // Відображення інформації про користувача
                    journalListener?.d("u=\"${user.username}\" m=\"Користувач продивився свою інформацію.\"")
                }
                "2" -> {
                    clearConsole()
                    checkMessage(user)
                }
                "3" -> {
                    clearConsole()
                    println("Вихід з системи користувача.")
                    authRegisterAction?.resetCounter() // Скидання лічильника реєстрації
                    journalListener?.d("u=\"${user.username}\" m=\"Користувач вийшов з системи.\"")
                    break
                }
                else -> {
                    println("Невірний вибір. Будь ласка, оберіть пункт з меню.")
                    journalListener?.d("u=\"${user.username}\" m=\"Користувач обрав неіснуючу команду.\"")
                }
            }
            println()
        } while (isAuthenticated) // Повторення до успішної автентифікації
    }

    // Функція для відображення інформації про користувача
    private fun displayUserInfo(user: User) {
        clearConsole()
        println("--- Інформація про користувача ${user.username} ---")
        val userList = userManager?.getUserList()

        if (userList?.isNotEmpty() == true) {
            val foundedUser = userList.find { it.username == user.username }
                println("\nІм'я \'${foundedUser?.username}\'\nПароль \'${foundedUser?.password}\'\nРоль: \'${foundedUser?.role}\'\n")
                user.directories.forEach {
                    println("Директорія ${it.name} => ${it.permissions}") // Виведення директорій і прав доступу
                }
        }

        println("\nНатисніть Enter для повернення до меню: ")
        readln() // Очікування натискання Enter для повернення до меню
        clearConsole()
    }

    private fun checkMessage(user: User) {
        val outData = encryptionManager?.getUserMessage()
        if (user.username == outData?.username)
            println("Від респондента отримано повідомлення:\n\n\"${outData.decryptedText}\"\n")
        else
            println("Немає повідомлень від респондентів\n")

        println("\nНатисніть Enter для повернення до меню: ")
        readln() // Очікування натискання Enter для повернення до меню
        clearConsole()
    }

    // Обробник невдалого проходження автентифікації
    override fun authenticationOnFailed() {
        isAuthenticated = false
        println("Аутентифікацію не пройдено. Вихід з системи.")
        journalListener?.e("m=\"Користувач не пройшов автентифікацію.\"")
    }

    // Обробник успішного проходження автентифікації
    override fun authenticationOnSuccessful() {
        journalListener?.d("m=\"Користувач пройшов автентифікацію.\"")
        isAuthenticated = true
    }
}
