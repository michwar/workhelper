package michal.warcholinski.pl.data.local.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
internal class LocalAppFilesProvider @Inject constructor(@ApplicationContext private val context: Context) {

	fun getLocalFilesDir(): File {
		val filesDir = File(context.filesDir, "worker_helper_files")
		if (!filesDir.exists())
			filesDir.mkdirs()

		return filesDir
	}

	fun getZipFilesDir(): File {
		val zipFilesDir = File(getLocalFilesDir(), "zip_files")

		if (!zipFilesDir.exists())
			zipFilesDir.mkdirs()

		return zipFilesDir
	}

	fun getAllLocalFiles(): Flow<List<File>> {
		return flowOf(getLocalFilesDir().listFiles()?.toList().orEmpty().filter { !it.isDirectory })
	}

	fun deleteFile(path: String): Boolean {
		return File(path).delete()
	}
}