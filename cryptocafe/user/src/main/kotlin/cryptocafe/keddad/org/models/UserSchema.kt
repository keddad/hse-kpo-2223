package cryptocafe.keddad.org.models

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
data class User(var username: String, var email: String, var password: String, var isManager: Boolean)

@Serializable
data class UserWithId(var id: Int, var username: String, var email: String, var password: String, var isManager: Boolean)

class UserService(private val connection: Connection) {
    companion object {
        // INT GENERATED BY DEFAULT AS IDENTITY is pgsql speific
        // For debug I use H2, which uses "ID IDENTITY", which doesn't work with pgsql
        // That means that debugging outside of Docker is stupidly hard now
        private const val CREATE_TABLE_USERS =
            "CREATE TABLE IF NOT EXISTS USERS (ID INT GENERATED BY DEFAULT AS IDENTITY , EMAIL VARCHAR(512) NOT NULL, PASSWORD VARCHAR(512) NOT NULL, USERNAME VARCHAR(512) NOT NULL UNIQUE, IS_MANAGER BOOLEAN)"

        private const val INSERT_USER = "INSERT INTO USERS (EMAIL, PASSWORD, USERNAME, IS_MANAGER) VALUES (?, ?, ?, ?)"
        private const val SELECT_USER_BY_USERNAME =
            "SELECT ID, EMAIL, PASSWORD, USERNAME, IS_MANAGER FROM USERS WHERE USERNAME = ?"
    }

    init {
        val statement = connection.createStatement()
        statement.execute(CREATE_TABLE_USERS)
    }

    suspend fun create(user: User): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, user.email)
        statement.setString(2, user.password)
        statement.setString(3, user.username)
        statement.setBoolean(4, user.isManager)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted user with query $statement")
        }
    }

    suspend fun read(username: String): UserWithId? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_USER_BY_USERNAME)
        statement.setString(1, username)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext UserWithId(
                resultSet.getInt("ID"),
                resultSet.getString("USERNAME")!!,
                resultSet.getString("EMAIL")!!,
                resultSet.getString("PASSWORD")!!,
                resultSet.getBoolean("IS_MANAGER")
            )
        } else {
            return@withContext null
        }
    }
}