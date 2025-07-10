package com.jicay.bookmanagement.infrastructure.driving.web.dto

import com.jicay.bookmanagement.domain.model.Book

data class BookDTO(val name: String, val author: String) {
    fun toDomain(): Book {
        return Book(
            name = this.name,
            author = this.author
        )
    }
}

fun Book.toDto() = BookDTO(
    name = this.name,
    author = this.author
)