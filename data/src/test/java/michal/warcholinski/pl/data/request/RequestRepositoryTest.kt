package michal.warcholinski.pl.data.request

import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import michal.warcholinski.pl.data.local.dao.ProjectDao
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.data.local.impl.EmailDataProvider
import michal.warcholinski.pl.data.local.impl.request.RequestRepositoryImpl
import michal.warcholinski.pl.data.local.mapper.RequestMapper
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Michał Warcholiński on 2022-02-24.
 */
class RequestRepositoryTest {

	private val projectDao = mockk<ProjectDao>()
	private val requestDao = mockk<RequestDao>()
	private val requestMapper = spyk<RequestMapper>()
	private val emailDataProvider = mockk<EmailDataProvider>()
	private val requestRepository =
		RequestRepositoryImpl(projectDao, requestDao, requestMapper, emailDataProvider)


	@Test
	fun `get all requests related to project test`() {
		val projectId = 1L
		val requestsEntities = listOf(
			RequestEntity(1L, projectId, "testName1", 1L, null, "testDesc1"),
			RequestEntity(2L, projectId, "testName2", 2L, null, "testDesc2"))

		coEvery { requestDao.getAllForProject(projectId) }.returns(flowOf(requestsEntities))

		val requests = runBlocking {
			requestRepository.getAllRequests(projectId).first()
		}

		assertEquals(2, requests.size)

		coVerifySequence {
			requestDao.getAllForProject(projectId)
			requestMapper.fromEntity(any())
			requestMapper.fromEntity(any())
		}
		coVerify(exactly = 1) { requestDao.getAllForProject(projectId) }
		verify(exactly = 2) { requestMapper.fromEntity(any()) }
	}

	@Test
	fun `get all requests related to project should return empty list if there is no request for given project test`() {
		coEvery { requestDao.getAllForProject(1L) }.returns(flowOf(emptyList()))
		val requests = runBlocking {
			requestRepository.getAllRequests(1L).first()
		}

		assertEquals(0, requests.size)
	}

	@Test
	fun `if request with given id exists then should return its mapped to data model test`() {
		val knownId = 1L
		val testEntity =
			RequestEntity(knownId, knownId, "testName", Long.MIN_VALUE, null, "testDesc")

		coEvery { requestDao.getById(knownId) } returns testEntity

		val dataModel = runBlocking {
			requestRepository.getDetails(knownId)
		}

		assertEquals(knownId, dataModel?.id)
		assertEquals(knownId, dataModel?.projectId)
		assertEquals("testName", dataModel?.name)
		assertEquals("testDesc", dataModel?.desc)
		assertEquals(null, dataModel?.photoPath)
		assertEquals(Long.MIN_VALUE, dataModel?.dateOfCreate)

		coVerifySequence {
			requestDao.getById(knownId)
			requestMapper.fromEntity(testEntity)
		}
		verify(exactly = 1) { requestMapper.fromEntity(testEntity) }
		coVerify(exactly = 1) { requestDao.getById(knownId) }
	}

	@Test
	fun `if request with given id does not exist then should return null test`() {
		val unknownId = 1L

		coEvery { requestDao.getById(unknownId) } returns null

		val dataModel = runBlocking {
			requestRepository.getDetails(unknownId)
		}

		assertEquals(null, dataModel)

		coVerifySequence { requestDao.getById(unknownId) }
		coVerify(exactly = 1) { requestDao.getById(unknownId) }
	}

	@Test
	fun `when requestId is null then should insert new entity test`() {
		every { requestDao.insert(any()) } returns listOf(1L)

		runBlocking {
			requestRepository.saveRequest(null, 1L, "name", "desc", null)
		}

		verify(exactly = 1) { requestDao.insert(any()) }
		verify(exactly = 0) { requestDao.update(any()) }
	}

	@Test
	fun `when requestId is not null then should try to update existing entity test`() {
		every { requestDao.update(any()) } returns 1

		runBlocking {
			requestRepository.saveRequest(1L, 1L, "name", "desc", null)
		}

		verify(exactly = 0) { requestDao.insert(any()) }
		verify(exactly = 1) { requestDao.update(any()) }
	}

	@Test
	fun `delete request test`() {
		coEvery { requestDao.delete(1L) } returns Unit

		runBlocking {
			requestRepository.deleteRequest(1L)
		}

		coVerifySequence { requestDao.delete(1L) }
		coVerify(exactly = 1) { requestDao.delete(1L) }
	}
}