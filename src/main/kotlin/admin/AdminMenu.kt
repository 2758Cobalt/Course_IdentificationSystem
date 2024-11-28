package org.cobaltumapps.admin

import org.cobaltumapps.*
import org.cobaltumapps.authentication.AuthenticationRegisterAction
import org.cobaltumapps.authentication.AuthenticationStatusListener
import org.cobaltumapps.encryption.EncryptionManager
import org.cobaltumapps.registration.RegistrationSystem
import org.cobaltumapps.registration.User
import org.cobaltumapps.user.journal.UserJournalListener
import org.cobaltumapps.user.managers.UserManager

/**
 * Клас AdminMenu надає функціональність для управління меню адміністратора.
 * Адміністратор може переглядати список користувачів, реєструвати нових користувачів, видаляти користувачів
 * і виходити з системи.
 * Підтримує автентифікацію користувача та запис подій в журнал.
 */
class AdminMenu(
    private var userManager: UserManager? = null
): AuthenticationStatusListener {
    var encryptionManager: EncryptionManager? = null

    // Слухач для журналу подій
    var journalListener: UserJournalListener? = null

    // Об'єкт для реєстрації користувачів
    var authRegisterAction: AuthenticationRegisterAction? = null

    // Прапорець автентифікації
    private var isAuthenticated = true

    /**
     * Відкриває меню адміністратора та обробляє вибір користувача.
     * В залежності від введеного вибору виконується одна з адміністративних функцій.
     * @param user Поточний користувач, що виконує дії в меню.
     */
    fun openAdminMenu(user: User) {
        clearConsole()
        isAuthenticated = true
        do  {
            authRegisterAction?.registerUserAction()
            if (!isAuthenticated) break

            // Виведення меню адміністратора
            println("--- Меню адміністратора ---\n")
            println("1. Список користувачів")
            println("2. Реєстрація користувача")
            println("3. Видалення користувача")
            println("4. Відправити повідомлення резиденту")
            println("5. Вийти з системи")
            print("Оберіть пункт меню: ")

            val input = readlnOrNull() ?: ""
            when (input.trim()) {
                "1" -> {
                    clearConsole()
                    displayUsers()
                    journalListener?.d("u=\"${user.username}\" m=\"Адміністратор продивився список користувачів.\"")
                }
                "2" -> {
                    clearConsole()
                    registerNewUser()
                    journalListener?.d("u=\"${user.username}\" m=\"Адміністратор зареєстрував нового користувача.\"")
                }
                "3" -> {
                    clearConsole()
                    deleteUser()
                    journalListener?.d("u=\"${user.username}\" m=\"Адміністратор видалив користувача.\"")
                }
                "4" -> {
                    clearConsole()
                    sendMessage()
                }
                "5" -> {
                    clearConsole()
                    println("Вихід з системи адміністратора.")
                    authRegisterAction?.resetCounter()
                    journalListener?.d("u=\"${user.username}\" m=\"Адміністратор вийшов з системи.\"")
                    break
                }
                else -> {
                    println("Невірний вибір. Будь ласка, оберіть пункт з меню.")
                    journalListener?.d("u=\"${user.username}\" m=\"Адміністратор обрав неіснуючу команду.\"")
                }
            }
            println()
        } while (isAuthenticated)
    }

    /**
     * Функція для відображення списку користувачів.
     * Виводить список усіх зареєстрованих користувачів із їхніми даними.
     */
    private fun displayUsers() {
        println("--- Список користувачів ---")
        val userList = userManager?.getUserList()

        if (userList?.isNotEmpty() == true) {
            userList.forEach { user: User ->
                println("\nІм'я \'${user.username}\' Пароль \'${user.password}\' Роль: \'${user.role}\'\n")
                user.directories.forEach {
                    println("Директорія ${it.name} => ${it.permissions}")
                }
            }
        }
    }

    /**
     * Функція для реєстрації нового користувача.
     * Використовує клас RegistrationSystem для виконання реєстрації.
     */
    private fun registerNewUser() {
        val registerUserMenu = RegistrationSystem(userManager)
        registerUserMenu.encryptionManager = this.encryptionManager
        registerUserMenu.registerNewUser()
    }

    /**
     * Функція для видалення користувача з системи.
     * Користувач видаляється за його іменем.
     */
    private fun deleteUser() {
        println("--- Видалення користувача ---")

        println("Введіть ім'я користувача для видалення:")
        val username = readlnOrNull() ?: ""

        if (username.isNotEmpty()) {
            userManager?.removeUserAtName(username)
        }
    }

    private fun sendMessage() {
        println("--- Відправити повідомлення резиденту ---")

        println("Введіть ім'я резиденту:")
        val username = readlnOrNull() ?: ""

        if (username.isNotEmpty()) {
            println("Введіть повідомлення:\n")

            val msg = readlnOrNull() ?: ""

            if (msg.isNotEmpty()) {
                val user = userManager?.getUser(username)

                if (user != null) {
                    encryptionManager?.writeUserMessage(user.username, msg)
                    val encryptedData = encryptionManager?.writeEncryptedText(user.username, user.password, msg)
                    encryptionManager?.writeDecryptedText(user.username, encryptedData!!)
                }
                println("Повідомлення надіслано юзеру ${user?.username}")
            }
        }
    }

    /**
     * Обробка невдалого входу при автентифікації.
     * Встановлюється прапорець isAuthenticated в false.
     */
    override fun authenticationOnFailed() {
        isAuthenticated = false
        println("Аутентифікацію не пройдено. Вихід з системи.")
        journalListener?.e("m=\"Адміністратор не пройшов автентифікацію.\"")
    }

    /**
     * Обробка успішного входу при автентифікації.
     * Встановлюється прапорець isAuthenticated в true.
     */
    override fun authenticationOnSuccessful() {
        journalListener?.d("m=\"Адміністратор пройшов автентифікацію.\"")
        isAuthenticated = true
    }
}
