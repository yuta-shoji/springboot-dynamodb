package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.BeanTableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest

class DynamoDBEnhancedRepositoryTest {
    @DynamoDbBean
    data class TestEntity(
        val id: String,
        override val tableName: String = "test_table"
    ) : TableEntity

    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient = mockk(relaxed = true)
    private val spyStubDynamoDbTable: DynamoDbTable<TestEntity> = mockk(relaxed = true)
    private val tableNameSuffix = "suffix"

    private val repository = spyk(object : DynamoDBEnhancedRepository(
        dynamoDbEnhancedClient,
        tableNameSuffix
    ) {})

    @BeforeEach
    fun setup() {
        every {
            dynamoDbEnhancedClient.table(any<String>(), any<TableSchema<TestEntity>>())
        } returns spyStubDynamoDbTable
    }

    @Nested
    inner class SaveInTransaction {
        @Test
        fun `dynamoDBEnhancedClientのtableメソッドに正しい引数を渡し、requestにitemをaddする`() {
            val testEntities = listOf(TestEntity("1"), TestEntity("2"))
            val mockTable = mockk<DynamoDbTable<TestEntity>>(relaxed = true)
            val requestBuilderMock = mockk<TransactWriteItemsEnhancedRequest.Builder>(relaxed = true)
            every {
                dynamoDbEnhancedClient.table(any(), any<TableSchema<TestEntity>>())
            } returns mockTable
            mockkStatic(TransactWriteItemsEnhancedRequest::class)
            every { TransactWriteItemsEnhancedRequest.builder() } returns requestBuilderMock

            repository.saveInTransaction(testEntities)

            testEntities.forEach { entity ->
                verify {
                    dynamoDbEnhancedClient.table(
                        eq("test_table_suffix"),
                        any<BeanTableSchema<TestEntity>>()
                    )
                    requestBuilderMock.addPutItem(mockTable, entity)
                }
            }
            verify { requestBuilderMock.build() }
        }

        @Test
        fun `正しいrequestをbuildして、transactWriteItemsに渡して呼ぶ`() {
            val requestBuilderMock = mockk<TransactWriteItemsEnhancedRequest.Builder>(relaxed = true)
            val mockTransactWriteItemsEnhancedRequest = mockk<TransactWriteItemsEnhancedRequest>()
            mockkStatic(TransactWriteItemsEnhancedRequest::class)
            every { TransactWriteItemsEnhancedRequest.builder() } returns requestBuilderMock
            every { requestBuilderMock.build() } returns mockTransactWriteItemsEnhancedRequest

            repository.saveInTransaction(emptyList())

            verify { dynamoDbEnhancedClient.transactWriteItems(mockTransactWriteItemsEnhancedRequest) }
        }
    }

    @Nested
    inner class BatchGetItems {
        // TODO("Not yet implemented")
    }
}