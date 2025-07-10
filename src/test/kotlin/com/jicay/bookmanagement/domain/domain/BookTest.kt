package com.jicay.bookmanagement.domain.domain

import com.jicay.bookmanagement.domain.model.Book
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class BookTest : FunSpec({

    test("name should not be empty") {
        shouldThrow<IllegalArgumentException> { Book("", "Victor Hugo") }
    }

    test("author should not be empty") {
        shouldThrow<IllegalArgumentException> { Book("Les Misérables", "") }
    }

})