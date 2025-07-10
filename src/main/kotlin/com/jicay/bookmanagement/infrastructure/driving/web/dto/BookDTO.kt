package com.jicay.bookmanagement.infrastructure.driving.web.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.jicay.bookmanagement.domain.model.Book

data class BookDTO(
    val name: String, 
    val author: String, 
    @get:JsonProperty("isReserved") val isReserved: Boolean = false
) {
    fun toDomain(): Book {
        return Book(
            name = this.name,
            author = this.author,
            isReserved = this.isReserved
        )
    }
}

fun Book.toDto() = BookDTO(
    name = this.name,
    author = this.author,
    isReserved = this.isReserved
)