package org.cobaltumapps.user.journal

interface UserJournalListener {
    fun d(msg: String)
    fun w(msg: String)
    fun e(msg: String)
}