package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.entity.TableEntity
import com.sjyt.springboot_dynamodb.extension.setPK
import com.sjyt.springboot_dynamodb.extension.setPrimaryKeys
import com.sjyt.springboot_dynamodb.model.GSI
import com.sjyt.springboot_dynamodb.model.LSI
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.model.Page
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest

class DynamoDBRepositoryTest {
    @DynamoDbBean
    data class TestEntity(
        val text: String
    ): TableEntity {
        override val tableName: String
            get() = "test_table"
    }

    private val dynamoDbEnhancedClient: DynamoDbEnhancedClient = mockk(relaxed = true)
    private val spyStubDynamoDbTable: DynamoDbTable<TestEntity> = mockk(relaxed = true)
    private val tableNameSuffix = "test"

    private val pageIterable = mockk<PageIterable<TestEntity>>()
    private val page = mockk<Page<TestEntity>>()

    private val dynamoDBRepository: DynamoDBRepository<TestEntity, String, String> = object : DynamoDBRepository<TestEntity, String, String>(
        dynamoDbEnhancedClient,
        tableNameSuffix
    ) {
        init {
            val dynamoDbTableField = DynamoDBRepository::class.java.getDeclaredField("dynamoDbTable")
            dynamoDbTableField.isAccessible = true
            dynamoDbTableField.set(this, spyStubDynamoDbTable)
        }
    }

    @Nested
    inner class FindAll {
        @Test
        fun dynamoDbTableのscanメソッドを呼んでいる() {
            dynamoDBRepository.findAll()

            verify { spyStubDynamoDbTable.scan() }
        }

        @Test
        fun dynamoDbTableのscanメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedTestEntities = listOf(
                TestEntity("some text"),
            )
            setPageIterable(expectedTestEntities)

            val actualTestEntities = dynamoDBRepository.findAll()

            assertEquals(expectedTestEntities, actualTestEntities)
        }
    }

    @Nested
    inner class FindAllWithLimit {
        @Test
        fun dynamoDbTableのscanメソッドをlimit設定の関数も含めてを正しく呼んでいる() {
            val request = ScanEnhancedRequest.builder().limit(100).build()

            dynamoDBRepository.findAllWithLimit(100)

            verify { spyStubDynamoDbTable.scan(request) }
        }

        @Test
        fun dynamoDbTableのscanメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedTestEntities = listOf(
                TestEntity("some text"),
            )
            setPageIterable(expectedTestEntities)
            every { spyStubDynamoDbTable.scan(any<ScanEnhancedRequest>()) } returns pageIterable

            val actualTestEntities = dynamoDBRepository.findAllWithLimit(100)

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
                        .setPK(pk)
                        .build()
                )

            dynamoDBRepository.findAllByPK(pk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"), TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPK("")

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
                        .setPrimaryKeys(pk, startSk)
                        .build(),
                    Key.builder()
                        .setPrimaryKeys(pk, endSk)
                        .build()
                )

            dynamoDBRepository.findAllByPKAndSKBetween(pk, startSk, endSk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPKAndSKBetween("", "", "")

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

            dynamoDBRepository.findAllByPKAndSKBeginsWith(pk, beginningOfSk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPKAndSKBeginsWith("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKGreaterThan {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "1"
            val sk = "2"
            val expectedQueryConditional = QueryConditional
                .sortGreaterThan(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDBRepository.findAllByPKAndSKGreaterThan(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPKAndSKGreaterThan("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKGreaterThanOrEqualTo {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "1"
            val sk = "2"
            val expectedQueryConditional = QueryConditional
                .sortGreaterThanOrEqualTo(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDBRepository.findAllByPKAndSKGreaterThanOrEqualTo(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPKAndSKGreaterThanOrEqualTo("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKLessThan {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "1"
            val sk = "2"
            val expectedQueryConditional = QueryConditional
                .sortLessThan(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDBRepository.findAllByPKAndSKLessThan(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPKAndSKLessThan("", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSKLessThanOrEqualTo {
        @Test
        fun dynamoDbTableのqueryメソッドに正しいQueryConditionを渡す() {
            val pk = "1"
            val sk = "2"
            val expectedQueryConditional = QueryConditional
                .sortLessThanOrEqualTo(
                    Key.builder()
                        .setPrimaryKeys(pk, sk)
                        .build(),
                )

            dynamoDBRepository.findAllByPKAndSKLessThanOrEqualTo(pk, sk)

            verify { spyStubDynamoDbTable.query(expectedQueryConditional) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            setPageIterable(expectedEntities)
            every { spyStubDynamoDbTable.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByPKAndSKLessThanOrEqualTo("", "")

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
                .setPrimaryKeys(pk, sk)
                .build()

            dynamoDBRepository.findByPrimaryKeys(pk, sk)

            verify { spyStubDynamoDbTable.getItem(expectedKey) }
        }

        @Test
        fun 正常にデータを取得できた時_dynamoDbTableのgetItemsメソッドの返り値を返す() {
            val expectedEntity = TestEntity("some text")
            every { spyStubDynamoDbTable.getItem(any<Key>()) } returns expectedEntity

            val actualEntity = dynamoDBRepository.findByPrimaryKeys("", "")

            assertEquals(expectedEntity, actualEntity)
        }

        @Test
        fun dynamoDbTableのgetItemsがエラーを投げた時_nullを返す() {
            every { spyStubDynamoDbTable.getItem(any<Key>()) } throws UnsupportedOperationException()

            val actualEntity = dynamoDBRepository.findByPrimaryKeys("", "")

            assertNull(actualEntity)
        }
    }

    @Nested
    inner class FindAllByGSI {
        private lateinit var dynamoDbIndex: DynamoDbIndex<TestEntity>

        @BeforeEach
        fun setup() {
            dynamoDbIndex = mockk<DynamoDbIndex<TestEntity>>()
            every { page.items() } returns emptyList()
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.index(any()) } returns dynamoDbIndex
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable
        }

        @Test
        fun dynamoDbTableのindexメソッドとqueryメソッドをただし順序で正しい引数を渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDBRepository.findAllByGSI(GSI(expectedIndexName, "", null))

            verifyOrder {
                spyStubDynamoDbTable.index(any())
                dynamoDbIndex.query(any<QueryConditional>())
            }
        }

        @Test
        fun dynamoDbTableのindexメソッドに正しいindexNameを渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDBRepository.findAllByGSI(GSI(expectedIndexName, "", null))

            verify { spyStubDynamoDbTable.index(expectedIndexName) }
        }

        @Test
        fun `gsiのskがnullではない場合、ソートキーを含めたQueryConditionをdynamoDbIndexのqueryメソッドに渡す`() {
            val gsiWithSkNotNull = GSI("", "some pk", "not null sk")
            val expectedCondition = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .setPrimaryKeys(gsiWithSkNotNull)
                        .build()
                )

            dynamoDBRepository.findAllByGSI(gsiWithSkNotNull)

            verify { dynamoDbIndex.query(expectedCondition) }
        }

        @Test
        fun `gsiのskがnullの場合、ソートキーを含まないQueryConditionをdynamoDbIndexのqueryメソッドに渡す`() {
            val gsiWithSkIsNull = GSI("", "some pk", null)
            val expectedCondition = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .setPrimaryKeys(gsiWithSkIsNull)
                        .build()
                )

            dynamoDBRepository.findAllByGSI(gsiWithSkIsNull)

            verify { dynamoDbIndex.query(expectedCondition) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドを返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByGSI(GSI.withoutSk("", ""))

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByLSI {
        private lateinit var dynamoDbIndex: DynamoDbIndex<TestEntity>

        @BeforeEach
        fun setup() {
            dynamoDbIndex = mockk<DynamoDbIndex<TestEntity>>()
            every { page.items() } returns emptyList()
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { spyStubDynamoDbTable.index(any()) } returns dynamoDbIndex
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable
        }

        @Test
        fun dynamoDbTableのindexメソッドとqueryメソッドをただし順序で正しい引数を渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDBRepository.findAllByLSI(LSI(expectedIndexName, "", ""))

            verifyOrder {
                spyStubDynamoDbTable.index(any())
                dynamoDbIndex.query(any<QueryConditional>())
            }
        }

        @Test
        fun dynamoDbTableのindexメソッドに正しいindexNameを渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDBRepository.findAllByGSI(GSI(expectedIndexName, "", null))

            verify { spyStubDynamoDbTable.index(expectedIndexName) }
        }

        @Test
        fun `正しいQueryConditionをdynamoDbIndexのqueryメソッドに渡す`() {
            val lsi = LSI("", "some pk", "some sk")
            val expectedCondition = QueryConditional
                .keyEqualTo(
                    Key.builder()
                        .setPrimaryKeys(lsi)
                        .build()
                )

            dynamoDBRepository.findAllByLSI(lsi)

            verify { dynamoDbIndex.query(expectedCondition) }
        }

        @Test
        fun dynamoDbTableのqueryメソッドを返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            every { page.items() } returns expectedEntities
            every { pageIterable.iterator() } returns mutableListOf(page).iterator()
            every { dynamoDbIndex.query(any<QueryConditional>()) } returns pageIterable

            val actualEntities = dynamoDBRepository.findAllByLSI(LSI("", "", ""))

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class Save {
        @Test
        fun 受け取ったItemをdynamoDbTableのputItemメソッドに正しく渡して呼ぶ() {
            val expectedItem = TestEntity("new item")

            dynamoDBRepository.save(expectedItem)

            verify { spyStubDynamoDbTable.putItem(expectedItem) }
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun 受け取ったItemをdynamoDbTableのdeleteメソッドに正しく渡して呼ぶ() {
            val expectedItem = TestEntity("new item")

            dynamoDBRepository.delete(expectedItem)

            verify { spyStubDynamoDbTable.deleteItem(expectedItem) }
        }
    }

    private fun setPageIterable(entities: List<TestEntity>) {
        every { page.items() } returns entities
        every { pageIterable.iterator() } returns mutableListOf(page).iterator()
        every { spyStubDynamoDbTable.scan() } returns pageIterable
    }
}

