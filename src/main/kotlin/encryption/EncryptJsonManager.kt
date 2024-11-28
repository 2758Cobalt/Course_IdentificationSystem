package org.cobaltumapps.encryption

import com.google.gson.Gson
import org.cobaltumapps.registration.UsersResponse
import java.io.File

// Дані для файлу close.json (зашифровані повідомлення)
data class CloseJsonData(val username: String, val encryptedText: String)

// Дані для файлу out.json (розшифровані повідомлення)
data class OutJsonData(val username: String, val decryptedText: String)

class EncryptJsonManager {
    // Шлях до файлів JSON, які використовуються системою
    private val fileInputJson = File("H:\\Code\\Repos\\intelliProjects\\Course_IdentificationSystem\\src\\main\\kotlin\\encryption\\input.json")
    private val fileCloseJson = File("H:\\Code\\Repos\\intelliProjects\\Course_IdentificationSystem\\src\\main\\kotlin\\encryption\\close.json")
    private val fileOutJson = File("H:\\Code\\Repos\\intelliProjects\\Course_IdentificationSystem\\src\\main\\kotlin\\encryption\\out.json")

    // Ініціалізація об'єкта Gson для роботи з JSON
    private val gson = Gson()

    // Локальна змінна для збереження вхідного списку повідомлень
    private var inputResponse = InputResponse(mutableListOf())

    /**
     * Парсинг JSON-файлу input.json.
     *
     * @return об'єкт InputResponse, який містить список вхідних повідомлень.
     */
    fun parseJson(): InputResponse {
        return gson.fromJson(fileInputJson.readText(Charsets.UTF_8), InputResponse::class.java)
    }

    /**
     * Додавання нового вхідного повідомлення до файлу input.json.
     *
     * @param inputItem об'єкт InputItem, що містить ім'я користувача та текст повідомлення.
     */
    fun addNewInputText(inputItem: InputItem) {
        val jsonString = gson.toJson(inputItem)
        fileInputJson.writeText(jsonString) // Запис повідомлення у файл input.json
    }

    /**
     * Отримання розшифрованого повідомлення користувача із файлу out.json.
     *
     * @return об'єкт OutJsonData, що містить розшифроване повідомлення та ім'я користувача.
     */
    fun getUserMessage(): OutJsonData {
        val outData = gson.fromJson(fileOutJson.readText(Charsets.UTF_8), OutJsonData::class.java)
        return outData
    }

    /**
     * Додавання зашифрованого повідомлення до файлу close.json.
     *
     * @param username ім'я користувача, до якого прив'язано повідомлення.
     * @param encryptedText зашифроване повідомлення у вигляді рядка.
     */
    fun addToCloseJson(username: String, encryptedText: String) {
        val closeData = CloseJsonData(username, encryptedText)
        val jsonString = gson.toJson(closeData)
        fileCloseJson.writeText(jsonString) // Запис даних у файл close.json
    }

    /**
     * Додавання розшифрованого повідомлення до файлу out.json.
     *
     * @param username ім'я користувача, до якого прив'язано повідомлення.
     * @param decryptedText розшифроване повідомлення у вигляді рядка.
     */
    fun addToOutJson(username: String, decryptedText: String) {
        val outData = OutJsonData(username, decryptedText)
        val jsonString = gson.toJson(outData)
        fileOutJson.writeText(jsonString) // Запис даних у файл out.json
    }
}