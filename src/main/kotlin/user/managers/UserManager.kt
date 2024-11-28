package org.cobaltumapps.user.managers

import com.google.gson.Gson
import org.cobaltumapps.registration.User
import org.cobaltumapps.registration.UsersResponse
import java.io.File

/**
 * Клас `UserManager` відповідає за управління користувачами у системі.
 * Він забезпечує зберігання, отримання, додавання, видалення та синхронізацію даних користувачів із JSON-файлом.
 *
 * @param fileJson JSON-файл, у якому зберігаються дані про користувачів.
 * @author StanislavKolomoiets
 */
class UserManager(
    private val fileJson: File
) {
    // Об'єкт, який містить список користувачів, завантажених із JSON-файлу.
    private var usersResponse = UsersResponse(listOf())

    // Бібліотека для роботи з JSON.
    private var gson: Gson = Gson()

    /**
     * Зчитує дані про користувачів із JSON-файлу та парсить їх у об'єкт `UsersResponse`.
     *
     * @return Об'єкт `UsersResponse`, що містить список користувачів.
     */
    fun parseJson(): UsersResponse {
        usersResponse = gson.fromJson(fileJson.readText(Charsets.UTF_8), UsersResponse::class.java)
        return usersResponse
    }

    /**
     * Повертає користувача за його іменем.
     *
     * @param username Ім'я користувача для пошуку.
     * @return Об'єкт `User`, якщо користувача знайдено, або `null`, якщо користувач відсутній.
     */
    fun getUser(username: String): User? {
        return usersResponse.users.find { it.username == username }
    }

    /**
     * Повертає список усіх користувачів.
     *
     * @return Список користувачів у вигляді `List<User>`.
     */
    fun getUserList(): List<User> {
        return usersResponse.users
    }

    /**
     * Додає нового користувача до списку та оновлює JSON-файл.
     *
     * @param user Новий користувач для додавання.
     */
    fun addNewUser(user: User) {
        usersResponse.users.addLast(user)
        updateJson()
    }

    /**
     * Видаляє користувача зі списку за його іменем та оновлює JSON-файл.
     *
     * @param username Ім'я користувача для видалення.
     */
    fun removeUserAtName(username: String) {
        val userList = usersResponse.users.toMutableList()
        userList.forEach {
            if (it.username == username)
                println("Користувач $username був видалений.")
        }
        userList.removeIf { it.username == username }
        usersResponse = UsersResponse(userList)
        updateJson()
    }

    /**
     * Оновлює JSON-файл відповідно до поточного стану списку користувачів.
     */
    private fun updateJson() {
        val jsonString = gson.toJson(usersResponse)
        fileJson.writeText(jsonString)
    }
}
