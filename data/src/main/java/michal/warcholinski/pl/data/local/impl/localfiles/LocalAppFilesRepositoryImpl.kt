package michal.warcholinski.pl.data.local.impl.localfiles

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.impl.LocalAppFilesProvider
import michal.warcholinski.pl.domain.localfiles.domain.LocalAppFilesRepository
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */
internal class LocalAppFilesRepositoryImpl @Inject constructor(
	@ApplicationContext private val context: Context,
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

	override fun copyFile(uri: Uri?, projectName: String): Flow<String?> {
		if (null == uri)
			return flowOf(null)

		val localFilesDir = localAppFilesProvider.getLocalFilesDir()
		val currentDate =
			SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
		val localFile =
			File(localFilesDir, "${projectName}_${currentDate}.jpg")

		context.contentResolver.openInputStream(uri)?.use { inputStream ->
			val outputStream = FileOutputStream(localFile)
			val buffer = ByteArray(4 * 1024)
			while (true) {
				val byteCount = inputStream.read(buffer)
				if (byteCount < 0)
					break
				outputStream.write(buffer)
			}

			outputStream.close()
			inputStream.close()
		}

		return flowOf(localFile.path)
	}
}