package com.jicay.bookmanagement.infrastructure.application

import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.infrastructure.driven.adapter.BookDAO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {
    @Bean
    fun bookUseCase(bookDAO: BookDAO): BookUseCase {
        return BookUseCase(bookDAO)
    }
}