package org.cobaltumapps.encryption

import java.util.Base64
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class EncryptionSystemDES {

    /**
     * Метод для шифрування текстового повідомлення за допомогою алгоритму DES.
     * @param text текстове повідомлення, яке потрібно зашифрувати.
     * @param secretKey секретний ключ для шифрування.
     * @return зашифроване повідомлення у форматі Base64.
     */
    fun encryptDES(text: String, secretKey: SecretKey): String {
        // Ініціалізація об'єкта Cipher для шифрування з використанням DES у режимі ECB та PKCS5Padding.
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        // Шифрування текстового повідомлення.
        val encryptedBytes = cipher.doFinal(text.toByteArray())

        // Повернення зашифрованого тексту у форматі Base64.
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    /**
     * Метод для розшифрування повідомлення за допомогою алгоритму DES.
     * @param encryptedText зашифроване повідомлення у форматі Base64.
     * @param secretKey секретний ключ для розшифрування.
     * @return початкове розшифроване повідомлення.
     */
    fun decryptDES(encryptedText: String, secretKey: SecretKey): String {
        // Ініціалізація об'єкта Cipher для розшифрування з використанням DES у режимі ECB та PKCS5Padding.
        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        // Декодування зашифрованого тексту з формату Base64 до масиву байтів.
        val decodedBytes = Base64.getDecoder().decode(encryptedText)

        // Розшифрування повідомлення.
        val decryptedBytes = cipher.doFinal(decodedBytes)

        // Повернення початкового текстового повідомлення.
        return String(decryptedBytes)
    }

    /**
     * Метод для генерації секретного ключа DES на основі вхідного тексту.
     * DES вимагає ключ довжиною рівно 8 байт, тому ключ обрізається або доповнюється.
     * @param key текстовий ключ, введений користувачем.
     * @return об'єкт SecretKey для використання в алгоритмі DES.
     */
    fun generateKey(key: String): SecretKey {
        // Конвертація введеного ключа в масив байтів.
        val keyBytes = key.toByteArray()

        // Якщо ключ довший за 8 байт — обрізаємо його. Якщо коротший — доповнюємо нулями.
        val truncatedKey = if (keyBytes.size > 8) {
            Arrays.copyOfRange(keyBytes, 0, 8) // Залишаємо лише перші 8 байт.
        } else {
            keyBytes.copyOf(8) // Доповнюємо ключ до 8 байт нулями.
        }

        // Повертаємо SecretKeySpec для використання в DES.
        return SecretKeySpec(truncatedKey, "DES")
    }
}

/**
 * Клас для збереження списку вхідних повідомлень.
 * @param inputTextList список текстових повідомлень.
 */
data class InputResponse(
    val inputTextList: List<InputItem>
)

/**
 * Клас для опису вхідного текстового елемента.
 * @param username ім'я користувача.
 * @param input_message текстове повідомлення.
 */
data class InputItem(
    val username: String,
    val input_message: String
)

/**
 * Клас для зберігання зашифрованих даних.
 * @param encryptedMessage зашифроване повідомлення.
 * @param secretKey ключ шифрування.
 */
data class EncryptedData(
    var encryptedMessage: String,
    var secretKey: String
)
