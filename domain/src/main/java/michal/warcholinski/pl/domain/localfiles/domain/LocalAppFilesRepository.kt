package michal.warcholinski.pl.domain.localfiles.domain

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import java.io.File

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */

interface LocalAppFilesRepository {

	fun getDir(): File

	suspend fun getLocalAppFiles(): Flow<List<FileDataModel>>

	suspend fun deleteFile(path: String): Boolean

	fun copyFile(uri: Uri?, projectName: String): Flow<String?>
}