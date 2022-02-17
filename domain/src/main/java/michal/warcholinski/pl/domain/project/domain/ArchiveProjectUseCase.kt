package michal.warcholinski.pl.domain.project.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-24.
 */
class ArchiveProjectUseCase @Inject constructor(private val repo: ProjectRepository) {

	suspend fun execute(projectId: Long) {
		withContext(Dispatchers.IO) {
			repo.archive(projectId)
		}
	}
}