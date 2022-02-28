package michal.warcholinski.pl.data.project

import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import michal.warcholinski.pl.data.local.dao.ProjectDao
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.data.local.impl.EmailDataProvider
import michal.warcholinski.pl.data.local.impl.LocalAppFilesProvider
import michal.warcholinski.pl.data.local.impl.project.ProjectRepositoryImpl
import michal.warcholinski.pl.data.local.mapper.ProjectMapper
import org.junit.Assert.*
import org.junit.Test
import java.io.File

/**
 * Created by Michał Warcholiński on 2022-02-28.
 */
class ProjectRepositoryTest {

	private val projectDao = mockk<ProjectDao>()
	private val requestDao = mockk<RequestDao>()
	private val projectMapper = spyk<ProjectMapper>()
	private val localAppFilesProvider = mockk<LocalAppFilesProvider>()
	private val emailDataProvider = mockk<EmailDataProvider>()

	private val projectRepository =
		ProjectRepositoryImpl(projectDao, requestDao, projectMapper, localAppFilesProvider, emailDataProvider)


	@Test
	fun `get all projects test`() {
		val realizedProjects = listOf(
			ProjectEntity(1L, "testName", "testDesc", Long.MIN_VALUE, "123123123", "test@test.com", true),
			ProjectEntity(1L, "testName", "testDesc", Long.MIN_VALUE, "123123123", "test@test.com", true),
			ProjectEntity(1L, "testName", "testDesc", Long.MIN_VALUE, "123123123", "test@test.com", true)
		)
		coEvery { projectDao.getAll(true) }.returns(flowOf(realizedProjects))

		val projects = runBlocking { projectRepository.getAllProjects(true).first() }

		assertEquals(3, projects.size)
		verifySequence {
			projectDao.getAll(true)
			projectMapper.fromEntity(any())
			projectMapper.fromEntity(any())
			projectMapper.fromEntity(any())
		}

		verify(exactly = 1) { projectDao.getAll(true) }
		verify(exactly = 3) { projectMapper.fromEntity(any()) }
	}

	@Test
	fun `get details of project should return project data model if entity was found test`() {
		val projectId = 1L
		val projectEntity =
			ProjectEntity(projectId, "testName", "testDesc", 1L, "123123123", "test@test.com", false)
		coEvery { projectDao.getById(projectId) }.returns(projectEntity)
		val projectDetails = runBlocking { projectRepository.getDetails(projectId) }

		assertNotNull(projectDetails)

		coVerifySequence {
			projectDao.getById(projectId)
			projectMapper.fromEntity(any())
		}
		coVerify(exactly = 1) { projectDao.getById(projectId) }
		coVerify(exactly = 1) { projectMapper.fromEntity(any()) }
	}

	@Test
	fun `get details of project should return null if entity was not found test`() {
		val projectId = 1L
		coEvery { projectDao.getById(projectId) }.returns(null)
		val projectDetails = runBlocking { projectRepository.getDetails(projectId) }

		assertNull(projectDetails)

		coVerifySequence { projectDao.getById(projectId) }
		coVerify(exactly = 1) { projectDao.getById(projectId) }
	}

	@Test
	fun `add project test`() {
		val projectName = "testName"
		val projectDesc = "testName"
		val phoneNo = "123123123"
		val email = "email"

		coEvery { projectDao.insert(any()) }.returns(listOf(1))
		runBlocking { projectRepository.addProject(projectName, projectDesc, phoneNo, email) }

		coVerify(exactly = 1) { projectDao.insert(any()) }
	}

	@Test
	fun `archive project test`() {
		val projectId = 1L

		coEvery { projectDao.archive(projectId) }.returns(Unit)
		runBlocking { projectRepository.archive(projectId) }

		coVerify(exactly = 1) { projectDao.archive(projectId) }
	}

	@Test
	fun `delete project test`() {
		val projectId = 1L

		coEvery { projectDao.delete(projectId) }.returns(Unit)
		runBlocking { projectRepository.delete(projectId) }

		coVerify(exactly = 1) { projectDao.delete(projectId) }
	}


	@Test
	fun `delete zip file related to project test`() {
		val projectId = 1L
		val projectEntity =
			ProjectEntity(projectId, "testName", "testDesc", 1L, "123123123", "test@test.com", false)

		coEvery { projectDao.getById(projectId) }.returns(projectEntity)
		every { localAppFilesProvider.getZipFilesDir() }.returns(File("test_zip"))
		every { localAppFilesProvider.deleteFile(any()) }.returns(true)
		runBlocking { projectRepository.deleteZipFile(projectId) }

		coVerifySequence {
			projectDao.getById(projectId)
			localAppFilesProvider.getZipFilesDir()
			localAppFilesProvider.deleteFile(any())
		}

		coVerify(exactly = 1) { projectDao.getById(projectId) }
		verify(exactly = 1) { localAppFilesProvider.getZipFilesDir() }
		verify(exactly = 1) { localAppFilesProvider.deleteFile(any()) }
	}

	@Test
	fun `when project was not found then should not try to delete related zip file test`() {
		val projectId = 1L
		coEvery { projectDao.getById(projectId) }.returns(null)

		runBlocking { projectRepository.deleteZipFile(projectId) }

		coVerify(exactly = 1) { projectDao.getById(projectId) }
		coVerify(exactly = 0) { localAppFilesProvider.getZipFilesDir() }
		coVerify(exactly = 0) { localAppFilesProvider.deleteFile(any()) }
	}
}