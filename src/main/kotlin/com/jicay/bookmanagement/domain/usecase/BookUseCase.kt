package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

    fun reserveBook(id: Int): Boolean {
        val book = bookPort.getBookById(id)
        if (book == null) {
            return false
        }
        
        if (book.isReserved) {
            return false
        }
        
        return bookPort.reserveBook(id)
    }
}