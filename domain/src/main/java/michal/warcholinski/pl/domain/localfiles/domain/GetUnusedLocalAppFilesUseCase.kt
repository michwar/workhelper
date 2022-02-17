package michal.warcholinski.pl.domain.localfiles.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
class GetUnusedLocalAppFilesUseCase @Inject constructor(private val repo: LocalAppFilesRepository) {

	suspend fun execute(): Flow<List<FileDataModel>> =
		withContext(Dispatchers.Default) {
			repo.getLocalAppFiles()
		}
}