package org.cobaltumapps.registration

// Дані про каталог або директорію
data class Directory(
    val name: String,
    val permissions: String
)

// Дані користувач
data class User(
    val username: String,
    val password: String,
    val role: String,
    val directories: List<Directory>
)
// Відповідь з JSON після парсингу даних. Містить набір даних "User"
data class UsersResponse(
    val users: List<User>
)