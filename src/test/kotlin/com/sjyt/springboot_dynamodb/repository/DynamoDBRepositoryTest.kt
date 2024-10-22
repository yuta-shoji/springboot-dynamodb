package com.sjyt.springboot_dynamodb.repository

import com.sjyt.springboot_dynamodb.model.GSI
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.*
import java.lang.UnsupportedOperationException
import kotlin.test.Test

@SpringBootTest
class DynamoDBRepositoryTest {
    data class TestEntity(
        val text: String
    )

    @MockBean
    private lateinit var spyStubDynamoDbTable: DynamoDbTable<TestEntity>

    private lateinit var dynamoDbRepository: NoSQLRepository<TestEntity>

    private lateinit var pageIterable: PageIterable<TestEntity>
    private lateinit var page: Page<TestEntity>

    @BeforeEach
    fun setup() {
        dynamoDbRepository = DynamoDBRepository(spyStubDynamoDbTable)

        pageIterable = mock()
        page = mock()
        `when`(page.items()).thenReturn(emptyList())
        `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
        `when`(spyStubDynamoDbTable.query(any<QueryConditional>())).thenReturn(pageIterable)
    }

    @Nested
    inner class FindAll {
        @Test
        fun dynamoDbTableのscanメソッドを呼んでいる() {
            `when`(page.items()).thenReturn(emptyList())
            `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
            `when`(spyStubDynamoDbTable.scan()).thenReturn(pageIterable)

            dynamoDbRepository.findAll()

            verify(spyStubDynamoDbTable).scan()
        }

        @Test
        fun dynamoDbTableのscanメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mock<PageIterable<TestEntity>>()
            val page = mock<Page<TestEntity>>()
            val expectedTestEntities = listOf(
                TestEntity("some text"),
            )

            `when`(page.items()).thenReturn(expectedTestEntities)
            `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
            `when`(spyStubDynamoDbTable.scan()).thenReturn(pageIterable)

            val actualTestEntities = dynamoDbRepository.findAll()

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

            verify(spyStubDynamoDbTable).query(expectedQueryConditional)
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mock<PageIterable<TestEntity>>()
            val page = mock<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"), TestEntity("1"))

            `when`(page.items()).thenReturn(expectedEntities)
            `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
            `when`(spyStubDynamoDbTable.query(any<QueryConditional>())).thenReturn(pageIterable)

            val actualEntities = dynamoDbRepository.findAllByPK("")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindAllByPKAndSortBetween {
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

            dynamoDbRepository.findAllByPKAndSortBetween(pk, startSk, endSk)

            verify(spyStubDynamoDbTable).query(expectedQueryConditional)
        }

        @Test
        fun dynamoDbTableのqueryメソッドの返り値を正しいEntityの配列に変換して返す() {
            val pageIterable = mock<PageIterable<TestEntity>>()
            val page = mock<Page<TestEntity>>()
            val expectedEntities = listOf(TestEntity("1"))

            `when`(page.items()).thenReturn(expectedEntities)
            `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
            `when`(spyStubDynamoDbTable.query(any<QueryConditional>())).thenReturn(pageIterable)

            val actualEntities = dynamoDbRepository.findAllByPKAndSortBetween("", "", "")

            assertEquals(expectedEntities, actualEntities)
        }
    }

    @Nested
    inner class FindByPKAndSK {
        @Test
        fun dynamoDbTableのgetItemメソッドに正しいKeyを渡す() {
            val pk = "some pk"
            val sk = "some sk"
            val expectedKey = Key.builder()
                .partitionValue(pk)
                .sortValue(sk)
                .build()

            dynamoDbRepository.findByPKAndSK(pk, sk)

            verify(spyStubDynamoDbTable).getItem(expectedKey)
        }

        @Test
        fun 正常にデータを取得できた時_dynamoDbTableのgetItemsメソッドの返り値を返す() {
            val expectedEntity = TestEntity("some text")
            `when`(spyStubDynamoDbTable.getItem(any<Key>())).thenReturn(expectedEntity)

            val actualEntity = dynamoDbRepository.findByPKAndSK("", "")

            assertEquals(expectedEntity, actualEntity)
        }

        @Test
        fun dynamoDbTableのgetItemsがエラーを投げた時_nullを返す() {
            `when`(spyStubDynamoDbTable.getItem(any<Key>()))
                .thenThrow(UnsupportedOperationException())

            val actualEntity = dynamoDbRepository.findByPKAndSK("", "")

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
            dynamoDbIndex = mock()
            pageIterable = mock()
            page = mock()
            `when`(page.items()).thenReturn(emptyList())
            `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
            `when`(spyStubDynamoDbTable.index(any())).thenReturn(dynamoDbIndex)
            `when`(dynamoDbIndex.query(any<QueryConditional>())).thenReturn(pageIterable)
        }

        @Test
        fun dynamoDbTableのindexメソッドとqueryメソッドをただし順序で正しい引数を渡して呼ぶ() {
            val expectedIndexName = "ExpectedIndexName"

            dynamoDbRepository.findAllByGSI(GSI(expectedIndexName, "", null))

            val inOrder = inOrder(spyStubDynamoDbTable, dynamoDbIndex)
            inOrder.verify(spyStubDynamoDbTable).index(any())
            inOrder.verify(dynamoDbIndex).query(any<QueryConditional>())
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

            verify(dynamoDbIndex).query(expectedCondition)
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

            verify(dynamoDbIndex).query(expectedCondition)
        }

        @Test
        fun dynamoDbTableのqueryメソッドを返り値を正しいEntityの配列に変換して返す() {
            val expectedEntities = listOf(TestEntity("1"))
            `when`(page.items()).thenReturn(expectedEntities)
            `when`(pageIterable.iterator()).thenReturn(mutableListOf(page).iterator())
            `when`(dynamoDbIndex.query(any<QueryConditional>())).thenReturn(pageIterable)

            val actualEntities = dynamoDbRepository.findAllByGSI(GSI("", ""))

            assertEquals(expectedEntities, actualEntities)
        }
    }
}

