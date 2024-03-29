package org.keddad.models

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement


@Serializable
data class Order(val author: Int, val status: String, val comment: String, val dishes: Map<Int, Int>)

class OrderService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_ORDERS =
            "CREATE TABLE IF NOT EXISTS ORDERS (ID INT GENERATED BY DEFAULT AS IDENTITY, STATUS VARCHAR(512) NOT NULL, COMMENT VARCHAR(512), AUTHOR INTEGER NOT NULL)"

        private const val INSERT_ORDER = "INSERT INTO ORDERS (STATUS, COMMENT, AUTHOR) VALUES (?, ?, ?)"
        private const val SELECT_ORDER_BY_ID =
            "SELECT STATUS, COMMENT, AUTHOR FROM ORDERS WHERE ID = ?"
    }

    init {
        val statement = connection.createStatement()
        statement.execute(CREATE_TABLE_ORDERS)
    }

    suspend fun create(order: Order): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, order.status)
        statement.setString(2, order.comment)
        statement.setInt(3, order.author)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted order with query $statement")
        }
    }

    suspend fun read(id: Int): Order? = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_ORDER_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            return@withContext Order(
                resultSet.getInt("AUTHOR"),
                resultSet.getString("STATUS")!!,
                resultSet.getString("COMMENT")!!,
                mapOf()
            )
        } else {
            return@withContext null
        }
    }
}