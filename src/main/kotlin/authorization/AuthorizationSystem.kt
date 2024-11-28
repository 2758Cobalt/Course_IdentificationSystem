package org.cobaltumapps.authorization

import org.cobaltumapps.registration.User
import org.cobaltumapps.user.journal.UserJournalListener
import org.cobaltumapps.user.managers.UserManager
import kotlin.system.exitProcess

/** Клас, що відповідає за систему авторизації користувачів
 * @constructor UserListManager - менеджер списку користувачів
 * @param authorizedUser Користувач, який вже авторизований
 * @author StanislavKolomoiets */
class AuthorizationSystem(
    private var userManager: UserManager? = null
) {
    private var authorizedUser: User? = null
    var journalListener: UserJournalListener? = null

    /**
     * Метод для авторизації користувача.
     * @param onAuthorized Лямбда, яка викликається після авторизації з передачею авторизованого користувача.
     * @return Повертає авторизованого користувача або `null`, якщо авторизація не відбулася.
     * @author Stanislav Kolomoiets
     */
    fun authorization(onAuthorized: (user: User?) -> Unit): User? {
        println("--- Авторизація ---")
        println("\nЗалиште поле пустим для виходу з програми")

        while (true) {
            // Запит імені користувача
            print("\nВведіть ім'я користувача: ")
            val username = readlnOrNull()?.trim() ?: ""

            // Якщо поле пусте, завершуємо програму
            if (username.isEmpty()) {
                journalListener?.w("m=\"Сесія була завершена\"");
                exitProcess(0)
            }

            // Запит паролю користувача
            print("Введіть пароль: ")
            val password = readlnOrNull()?.trim() ?: ""

            // Пошук користувач зі списку, ім'я та пароль якого співпадають
            val user = userManager?.getUserList()?.find { it.username == username && it.password == password }

            if (user != null) {
                // Якщо авторизація успішна - завершення циклу
                println("Авторизація успішна! Користувач ${user.username} авторизований.")
                authorizedUser = user
                journalListener?.d("u=\"${user.username}\" m=\"Увійшов до системи.\"")
                break

            // Якщо авторизація не успішна - повтор циклу
            } else {
                println("\nНевірне ім'я користувача або пароль.")
                journalListener?.w("u=\"$username\", p=\"$password\" m=\"Не вірне ім'я або пароль до системи.\"")
                authorizedUser = null
            }
        }
        // Виклик переданої лямбди з авторизованим користувачем
        onAuthorized.invoke(authorizedUser)

        // Повернення результату авторизації
        return authorizedUser
    }
}