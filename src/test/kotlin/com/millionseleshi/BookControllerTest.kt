package com.millionseleshi

import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext
import com.amazonaws.services.lambda.runtime.Context
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.withEnvironment
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.micronaut.function.aws.proxy.MicronautLambdaHandler
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class BookRequestHandlerTest : FunSpec({

    test("test book controller") {
            val handler = MicronautLambdaHandler()
            val book = Book()
            book.name = "Building Microservices"
            val objectMapper = handler.applicationContext.getBean(ObjectMapper::class.java)
            val json = objectMapper.writeValueAsString(book)
            val request = AwsProxyRequestBuilder("/book", HttpMethod.POST.toString())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .body(json)
                    .build()
            val lambdaContext: Context = MockLambdaContext()
            val response = handler.handleRequest(request, lambdaContext)
            response.statusCode.shouldBe(HttpStatus.OK.code)
            val bookRepository: BookRepository = objectMapper.readValue(response.body, BookRepository::class.java)
            bookRepository.shouldNotBeNull()
            bookRepository.name.shouldBe(book.name)
            bookRepository.isbn.shouldNotBeNull()
            handler.applicationContext.close()
    }

})



