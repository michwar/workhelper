package michal.warcholinski.pl.domain.localfiles.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
class DeleteLocalAppFileUseCase @Inject constructor(private val repo: LocalAppFilesRepository) {

	suspend fun execute(path: String): Boolean {
		return withContext(Dispatchers.IO) {
			repo.deleteFile(path)
		}
	}
}