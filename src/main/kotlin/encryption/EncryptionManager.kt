package org.cobaltumapps.encryption

import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class EncryptionManager {
    // Об'єкт для роботи із системою шифрування DES
    private val encryptionSystemDES = EncryptionSystemDES()

    // Об'єкт для роботи з файлами JSON
    private val encryptJsonManager = EncryptJsonManager()

    /**
     * Шифрує повідомлення за допомогою алгоритму DES.
     * @param password пароль користувача, на основі якого генерується ключ.
     * @param message текст повідомлення, яке потрібно зашифрувати.
     * @return об'єкт `EncryptedData`, який містить зашифроване повідомлення і ключ у форматі String.
     */
    fun encryptMessage(password: String, message: String): EncryptedData {
        val secretKey = generateUserKey(password)  // Генерація секретного ключа
        val encryptedMessage = encryptionSystemDES.encryptDES(message, secretKey)  // Шифрування повідомлення
        val keyString = Base64.getEncoder().encodeToString(secretKey.encoded)  // Ключ у форматі Base64
        return EncryptedData(encryptedMessage, keyString)
    }

    /**
     * Розшифровує повідомлення за допомогою алгоритму DES.
     * @param encryptedMessage зашифроване повідомлення.
     * @param keyString ключ у форматі String, який використовується для розшифрування.
     * @return розшифроване повідомлення у вигляді рядка.
     */
    fun decryptMessage(encryptedMessage: String, keyString: String): String {
        val secretKey = getSecretKeyFromString(keyString)  // Перетворення рядка у SecretKey
        return encryptionSystemDES.decryptDES(encryptedMessage, secretKey)  // Розшифрування повідомлення
    }

    /**
     * Генерує унікальний ключ для користувача на основі його пароля.
     * @param password пароль користувача.
     * @return об'єкт `SecretKey`, який використовується для шифрування.
     */
    fun generateUserKey(password: String): SecretKey {
        return encryptionSystemDES.generateKey(password)
    }

    /**
     * Зберігає повідомлення користувача у файлі `input.json`.
     * @param username ім'я користувача.
     * @param msg текст повідомлення.
     */
    fun writeUserMessage(username: String, msg: String) {
        encryptJsonManager.addNewInputText(InputItem(username, msg))
    }

    /**
     * Отримує останнє розшифроване повідомлення користувача з файлу `out.json`.
     * @return об'єкт `OutJsonData`, який містить ім'я користувача і розшифроване повідомлення.
     */
    fun getUserMessage(): OutJsonData {
        return encryptJsonManager.getUserMessage()
    }

    /**
     * Зберігає зашифроване повідомлення у файлі `close.json`.
     * @param username ім'я користувача.
     * @param password пароль користувача для генерації ключа.
     * @param msg текст повідомлення для шифрування.
     * @return об'єкт `EncryptedData`, який містить зашифроване повідомлення і ключ.
     */
    fun writeEncryptedText(username: String, password: String, msg: String): EncryptedData {
        val encryptedData = encryptMessage(password, msg)
        encryptJsonManager.addToCloseJson(username, encryptedData.encryptedMessage)
        return encryptedData
    }

    /**
     * Зберігає розшифроване повідомлення у файлі `out.json`.
     * @param username ім'я користувача.
     * @param encryptedData об'єкт `EncryptedData`, який містить зашифроване повідомлення і ключ.
     */
    fun writeDecryptedText(username: String, encryptedData: EncryptedData) {
        val decryptedText = decryptMessage(encryptedData.encryptedMessage, encryptedData.secretKey)
        encryptJsonManager.addToOutJson(username, decryptedText)
    }

    /**
     * Перетворює ключ із формату String (Base64) у об'єкт `SecretKey`.
     * @param keyString ключ у форматі Base64.
     * @return об'єкт `SecretKey`, готовий для використання у шифруванні/розшифруванні.
     */
    private fun getSecretKeyFromString(keyString: String): SecretKey {
        val decodedKey = Base64.getDecoder().decode(keyString)  // Декодування Base64
        return SecretKeySpec(decodedKey, "DES")  // Створення ключа на основі масиву байтів
    }
}