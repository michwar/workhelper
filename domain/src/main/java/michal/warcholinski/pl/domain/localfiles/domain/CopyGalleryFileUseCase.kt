package michal.warcholinski.pl.domain.localfiles.domain

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-03-02.
 */
class CopyGalleryFileUseCase @Inject constructor(private val repo: LocalAppFilesRepository) {

	suspend operator fun invoke(uri: Uri?, projectName: String): Flow<String?> {
		return withContext(Dispatchers.Default) {
			repo.copyFile(uri, projectName)
		}
	}
}