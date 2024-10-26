package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.extension.setPrimaryKeys
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import software.amazon.awssdk.services.dynamodb.model.ScanRequest

class DynamoDBRepositoryTest {
    data class TestEntity(
        val text: String
    ): TableEntity {
        override val tableName: String
            get() = "test_table"
    }

    private lateinit var spyStubDynamoDbTable: DynamoDbTable<TestEntity>
    private lateinit var spyStubDynamoDbEnhancedClient: DynamoDbEnhancedClient

    private lateinit var dynamoDbRepository: NoSQLRepository<TestEntity>

    private lateinit var pageIterable: PageIterable<TestEntity>
    private lateinit var page: Page<TestEntity>

    @BeforeEach
    fun setup() {
        spyStubDynamoDbTable = mockk(relaxed = true)
        spyStubDynamoDbEnhancedClient = mockk()
        pageIterable = mockk()
        page = mockk()

        dynamoDbRepository = DynamoDBRepository(spyStubDynamoDbTable, spyStubDynamoDbEnhancedClient)

        every { page.items() } returns emptyList()
        every { pageIterable.iterator() } returns mutableListOf(page).iterator()
        every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable
    }

    @Nested
    inner class FindAll {
        @Test
        fun dynamoDbTableのscanメソッドを呼んでいる() {
            every { page.items() } returns emptyList()
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.scan() } returns pageIterable

            dynamoDbRepository.findAll()

            verify { spyStubDynamoDbTable.scan() }
        }

        @Test
        fun dynamoDbTableのscanメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedTestEntities = listOf(
                TestEntity("some text"),
            )
            every { page.items() } returns expectedTestEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.scan() } returns pageIterable

            val actualTestEntities = dynamoDbRepository.findAll()

            assertEquals(expectedTestEntities, actualTestEntities)
        }
    }

    @Nested
    inner class FindAllWithLimit {
        @Test
        fun dynamoDbTableのscanメソッドをlimit設定の関数も含めてを正しく呼んでいる() {
            every { page.items() } returns emptyList()
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            val request = ScanEnhancedRequest.builder().limit(100).build()
            every { spyStubDynamoDbTable.scan(request) } returns pageIterable

            dynamoDbRepository.findAllWithLimit(100)

            verify { spyStubDynamoDbTable.scan(request) }
        }

        @Test
        fun dynamoDbTableのscanメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedTestEntities = listOf(
                TestEntity("some text"),
            )
            every { page.items() } returns expectedTestEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            val request = ScanEnhancedRequest.builder().limit(100).build()
            every { spyStubDynamoDbTable.scan(request) } returns pageIterable

            val actualTestEntities = dynamoDbRepository.findAllWithLimit(100)

            assertEquals(expectedTestEntities, actualTestEntities)
        }
    }

    @Nested
    inner class FindAllByPK {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "some pk"
            val expectedQueryConditional = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .partitionValue(pk)
                        .build()
                )

            dynamoDbRepository.findAllByPK(pk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"), TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPK("")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKBetween {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "some pk"
            val startSk = "start sk"
            val endSk = "end sk"
            val expectedQueryConditional = QueryConditional
                .sortBetween(
                    Key.builder()
                        .partitionValue(pk)
                        .sortValue(startSk)
                        .build(),
                    Key.builder()
                        .partitionValue(pk)
                        .sortValue(endSk)
                        .build()
                )

            dynamoDbRepository.findAllByPKAndSKBetween(pk, startSk, endSk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPKAndSKBetween("", "", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKBeginsWith {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "some pk"
            val beginningOfSk = "beginning of sk"
            val expectedQueryConditional = QueryConditional
                .sortBeginsWith(
                    Key.builder()
                        .setPrimaryKeys(pk, beginningOfSk)
                        .build(),
                )

            dynamoDbRepository.findAllByPKAndSKBeginsWith(pk, beginningOfSk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPKAndSKBeginsWith("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKGreaterThan {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = 1
            val sk = 2
            val expectedQueryConditional = QueryConditional
                .sortGreaterThan(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDbRepository.findAllByPKAndSKGreaterThan(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPKAndSKGreaterThan("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKGreaterThanOrEqualTo {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = 1
            val sk = 2
            val expectedQueryConditional = QueryConditional
                .sortGreaterThanOrEqualTo(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDbRepository.findAllByPKAndSKGreaterThanOrEqualTo(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPKAndSKGreaterThanOrEqualTo("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKLessThan {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = 1
            val sk = 2
            val expectedQueryConditional = QueryConditional
                .sortLessThan(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDbRepository.findAllByPKAndSKLessThan(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPKAndSKLessThan("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKLessThanOrEqualTo {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = 1
            val sk = 2
            val expectedQueryConditional = QueryConditional
                .sortLessThanOrEqualTo(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDbRepository.findAllByPKAndSKLessThanOrEqualTo(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mockk<PageIterable<TestEntity>>()
            val page = mockk<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByPKAndSKLessThanOrEqualTo("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindByPrimaryKeys {
        @Test
        fun dynamoDbTableのgetItemメソッドに正しいKeyを渡す() {
            val pk = "some pk"
            val sk = "some sk"
            val expectedKey = Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build()

            dynamoDbRepository.findByPrimaryKeys(pk, sk)

            verify { spyStubDynamoDbTable.getItem(expectedKey) }
        }

        @Test
        fun 正常にデータを取得できた時_dynamoDbTableのgetItemsメソッドの返り値を返す() {
            val expectedEntity = TestEntity("some text")
            every { spyStubDynamoDbTable.getItem(any<Key>()) } returns expectedEntity

            val actualEntity = dynamoDbRepository.findByPrimaryKeys("", "")

            assertEquals(expectedEntity, actualEntity)
        }

        @Test
        fun dynamoDbTableのgetItemsがエラーを投げた時_nullを返す() {
            every { spyStubDynamoDbTable.getItem(any<Key>()) } throws UnsupportedOperationException()


            val actualEntity = dynamoDbRepository.findByPrimaryKeys("", "")

            assertNull(actualEntity)
        }
    }

    @Nested
    inner class FindAllByGSI {
        private lateinit var dynamoDbIndex: DynamoDbIndex<TestEntity>
        private lateinit var pageIterable: PageIterable<TestEntity>
        private lateinit var page: Page<TestEntity>

        @BeforeEach
        fun setup() {
            dynamoDbIndex = mockk()
            pageIterable = mockk()
            page = mockk()
            every { page.items() } returns emptyList()
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.index(any()) } returns dynamoDbIndex
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable
        }

        @Test
        fun dynamoDbTableのindexメソッドとqueryメソッドをただし順序で正しい引数を渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDbRepository.findAllByGSI(GSI(expectedIndexName, "", null))

            verifyOrder {
                spyStubDynamoDbTable.index(any())
                dynamoDbIndex.query(any<QueryConditional>())
            }
        }

        @Test
        fun `gsiのskがnullではない場合、ソートキーを含めたQueryConditionをdynamoDbIndexのqueryメソッドに渡す`() {
            val skIsNotNullGST = GSI("", "some pk", "not null sk")
            val expectedCondition = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .partitionValue(skIsNotNullGST.pk)
                        .sortValue(skIsNotNullGST.sk)
                        .build()
                )

            dynamoDbRepository.findAllByGSI(skIsNotNullGST)

            verify { dynamoDbIndex.query(expectedCondition) }
        }

        @Test
        fun `gsiのskがnullの場合、ソートキーを含まないQueryConditionをdynamoDbIndexのqueryメソッドに渡す`() {
            val skIsNullGST = GSI("", "some pk", null)
            val expectedCondition = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .partitionValue(skIsNullGST.pk)
                        .build()
                )

            dynamoDbRepository.findAllByGSI(skIsNullGST)

            verify { dynamoDbIndex.query(expectedCondition) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドを返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByGSI(GSI.withoutSk("", ""))

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByLSI {
        private lateinit var dynamoDbIndex: DynamoDbIndex<TestEntity>
        private lateinit var pageIterable: PageIterable<TestEntity>
        private lateinit var page: Page<TestEntity>

        @BeforeEach
        fun setup() {
            dynamoDbIndex = mockk()
            pageIterable = mockk()
            page = mockk()
            every { page.items() } returns emptyList()
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.index(any()) } returns dynamoDbIndex
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable
        }

        @Test
        fun dynamoDbTableのindexメソッドとqueryメソッドをただし順序で正しい引数を渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDbRepository.findAllByLSI(LSI(expectedIndexName, "", ""))

            verifyOrder {
                spyStubDynamoDbTable.index(any())
                dynamoDbIndex.query(any<QueryConditional>())
            }
        }

        @Test
        fun `正しいQueryConditionをdynamoDbIndexのqueryメソッドに渡す`() {
            val lsi = LSI("", "some pk", "some sk")
            val expectedCondition = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .partitionValue(lsi.pk)
                        .sortValue(lsi.sk)
                        .build()
                )

            dynamoDbRepository.findAllByLSI(lsi)

            verify { dynamoDbIndex.query(expectedCondition) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドを返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDbRepository.findAllByLSI(LSI("", "", ""))

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class Save {
        @Test
        fun 受け取ったItemをdynamoDbTableのputItemメソッドに正しく渡して呼ぶ() {
            val expectedItem = TestEntity("new item")

            dynamoDbRepository.save(expectedItem)

            verify { spyStubDynamoDbTable.putItem(expectedItem) }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun 受け取ったItemをdynamoDbTableのdeleteメソッドに正しく渡して呼ぶ() {
            val expectedItem = TestEntity("new item")

            dynamoDbRepository.delete(expectedItem)

            verify { spyStubDynamoDbTable.deleteItem(expectedItem) }
        }
    }
}

