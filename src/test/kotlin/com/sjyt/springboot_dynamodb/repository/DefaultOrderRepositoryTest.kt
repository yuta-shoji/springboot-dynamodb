package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.config.SpyDynamoDBFactory
import com.sjyt.springboot_dynamodb.config.StubDynamoDBFactory
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.entity.MainTableEntityBuilder
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import com.sjyt.springboot_dynamodb.model.OrderBuilder
import com.sjyt.springboot_dynamodb.repository.doubles.DummyDynamoDBRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DefaultOrderRepositoryTest {
    @Nested
    @DisplayName("Initializing")
    inner class Initializing {
        @Test
        fun 初期化時_dynamoDBRepositoryFactoryのbuildメソッドを正しく読んでいる() {
            val spyDynamoDBRepositoryFactory = SpyDynamoDBFactory<MainTableEntity>()

            DefaultOrderRepository(spyDynamoDBRepositoryFactory)

            assertEquals(
                MainTableEntity::class.java,
                spyDynamoDBRepositoryFactory.build_argument_tableClass
            )
        }

        @Test
        fun 初期化時_dynamoDBRepositoryFactoryのbuildメソッドが返すDynamoDBRepositoryをメンバーに保持する() {
            val stubDynamoDBRepositoryFactory = StubDynamoDBFactory<MainTableEntity>()
            val expectedDynamoDBRepository = DummyDynamoDBRepository<MainTableEntity>()
            stubDynamoDBRepositoryFactory.build_returnValue = expectedDynamoDBRepository

            val orderRepository = DefaultOrderRepository(stubDynamoDBRepositoryFactory)

            assertEquals(
                expectedDynamoDBRepository,
                orderRepository.dynamoDBRepository
            )
        }
    }

    @Nested
    inner class Initialized {
        private val stubDynamoDBRepositoryFactory = StubDynamoDBFactory<MainTableEntity>()
        private val mockDynamoDBRepository = mockk<NoSQLRepository<MainTableEntity>>(relaxed = true)

        @Nested
        inner class FindAllOrders {
            @Test
            fun dynamoDBRepositoryのfindAllByPKメソッドに正しい引数を渡して呼ぶ() {
                val spyDynamoDBRepository = mockDynamoDBRepository
                val orderRepository = build(spyDynamoDBRepository)

                orderRepository.findAllOrders()

                verify { spyDynamoDBRepository.findAllByPK("ORDER") }
            }

            @Test
            fun dynamoDBRepositoryのfindAllByPKの返り値をOrderの配列型に変換して返す() {
                val stubDynamoDBRepository = mockDynamoDBRepository
                val expectedOrders = listOf(
                    OrderBuilder.build {
                        id = "id 1"
                        productName = "product 1"
                        email = "hoge@example.com"
                        amount = 10
                        place = 1000
                    },
                    OrderBuilder.build {
                        id = "id 2"
                        productName = "product 2"
                        email = "fuga@example.com"
                        amount = 20
                        place = 2000
                    },
                )
                val mainTableEntities = listOf(
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
                    stubDynamoDBRepository.findAllByPK("ORDER")
                } returns mainTableEntities
                val orderRepository = build(stubDynamoDBRepository)

                val actualOrders = orderRepository.findAllOrders()

                assertEquals(expectedOrders, actualOrders)
            }
        }

        @Nested
        inner class FindOrderById {
            @Test
            fun dynamoDBRepositoryのfindByPrimaryKeysメソッドに正しい引数を渡して呼ぶ() {
                val spyDynamoDBRepository = mockDynamoDBRepository
                val orderRepository = build(spyDynamoDBRepository)
                every {
                    spyDynamoDBRepository.findByPrimaryKeys("ORDER", "expected id")
                } returns null

                orderRepository.findOrderById("expected id")

                verify { spyDynamoDBRepository.findByPrimaryKeys("ORDER", "expected id") }
            }

            @Test
            fun dynamoDBRepositoryのfindByPrimaryKeysの返り値をOrder型に変換して返す() {
                val stubDynamoDBRepository = mockDynamoDBRepository
                val expectedOrder = OrderBuilder.build {
                    id = "id 1"
                    productName = "product 1"
                    email = "hoge@example.com"
                    amount = 10
                    place = 1000
                }
                val mainTableEntity = MainTableEntityBuilder.build {
                    sk = "id 1"
                    productName = "product 1"
                    emailLsiSk = "hoge@example.com"
                    amount = 10
                    place = 1000
                }
                every {
                    stubDynamoDBRepository.findByPrimaryKeys("ORDER", "")
                } returns mainTableEntity
                val orderRepository = build(stubDynamoDBRepository)

                val actualOrder = orderRepository.findOrderById("")

                assertEquals(expectedOrder, actualOrder)
            }

            @Test
            fun dynamoDBRepositoryのfindByPrimaryKeysの返り値をがnullの場合nullを返す() {
                val stubDynamoDBRepository = mockDynamoDBRepository
                every {
                    stubDynamoDBRepository.findByPrimaryKeys("ORDER", "")
                } returns null
                val orderRepository = build(stubDynamoDBRepository)

                val actualOrder = orderRepository.findOrderById("")

                assertNull(actualOrder)
            }
        }

        @Nested
        inner class FindOrdersByProductName {
            @Test
            fun dynamoDBRepositoryのfindAllByGSIメソッドに正しいGSIを渡して呼ぶ() {
                val spyDynamoDBRepository = mockDynamoDBRepository
                val orderRepository = build(spyDynamoDBRepository)
                val expectedGSI = GSI.withoutSk("ProductNameGSI", "expected product name")

                orderRepository.findOrdersByProductName("expected product name")

                verify { spyDynamoDBRepository.findAllByGSI(expectedGSI) }
            }

            @Test
            fun dynamoDBRepositoryのfindAllByGSIの返り値をOrderの配列型に変換して返す() {
                val stubDynamoDBRepository = mockDynamoDBRepository
                val expectedOrders = listOf(
                    OrderBuilder.build {
                        id = "id 3"
                        productName = "product 3"
                        email = "piyo@example.com"
                        amount = 30
                        place = 3000
                    },
                    OrderBuilder.build {
                        id = "id 4"
                        productName = "product 4"
                        email = "hogehoge@example.com"
                        amount = 40
                        place = 4000
                    },
                )
                val mainTableEntities = listOf(
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
                    stubDynamoDBRepository.findAllByGSI(any<GSI<String, String>>())
                } returns mainTableEntities
                val orderRepository = build(stubDynamoDBRepository)

                val actualOrders = orderRepository.findOrdersByProductName("")

                assertEquals(expectedOrders, actualOrders)
            }
        }

        @Nested
        inner class FindOrdersByUserEmail {
            @Test
            fun dynamoDBRepositoryのfindAllByLSIメソッドに正しいLSIを渡して呼ぶ() {
                val spyDynamoDBRepository = mockDynamoDBRepository
                val orderRepository = build(spyDynamoDBRepository)
                val expectedLSI = LSI("EmailLSI", "ORDER", "hoge@gmail.com")

                orderRepository.findOrdersByUserEmail("hoge@gmail.com")

                verify { spyDynamoDBRepository.findAllByLSI(expectedLSI) }
            }

            @Test
            fun dynamoDBRepositoryのfindAllByLSIの返り値をOrderの配列型に変換して返す() {
                val stubDynamoDBRepository = mockDynamoDBRepository
                val expectedOrders = listOf(
                    OrderBuilder.build {
                        id = "id 3"
                        productName = "product 3"
                        email = "piyo@example.com"
                        amount = 30
                        place = 3000
                    },
                    OrderBuilder.build {
                        id = "id 4"
                        productName = "product 4"
                        email = "hogehoge@example.com"
                        amount = 40
                        place = 4000
                    },
                )
                val mainTableEntities = listOf(
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
                    stubDynamoDBRepository.findAllByLSI(any<LSI<String, String>>())
                } returns mainTableEntities
                val orderRepository = build(stubDynamoDBRepository)

                val actualOrders = orderRepository.findOrdersByUserEmail("")

                assertEquals(expectedOrders, actualOrders)
            }
        }

        @Nested
        inner class SaveOrder {
            @Test
            fun 受け取ったOrderをMainTableEntity型に正しく変換してdynamoDBRepositoryのsaveメソッドに渡す() {
                val spyDynamoDBRepository = mockDynamoDBRepository
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
                val orderRepository = build(spyDynamoDBRepository)

                orderRepository.saveOrder(order)

                verify { spyDynamoDBRepository.save(expectedMainTableEntity) }
            }

            @Test
            fun dynamoDBRepositoryのfindAllByPKの返り値をOrderの配列型に変換して返す() {
                val stubDynamoDBRepository = mockDynamoDBRepository
                val expectedOrders = listOf(
                    OrderBuilder.build {
                        id = "id 1"
                        productName = "product 1"
                        email = "hoge@example.com"
                        amount = 10
                        place = 1000
                    },
                    OrderBuilder.build {
                        id = "id 2"
                        productName = "product 2"
                        email = "fuga@example.com"
                        amount = 20
                        place = 2000
                    },
                )
                val mainTableEntities = listOf(
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
                    stubDynamoDBRepository.findAllByPK("ORDER")
                } returns mainTableEntities
                val orderRepository = build(stubDynamoDBRepository)

                val actualOrders = orderRepository.findAllOrders()

                assertEquals(expectedOrders, actualOrders)
            }
        }

        private fun build(dynamoDBRepository: NoSQLRepository<MainTableEntity>): OrderRepository {
            stubDynamoDBRepositoryFactory.build_returnValue = dynamoDBRepository
            return DefaultOrderRepository(stubDynamoDBRepositoryFactory)
        }
    }
}
