package com.jicay.bookmanagement.infrastructure.driving.web

import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.infrastructure.driving.web.dto.BookDTO
import com.jicay.bookmanagement.infrastructure.driving.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/books")
class BookController(
    private val bookUseCase: BookUseCase
) {
    @CrossOrigin
    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks()
            .map { it.toDto() }
    }

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.toDomain())
    }

    @CrossOrigin
    @PostMapping("/{id}/reserve")
    @ResponseStatus(HttpStatus.OK)
    fun reserveBook(@PathVariable id: Int) {
        val success = bookUseCase.reserveBook(id)
        if (!success) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Livre introuvable ou déjà réservé")
        }
    }

}