package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.config.SpyDynamoDBRepositoryFactory
import com.sjyt.springboot_dynamodb.config.StubDynamoDBRepositoryFactory
import com.sjyt.springboot_dynamodb.entity.MainTableEntity
import com.sjyt.springboot_dynamodb.repository.doubles.DummyDynamoDBRepository
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultOrderRepositoryTest {
    @Nested
    inner class Initializing {
        @Test
        fun 初期化時_dynamoDBRepositoryFactoryのbuildメソッドを正しく読んでいる() {
            val spyDynamoDBRepositoryFactory = SpyDynamoDBRepositoryFactory<MainTableEntity>()

            DefaultOrderRepository(spyDynamoDBRepositoryFactory)

            assertEquals(
                MainTableEntity::class.java,
                spyDynamoDBRepositoryFactory.build_argument_tableClass
            )
        }

        @Test
        fun 初期化時_dynamoDBRepositoryFactoryのbuildメソッドが返すDynamoDBRepositoryをメンバーに保持する() {
            val stubDynamoDBRepositoryFactory = StubDynamoDBRepositoryFactory<MainTableEntity>()
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
        val stubDynamoDBRepositoryFactory = StubDynamoDBRepositoryFactory<MainTableEntity>()
        val mockDynamoDBRepository = mockk<NoSQLRepository<MainTableEntity>>(relaxed = true)

        private lateinit var orderRepository: OrderRepository

        @Nested
        @DisplayName("FindAllOrders")
        inner class FindAllOrders {
            @Test
            fun dynamoDBRepositoryのfindAllByPKメソッドに正しい引数を渡して呼ぶ() {
                val spyDynamoDBRepository = mockDynamoDBRepository
                stubDynamoDBRepositoryFactory.build_returnValue = spyDynamoDBRepository
                orderRepository = DefaultOrderRepository(stubDynamoDBRepositoryFactory)

                orderRepository.findAllOrders()

                verify { spyDynamoDBRepository.findAllByPK("ORDER") }
            }
        }
    }
}
