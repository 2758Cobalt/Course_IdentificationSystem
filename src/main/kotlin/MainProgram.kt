package org.cobaltumapps

import org.cobaltumapps.admin.AdminMenu
import org.cobaltumapps.authentication.AuthenticationSystem
import org.cobaltumapps.authorization.AuthorizationSystem
import org.cobaltumapps.encryption.EncryptionManager
import org.cobaltumapps.user.GuestMenu
import org.cobaltumapps.user.UserRole
import org.cobaltumapps.user.journal.UserJournal
import org.cobaltumapps.user.managers.UserManager
import java.io.File

class MainProgram {
    companion object {
        const val usernameListJson = "H:\\Code\\Repos\\intelliProjects\\Course_IdentificationSystem\\src\\main\\kotlin\\username.json"
    }

    private val userManager = UserManager(File(usernameListJson))
    private val encryptionManager = EncryptionManager()

    private val authorizationMenu = AuthorizationSystem(userManager)

    private val authenticationMenu = AuthenticationSystem()

    // Ініціалізуємо меню адміністратора та користувача
    private val adminMenu = AdminMenu(userManager)
    private val guestMenu = GuestMenu(userManager)

    private val userJournal = UserJournal()
    init {
        userManager.parseJson()
        authorizationMenu.journalListener = userJournal
        adminMenu.journalListener = userJournal
        guestMenu.journalListener = userJournal

        adminMenu.apply {
            authRegisterAction = this@MainProgram.authenticationMenu
            encryptionManager = this@MainProgram.encryptionManager
        }
        guestMenu.apply {
            authRegisterAction = this@MainProgram.authenticationMenu
            encryptionManager = this@MainProgram.encryptionManager
        }
    }
    fun launch() {
        while (true) {
            // Запускаємо ауторизацію
            authorizationMenu.authorization { user ->
                when(user?.role) {

                    // Якщо роль користувача - адміністратор
                    UserRole.admin.name -> {
                        authenticationMenu.currentListener = adminMenu
                        adminMenu.openAdminMenu(user)
                    }

                    // Якщо роль користувача - гість (звичайний користувач)
                    UserRole.guest.name -> {
                        authenticationMenu.currentListener = guestMenu
                        guestMenu.openUserMenu(user)
                    }
                }
            }
        }
    }
}