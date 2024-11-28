package org.cobaltumapps.registration

import org.cobaltumapps.encryption.EncryptionManager
import org.cobaltumapps.user.UserRole
import org.cobaltumapps.user.journal.UserJournalListener
import org.cobaltumapps.user.managers.UserManager

class RegistrationSystem(
    private var userManager: UserManager? = null
) {
    var journalListener: UserJournalListener? = null
    var encryptionManager: EncryptionManager? = null
    private val maxUsers = 8

    // Реєструє нового користувача
    fun registerNewUser() {
        if (userManager?.getUserList()?.size!! >= maxUsers) {
            println("Не можливо зареєструвати. Кількість користувачів (N) > $maxUsers")
        }

        println("--- Реєстрація нового користувача ---")

        println("Введіть ім'я користувача:")
        val username = readlnOrNull() ?: ""



        println("Введіть пароль користувача:")
        val password = readlnOrNull() ?: ""



        println("Введіть роль користувача:")
        val role = readlnOrNull() ?: "guest"



        println("Введіть каталоги та їх права у форматі: A(W,E,R); B(R,A)")
        val directories = readlnOrNull() ?: ""

        if (directories.isEmpty()) {
            println("Права до каталогів не можуть бути пустими")
        }


        userManager?.addNewUser(User(username, password, role, parseUserInput(directories)))
        journalListener?.w("Зареєстрований новий користувач $username на роль $role. Ключ шифрування юзера починається з ...")
    }

    private fun parseUserInput(input: String): List<Directory> {
        // Регулярний вираз для парсингу вводу
        val regex = """([A-Za-z]+)\(([^)]+)\)""".toRegex()

        // Знаходимо всі матчі за регулярним виразом
        val matches = regex.findAll(input)

        // Перетворюємо матчі в список об'єктів Directory
        return matches.map { match ->
            val name = match.groupValues[1]  // Назва каталогу
            val permissions = match.groupValues[2]  // Строка з правами
            Directory(name, permissions)  // Створюємо об'єкт Directory
        }.toList()
    }


    // Видаляє вказаного користувача
    fun removeUser(username: String) {
        userManager?.removeUserAtName(username)
        journalListener?.d("Користувач $username був видалений з системи.")
    }
}

