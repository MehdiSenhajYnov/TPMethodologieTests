package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    test("get all books should returns all books sorted by name") {
        val bookPort = mockk<BookPort>()
        val bookUseCase = BookUseCase(bookPort)
        
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo"),
            Book("Hamlet", "William Shakespeare")
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainExactly(
            Book("Hamlet", "William Shakespeare"),
            Book("Les Misérables", "Victor Hugo")
        )
    }

    test("add book") {
        val bookPort = mockk<BookPort>()
        val bookUseCase = BookUseCase(bookPort)
        
        justRun { bookPort.createBook(any()) }

        val book = Book("Les Misérables", "Victor Hugo")

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("reserve book should return true when book exists and is not reserved") {
        val bookPort = mockk<BookPort>()
        val bookUseCase = BookUseCase(bookPort)
        
        val book = Book("Les Misérables", "Victor Hugo", isReserved = false)
        every { bookPort.getBookById(1) } returns book
        every { bookPort.reserveBook(1) } returns true

        val result = bookUseCase.reserveBook(1)

        result shouldBe true
        verify(exactly = 1) { bookPort.getBookById(1) }
        verify(exactly = 1) { bookPort.reserveBook(1) }
    }

    test("reserve book should return false when book does not exist") {
        val bookPort = mockk<BookPort>()
        val bookUseCase = BookUseCase(bookPort)
        
        every { bookPort.getBookById(1) } returns null

        val result = bookUseCase.reserveBook(1)

        result shouldBe false
        verify(exactly = 1) { bookPort.getBookById(1) }
        verify(exactly = 0) { bookPort.reserveBook(any()) }
    }

    test("reserve book should return false when book is already reserved") {
        val bookPort = mockk<BookPort>()
        val bookUseCase = BookUseCase(bookPort)
        
        val book = Book("Les Misérables", "Victor Hugo", isReserved = true)
        every { bookPort.getBookById(1) } returns book

        val result = bookUseCase.reserveBook(1)

        result shouldBe false
        verify(exactly = 1) { bookPort.getBookById(1) }
        verify(exactly = 0) { bookPort.reserveBook(any()) }
    }

})