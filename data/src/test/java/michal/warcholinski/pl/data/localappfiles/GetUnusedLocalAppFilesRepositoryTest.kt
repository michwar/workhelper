package michal.warcholinski.pl.data.localappfiles

import android.net.Uri
import androidx.core.net.toUri
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.data.local.impl.LocalAppFilesProvider
import michal.warcholinski.pl.data.local.impl.localfiles.LocalAppFilesRepositoryImpl
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Created by Michał Warcholiński on 2022-01-22.
 */
class GetUnusedLocalAppFilesRepositoryTest {
	private val localAppFilesProvider = mockk<LocalAppFilesProvider>()
	private val requestDao = mockk<RequestDao>()
	private val unusedFilesRepositoryImpl =
		LocalAppFilesRepositoryImpl(localAppFilesProvider, requestDao)

	val file = mockk<File>()

	@Test
	fun `app does not have any local files then should return empty list`() {
		val request1 = createTestRequest(null)
		val request2 = createTestRequest("testPath2")

		coEvery { requestDao.getAll() } returns listOf(request1, request2)
		every { localAppFilesProvider.getAllLocalFiles() } returns flowOf(emptyList())

		val unusedFiles = runBlocking {
			unusedFilesRepositoryImpl.getLocalAppFiles().first()
		}
		assertEquals(0, unusedFiles.size)
	}

	@Test
	fun `should return only files that are not linked to any request`() {
		val request1 = createTestRequest(null)
		val request2 = createTestRequest("testPath2")
		val request3 = createTestRequest("testPath3")
		val request4 = createTestRequest("testPath4")
		val requests = listOf(request1, request2, request3, request4)

		mockkStatic(Uri::class)
		mockkStatic(File::class)
		val uri = mockk<Uri>()
		every { Uri.fromFile(any()) } returns uri
		every { File("").toUri() } returns uri

		val files = flowOf(listOf(File("testPath1")))
		coEvery { requestDao.getAll() } returns requests
		every { localAppFilesProvider.getAllLocalFiles() } returns files

		val unusedFiles = runBlocking {
			unusedFilesRepositoryImpl.getLocalAppFiles().first()
		}

		assertEquals(1, unusedFiles.size)
	}

	@Test
	fun `should return empty list of unused files when all linked to requests`() {
		val request1 = createTestRequest("testPath1")
		val request2 = createTestRequest("testPath2")
		val request3 = createTestRequest("testPath3")
		val request4 = createTestRequest("testPath4")
		val requests = listOf(request1, request2, request3, request4)

		val files = flow { emit(listOf(File("testPath1"))) }
		coEvery { requestDao.getAll() } returns requests
		every { localAppFilesProvider.getAllLocalFiles() } returns files

		val unusedFiles = runBlocking {
			unusedFilesRepositoryImpl.getLocalAppFiles().first()
		}

		assertEquals(0, unusedFiles.size)
	}

	private fun createTestRequest(photoPath: String?): RequestEntity {
		return RequestEntity(null, 1L, "name", 1L, photoPath, "desc")
	}
}