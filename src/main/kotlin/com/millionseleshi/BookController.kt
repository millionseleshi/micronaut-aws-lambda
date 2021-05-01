package com.millionseleshi

import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.*
import javax.validation.Valid


@Controller
open class BookController {

    @Post("/book")
    open fun save(@Valid @Body book: Book): BookRepository {
        val bookSaved = BookRepository()
        val bookTable: DynamoDbTable<BookRepository> = dynamoDbTable()
        bookSaved.id = UUID.randomUUID().toString()
        bookSaved.name = book.name
        bookSaved.isbn = UUID.randomUUID().toString()
        bookTable.putItem(bookSaved)
        return bookSaved
    }

    private fun dynamoDbTable(): DynamoDbTable<BookRepository> {
        val tableName = System.getenv("DYNAMO_DB_TABLE_NAME")
        val envRegion = System.getenv("AWS_REGION")
        val region = Region.of(envRegion)

        val dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build()

        val dynamoDbClientEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build()

        return dynamoDbClientEnhancedClient
                .table(tableName, TableSchema.fromBean(BookRepository::class.java))
    }


}