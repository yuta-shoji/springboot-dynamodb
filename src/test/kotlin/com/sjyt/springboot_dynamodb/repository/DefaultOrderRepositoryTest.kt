package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.EventTableEntity
import com.sjyt.springboot_dynamodb.entity.EventTableEntityBuilder
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.entity.MainTableEntityBuilder
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import com.sjyt.springboot_dynamodb.model.MainAndEventTableEntities
import com.sjyt.springboot_dynamodb.model.OrderBuilder
import com.sjyt.springboot_dynamodb.model.request.BatchResource
import com.sjyt.springboot_dynamodb.model.request.PrimaryKey
import com.sjyt.springboot_dynamodb.model.response.BatchResponse
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import java.time.LocalDateTime

class DefaultOrderRepositoryTest {
    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient = mockk(relaxed = true)
    private val tableNameSuffix = "test_suffix"

    private val orderRepository = spyk(DefaultOrderRepository(dynamoDbEnhancedClient, tableNameSuffix))

    @Nested
    inner class FindAllOrders {
        @Test
        fun 継承したdynamoDBRepositoryのfindAllByPKメソッドに正しい引数を渡して呼ぶ() {
            orderRepository.findAllOrders()

            verify { orderRepository.findAllByPK("ORDER") }
        }

        @Test
        fun 継承したdynamoDBRepositoryのfindAllByPKの返り値を返す() {
            val expectedEntities = listOf(
                MainTableEntityBuilder.build {
                    sk = "id 1"
                    productName = "product 1"
                    emailLsiSk = "hoge@example.com"
                    amount = 10
                    place = 1000
                },
                MainTableEntityBuilder.build {
                    sk = "id 2"
                    productName = "product 2"
                    emailLsiSk = "fuga@example.com"
                    amount = 20
                    place = 2000
                },
            )
            every {
                orderRepository.findAllByPK(any<String>())
            } returns expectedEntities

            val actualEntities = orderRepository.findAllOrders()

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindOrderById {
        @Test
        fun 継承したdynamoDBRepositoryのfindByPrimaryKeysメソッドに正しい引数を渡して呼ぶ() {
            orderRepository.findOrderById("expected id")

            verify { orderRepository.findByPrimaryKeys("ORDER", "expected id") }
        }

        @Test
        fun 継承したdynamoDBRepositoryのfindByPrimaryKeysの返り値を返す() {
            val expectedEntity = MainTableEntityBuilder.build {
                sk = "id 1"
                productName = "product 1"
                emailLsiSk = "hoge@example.com"
                amount = 10
                place = 1000
            }
            every {
                orderRepository.findByPrimaryKeys("ORDER", "")
            } returns expectedEntity

            val actualEntity = orderRepository.findOrderById("")

            assertEquals(expectedEntity, actualEntity)
        }
    }

    @Nested
    inner class FindOrdersByProductName {
        @Test
        fun 継承したdynamoDBRepositoryのfindAllByGSIメソッドに正しいGSIを渡して呼ぶ() {
            val productName = "expected product name"
            val expectedGSI = GSI.withoutSk("ProductNameGSI", productName)

            orderRepository.findOrdersByProductName(productName)

            verify { orderRepository.findAllByGSI(expectedGSI) }
        }

        @Test
        fun 継承したdynamoDBRepositoryのfindAllByGSIの返り値を返す() {
            val expectedEntities = listOf(
                MainTableEntityBuilder.build {
                    sk = "id 3"
                    productName = "product 3"
                    emailLsiSk = "piyo@example.com"
                    amount = 30
                    place = 3000
                },
                MainTableEntityBuilder.build {
                    sk = "id 4"
                    productName = "product 4"
                    emailLsiSk = "hogehoge@example.com"
                    amount = 40
                    place = 4000
                },
            )
            every {
                orderRepository.findAllByGSI(any<GSI<String, String>>())
            } returns expectedEntities

            val actualEntities = orderRepository.findOrdersByProductName("")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindOrdersByUserEmail {
        @Test
        fun 継承したdynamoDBRepositoryのfindAllByLSIメソッドに正しいLSIを渡して呼ぶ() {
            val expectedLSI = LSI("EmailLSI", "ORDER", "hoge@gmail.com")

            orderRepository.findOrdersByUserEmail("hoge@gmail.com")

            verify { orderRepository.findAllByLSI(expectedLSI) }
        }

        @Test
        fun 継承したdynamoDBRepositoryのfindAllByLSIの返り値を返す() {
            val expectedEntities = listOf(
                MainTableEntityBuilder.build {
                    sk = "id 3"
                    productName = "product 3"
                    emailLsiSk = "piyo@example.com"
                    amount = 30
                    place = 3000
                },
                MainTableEntityBuilder.build {
                    sk = "id 4"
                    productName = "product 4"
                    emailLsiSk = "hogehoge@example.com"
                    amount = 40
                    place = 4000
                },
            )
            every {
                orderRepository.findAllByLSI(any<LSI<String, String>>())
            } returns expectedEntities

            val actualEntities = orderRepository.findOrdersByUserEmail("")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class SaveOrder {
        @Test
        fun 受け取ったOrderをMainTableEntity型に正しく変換してdynamoDBRepositoryのsaveメソッドに渡す() {
            val order = OrderBuilder.build {
                id = "id 1"
                productName = "product 1"
                email = "piyo@example.com"
                amount = 10
                place = 1000
            }
            val expectedMainTableEntity = MainTableEntityBuilder.build {
                pk = "ORDER"
                sk = "id 1"
                productName = "product 1"
                emailLsiSk = "piyo@example.com"
                amount = 10
                place = 1000
            }

            orderRepository.saveOrder(order)

            verify { orderRepository.save(expectedMainTableEntity) }
        }
    }

    @Nested
    inner class BatchGetOrderAndEvent {
        @Test
        fun 継承したdynamoDBRepositoryのbatchGetItemメソッドに正しいresourceを渡して呼ぶ() {
            val orderPrimaryKeys = listOf(PrimaryKey("pk1", "sk1"))
            val eventPrimaryKeys = listOf(PrimaryKey("pk2", "sk2"))
            val expectedBatchResources = listOf(
                BatchResource(MainTableEntity::class.java, orderPrimaryKeys),
                BatchResource(EventTableEntity::class.java, eventPrimaryKeys),
            )
            every { orderRepository.batchGetItems(any()) } returns emptyList()

            orderRepository.batchGetOrderAndEvent(orderPrimaryKeys, eventPrimaryKeys)

            verify { orderRepository.batchGetItems(expectedBatchResources) }
        }

        @Test
        fun 継承したdynamoDBRepositoryのbatchGetItemの返り値を正しいMainAndEventTableEntities型に変換して返す() {
            val eventTableEntity = EventTableEntityBuilder.build {
                eventType = "CLICK"
                date = LocalDateTime.of(2021, 1, 1, 1, 1)
            }
            val mainTableEntity = MainTableEntityBuilder.build {
                pk = "pk 2"
                sk = "id 2"
                productName = "product 2"
            }
            val batchResponses = listOf(
                BatchResponse(listOf(eventTableEntity, mainTableEntity))
            )
            val expectedMainAndEventEntities = MainAndEventTableEntities(
                mainTableEntities = listOf(mainTableEntity),
                eventTableEntities = listOf(eventTableEntity),
            )
            every { orderRepository.batchGetItems(any()) } returns batchResponses

            val actualMainAndEventTableEntities = orderRepository
                .batchGetOrderAndEvent(emptyList(), emptyList())

            assertEquals(expectedMainAndEventEntities, actualMainAndEventTableEntities)
        }
    }
}
