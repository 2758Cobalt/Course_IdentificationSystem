package org.cobaltumapps.user.journal

import java.io.File
import java.io.FileNotFoundException

/**
 * Клас UserJournal реалізує інтерфейс UserJournalListener і відповідає за запис різних повідомлень у файл журналу.
 * Це дозволяє зберігати повідомлення різного рівня важливості (DEBUG, WARNING, ERROR) для подальшого аналізу.
 */
class UserJournal: UserJournalListener {

    // Шлях до файлу журналу, у якому будуть зберігатися повідомлення
    private val journalFile =
        File("H:\\Code\\Repos\\intelliProjects\\Course_IdentificationSystem\\src\\main\\kotlin\\us_book.txt")

    init {
        // Перевірка наявності файлу журналу, якщо файл не існує - кидається виключення
        if (!journalFile.exists()) {
            throw FileNotFoundException("Incorrect path file")
        }
    }

    /**
     * Метод для запису повідомлення рівня DEBUG у файл журналу.
     *
     * @param msg Повідомлення, яке потрібно записати в журнал
     */
    override fun d(msg: String) {
        journalFile.appendText("[DEBUG] >\t$msg;\n")
    }

    /**
     * Метод для запису повідомлення рівня WARNING у файл журналу.
     *
     * @param msg Повідомлення, яке потрібно записати в журнал
     */
    override fun w(msg: String) {
        journalFile.appendText("[WARNING] >\t$msg;\n")
    }

    /**
     * Метод для запису повідомлення рівня ERROR у файл журналу.
     *
     * @param msg Повідомлення, яке потрібно записати в журнал
     */
    override fun e(msg: String) {
        journalFile.appendText("[ERROR] >\t$msg;\n")
    }
}
