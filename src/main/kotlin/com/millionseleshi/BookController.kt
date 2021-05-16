package com.millionseleshi

import io.micronaut.http.annotation.*
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.*
import javax.validation.Valid


@Controller
open class BookController {

    @Post("/book")
    open fun save(@Valid @Body book: Book): BookRepository {
        val bookSaved = BookRepository()
        val bookTable: DynamoDbTable<BookRepository> = dynamoDbTable()
        bookSaved.name = book.name
        bookSaved.isbn = UUID.randomUUID().toString()
        bookTable.putItem(bookSaved)
        return bookSaved
    }

    @Get("/books")
    open fun findAll(): ArrayList<BookRepository> {
        val bookRepository = ArrayList<BookRepository>()
        val bookTable: DynamoDbTable<BookRepository> = dynamoDbTable()
        val results = bookTable.scan().items().iterator()
        while (results.hasNext()) {
            bookRepository.add(results.next())
        }
        return bookRepository
    }

    @Get("/book/{name}")
    open fun findOne(name: String): BookRepository? {
        val bookTable: DynamoDbTable<BookRepository> = dynamoDbTable()
        val key = Key.builder().partitionValue(AttributeValue.builder().s(name).build()).build()
        return bookTable.getItem { r -> r.key(key) }
    }

    @Delete("/book/{name}")
    open fun deleteOne(name: String): BookRepository? {
        val bookTable: DynamoDbTable<BookRepository> = dynamoDbTable()
        val key = Key.builder().partitionValue(AttributeValue.builder().s(name).build()).build()
        return bookTable.deleteItem { r -> r.key(key) }
    }

    private fun dynamoDbTable(): DynamoDbTable<BookRepository> {
        val tableName = System.getenv("DYNAMO_DB_TABLE_NAME")
        val envRegion = System.getenv("AWS_REGION")
        val region = Region.of(envRegion)

        val dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .build()

        val dynamoDbClientEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build()

        return dynamoDbClientEnhancedClient
                .table(tableName, TableSchema.fromBean(BookRepository::class.java))
    }


}