package com.jicay.bookmanagement.infrastructure.driven.adapter

import com.jicay.bookmanagement.domain.model.Book
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO
) : FunSpec() {
    init {
        extension(SpringExtension)

        beforeTest {
            performQuery(
                // language=sql
                "TRUNCATE TABLE book RESTART IDENTITY"
            )
        }

        test("get all books from db") {
            // GIVEN
            performQuery(
                // language=sql
                """
               insert into book (title, author, is_reserved)
               values 
                   ('Hamlet', 'Shakespeare', false),
                   ('Les fleurs du mal', 'Beaudelaire', false),
                   ('Harry Potter', 'Rowling', false);
            """.trimIndent()
            )

            // WHEN
            val res = bookDAO.getAllBooks()

            // THEN
            res.shouldContainExactlyInAnyOrder(
                Book("Hamlet", "Shakespeare", isReserved = false), 
                Book("Les fleurs du mal", "Beaudelaire", isReserved = false), 
                Book("Harry Potter", "Rowling", isReserved = false)
            )
        }

        test("create book in db") {
            // GIVEN
            // WHEN
            bookDAO.createBook(Book("Les misérables", "Victor Hugo"))

            // THEN
            val res = performQuery(
                // language=sql
                "SELECT * from book"
            )

            res shouldHaveSize 1
            assertSoftly(res.first()) {
                this["id"].shouldNotBeNull().shouldBeInstanceOf<Int>()
                this["title"].shouldBe("Les misérables")
                this["author"].shouldBe("Victor Hugo")
                this["is_reserved"].shouldBe(false)
            }
        }

        test("get book by id should return book when it exists") {
            // GIVEN
            performQuery(
                // language=sql
                """
                insert into book (title, author, is_reserved)
                values ('Les misérables', 'Victor Hugo', false);
            """.trimIndent()
            )

            // WHEN
            val res = bookDAO.getBookById(1)

            // THEN
            res.shouldNotBeNull()
            res.name.shouldBe("Les misérables")
            res.author.shouldBe("Victor Hugo")
            res.isReserved.shouldBe(false)
        }

        test("get book by id should return null when book does not exist") {
            // WHEN
            val res = bookDAO.getBookById(999)

            // THEN
            res.shouldBeNull()
        }

        test("reserve book should return true when book exists and is not reserved") {
            // GIVEN
            performQuery(
                // language=sql
                """
                insert into book (title, author, is_reserved)
                values ('Les misérables', 'Victor Hugo', false);
            """.trimIndent()
            )

            // WHEN
            val result = bookDAO.reserveBook(1)

            // THEN
            result.shouldBe(true)
            
            val res = performQuery(
                // language=sql
                "SELECT * from book WHERE id = 1"
            )
            res.shouldHaveSize(1)
            res.first()["is_reserved"].shouldBe(true)
        }

        test("reserve book should return false when book is already reserved") {
            // GIVEN
            performQuery(
                // language=sql
                """
                insert into book (title, author, is_reserved)
                values ('Les misérables', 'Victor Hugo', true);
            """.trimIndent()
            )

            // WHEN
            val result = bookDAO.reserveBook(1)

            // THEN
            result.shouldBe(false)
        }

        test("reserve book should return false when book does not exist") {
            // WHEN
            val result = bookDAO.reserveBook(999)

            // THEN
            result.shouldBe(false)
        }

        test("get all books should include reservation status") {
            // GIVEN
            performQuery(
                // language=sql
                """
                insert into book (title, author, is_reserved)
                values 
                    ('Les misérables', 'Victor Hugo', true),
                    ('Hamlet', 'Shakespeare', false);
            """.trimIndent()
            )

            // WHEN
            val res = bookDAO.getAllBooks()

            // THEN
            res.shouldContainExactlyInAnyOrder(
                Book("Les misérables", "Victor Hugo", isReserved = true),
                Book("Hamlet", "Shakespeare", isReserved = false)
            )
        }

        afterSpec {
            container.stop()
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }

        private fun ResultSet.toList(): List<Map<String, Any>> {
            val md = this.metaData
            val columns = md.columnCount
            val rows: MutableList<Map<String, Any>> = ArrayList()
            while (this.next()) {
                val row: MutableMap<String, Any> = HashMap(columns)
                for (i in 1..columns) {
                    row[md.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }

        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig()
            hikariConfig.setJdbcUrl(container.jdbcUrl)
            hikariConfig.username = container.username
            hikariConfig.password = container.password
            hikariConfig.setDriverClassName(container.driverClassName)

            val ds = HikariDataSource(hikariConfig)

            val statement = ds.connection.createStatement()
            statement.execute(sql)
            val resultSet = statement.resultSet
            return resultSet?.toList() ?: listOf()
        }
    }
}