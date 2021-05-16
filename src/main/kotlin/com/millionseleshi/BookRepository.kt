package com.millionseleshi

import io.micronaut.core.annotation.Introspected
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@Introspected
@DynamoDbBean
data class BookRepository(
        @get:DynamoDbPartitionKey
        var name: String? = null,
        var isbn: String? = null
)