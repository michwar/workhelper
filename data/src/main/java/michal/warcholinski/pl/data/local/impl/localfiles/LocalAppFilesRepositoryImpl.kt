package michal.warcholinski.pl.data.local.impl.localfiles

import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.impl.LocalAppFilesProvider
import michal.warcholinski.pl.domain.localfiles.domain.LocalAppFilesRepository
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import java.io.File
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */
internal class LocalAppFilesRepositoryImpl @Inject constructor(
	private val localAppFilesProvider: LocalAppFilesProvider,
	private val requestDao: RequestDao

) : LocalAppFilesRepository {

	override fun getDir(): File {
		return localAppFilesProvider.getLocalFilesDir()
	}

	override suspend fun getLocalAppFiles(): Flow<List<FileDataModel>> {
		val requestsWithFile = requestDao.getAll().mapNotNull { request -> request.photoPath }
		return localAppFilesProvider
			.getAllLocalFiles()
			.map { allFiles -> allFiles.filter { file -> !requestsWithFile.contains(file.path) } }
			.map { unusedFiles -> unusedFiles.map { file -> FileDataModel(file.name, file.path, file.toUri()) } }
	}

	override suspend fun deleteFile(path: String): Boolean {
		return localAppFilesProvider.deleteFile(path)

	}
}