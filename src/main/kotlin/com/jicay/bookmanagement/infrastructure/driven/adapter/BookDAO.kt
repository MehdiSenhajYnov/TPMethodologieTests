package com.jicay.bookmanagement.infrastructure.driven.adapter

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): BookPort {
    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    isReserved = rs.getBoolean("is_reserved")
                )
            }
    }

    override fun createBook(book: Book) {
        namedParameterJdbcTemplate
            .update("INSERT INTO BOOK (title, author, is_reserved) values (:title, :author, :isReserved)", mapOf(
                "title" to book.name,
                "author" to book.author,
                "isReserved" to book.isReserved
            ))
    }

    override fun getBookById(id: Int): Book? {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK WHERE id = :id", mapOf("id" to id)) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    isReserved = rs.getBoolean("is_reserved")
                )
            }.firstOrNull()
    }

    override fun reserveBook(id: Int): Boolean {
        val rowsAffected = namedParameterJdbcTemplate
            .update("UPDATE BOOK SET is_reserved = true WHERE id = :id AND is_reserved = false", mapOf("id" to id))
        return rowsAffected > 0
    }
}