package com.example.mykotlinapp.repositories

import com.example.mykotlinapp.dao.TestDao
import com.example.mykotlinapp.dao.TestDataSource
import com.example.mykotlinapp.domain.pojo.SyncState
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.model.entity.SyncData
import com.example.mykotlinapp.model.entity.TimeStampData
import com.example.mykotlinapp.model.repository.AppRepository
import com.example.mykotlinapp.model.repository.AppRepository.DataOperations
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("DIVISION_BY_ZERO")
class AppRepositoryTest : WordSpec({

    val sharedPreferenceDao = mockk<SharedPreferenceDao>()
    val appTestDao = AppTestDao()
    val repository = AppTestRepository(appTestDao, sharedPreferenceDao)

    afterEach { appTestDao.clear() }

    "executeAction" should {
        "compute a failed result if exception is thrown" {
            //Given
            val action: suspend () -> Int = { 5 / 0 }
            //When
            val result = repository.executeAction(UnconfinedTestDispatcher(), action)
            //Then
            result.isFailure shouldBe true
            shouldThrow<ArithmeticException> { result.getOrThrow() }
        }
        "compute a success result otherwise" {
            //Given
            val action = suspend { 5 / 1 }
            //When
            val result = repository.executeAction(UnconfinedTestDispatcher(), action)
            //Then
            result.isSuccess shouldBe true
        }
    }

    fun insertEntities() {
        appTestDao.upsert(listOf(
            EntityTest("remote1", SyncState.UP_TO_DATE, "content", 5, 0),
            EntityTest("remote2", SyncState.UP_TO_DATE, "content", 8, 0),
            EntityTest("remote3", SyncState.PENDING_REMOVAL, "content", 10, 0),
            EntityTest("remote4", SyncState.UP_TO_DATE, "content", 15, 0),
            EntityTest("remote5", SyncState.PENDING_REMOVAL, "content", 25, 0),
            EntityTest("remote6", SyncState.UP_TO_DATE, "content", 70, 0),
            EntityTest("remote7", SyncState.UP_TO_DATE, "content", 4, 0),
        ))
    }

    "AppRepository bulk sync functions" When {
        val newData: List<EntityTest> = listOf(
            EntityTest("remote1", SyncState.UP_TO_DATE, "newContent", 7, 0),
            EntityTest("remote3", SyncState.UP_TO_DATE, "newContent", 25, 0),
            EntityTest("remote4", SyncState.UP_TO_DATE, "newContent", 15, 0),
            EntityTest("remote5", SyncState.UP_TO_DATE, "newContent", 30, 0),
            EntityTest("remote7", SyncState.UP_TO_DATE, "newContent", 2, 0),
        )
        "updateData" should {
            "correctly update local data with new data" {
                runTest {
                    //Given
                    insertEntities()
                    //When
                    repository.updateTestData(newData)
                    //Then
                    appTestDao.getAll() shouldBe listOf(
                        EntityTest("remote1", SyncState.UP_TO_DATE, "newContent", 7, 0),
                        EntityTest("remote3", SyncState.PENDING_REMOVAL, "content", 10, 0),
                        EntityTest("remote4", SyncState.UP_TO_DATE, "content", 15, 0),
                        EntityTest("remote5", SyncState.PENDING_REMOVAL, "content", 25, 0),
                        EntityTest("remote7", SyncState.UP_TO_DATE, "content", 4, 0),
                    )
                }
            }
        }
        "replaceLocalData" should {
            //Given
            insertEntities()
            //When
            repository.replaceTestData(newData)
            //Then
            appTestDao.getAll() shouldBe listOf(
                EntityTest("remote1", SyncState.UP_TO_DATE, "newContent", 7, 0),
                EntityTest("remote3", SyncState.PENDING_REMOVAL, "content", 10, 0),
                EntityTest("remote4", SyncState.UP_TO_DATE, "newContent", 15, 0),
                EntityTest("remote5", SyncState.PENDING_REMOVAL, "content", 25, 0),
                EntityTest("remote7", SyncState.UP_TO_DATE, "newContent", 2, 0),
            )
        }
    }

    "updateEntity" should {
        "not update an entity if they are being deleted" {
            runTest {
                //Given
                insertEntities()
                //When
                val newEntity = EntityTest("remote3", SyncState.UP_TO_DATE, "newContent", 15, 0)
                repository.updateTestEntity(newEntity)
                //Then
                appTestDao.getData("remote3") shouldBe EntityTest("remote3", SyncState.PENDING_REMOVAL, "content", 10, 0)
            }
        }
        "update an entity if new data is more recent" {
            runTest {
                //Given
                insertEntities()
                //When
                val newEntity = EntityTest("remote6", SyncState.UP_TO_DATE, "newContent", 71, 0)
                repository.updateTestEntity(newEntity)
                //Then
                appTestDao.getData("remote6") shouldBe EntityTest("remote6", SyncState.UP_TO_DATE, "newContent", 71, 0)
            }
        }
        "update an entity if local data is more recent" {
            runTest {
                //Given
                insertEntities()
                //When
                val newEntity = EntityTest("remote6", SyncState.UP_TO_DATE, "newContent", 65, 0)
                repository.updateTestEntity(newEntity)
                //Then
                appTestDao.getData("remote6") shouldBe EntityTest("remote6", SyncState.UP_TO_DATE, "content", 70, 0)
            }
        }
    }

    "replaceEntity" should {
        "not replace an entity if they are being deleted" {
            runTest {
                //Given
                insertEntities()
                //When
                val newEntity = EntityTest("remote3", SyncState.UP_TO_DATE, "newContent", 15, 0)
                repository.replaceTestEntity(newEntity)
                //Then
                appTestDao.getData("remote3") shouldBe EntityTest("remote3", SyncState.PENDING_REMOVAL, "content", 10, 0)
            }
        }
        "correctly replace an entity otherwise" {
            runTest {
                //Given
                insertEntities()
                //When
                val newEntity = EntityTest("remote6", SyncState.UP_TO_DATE, "newContent", 2, 0)
                repository.replaceTestEntity(newEntity)
                //Then
                appTestDao.getData("remote6") shouldBe EntityTest("remote6", SyncState.UP_TO_DATE, "newContent", 2, 0)
            }
        }
    }

}) {

    data class EntityTest(
        override val remoteId: String,
        override val syncState: SyncState,
        val content: String,
        override val updateTime: Long,
        override val creationTime: Long) : SyncData, TimeStampData

    class AppTestDao: TestDao {

        private val entitiesTable = TestDataSource<String, EntityTest> { it.remoteId }

        fun getAll(): List<EntityTest> = entitiesTable.getAll()

        fun getData(id: String): EntityTest? = entitiesTable.get(id)

        fun getDataByIds(ids: List<String>): List<EntityTest> = entitiesTable.getAllWhen { ids.contains(it.remoteId) }

        fun upsert(entity: EntityTest) = entitiesTable.insert(entity)

        fun upsert(entities: List<EntityTest>) = entitiesTable.insert(entities)

        fun clearDataNotIn(except: List<String>) = entitiesTable.deleteWhen { !except.contains(it.remoteId) }

        override fun clear() = entitiesTable.clear()

    }

    class AppTestRepository(appTestDao: AppTestDao, sharedPreferenceDao: SharedPreferenceDao): AppRepository(sharedPreferenceDao){

        private val dataOperations = DataOperations(
            appTestDao::getData,
            appTestDao::upsert
        )
        private val dataBulkOperations = DataBulkOperations(
            appTestDao::getDataByIds,
            appTestDao::upsert,
            appTestDao::clearDataNotIn
        )

        suspend fun updateTestData(newData: List<EntityTest>) = updateData(newData, dataBulkOperations)
        suspend fun updateTestEntity(newData: EntityTest) = updateEntity(newData, dataOperations)
        suspend fun replaceTestData(newData: List<EntityTest>) = replaceLocalData(newData, dataBulkOperations)
        suspend fun replaceTestEntity(newData: EntityTest) = replaceLocalEntity(newData, dataOperations)
    }
}