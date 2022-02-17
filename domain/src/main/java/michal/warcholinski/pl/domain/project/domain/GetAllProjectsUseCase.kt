package michal.warcholinski.pl.domain.project.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
class GetAllProjectsUseCase @Inject constructor(private val repo: ProjectRepository) {

	suspend fun execute(realized: Boolean): Flow<List<ProjectDataModel>> =
		withContext(Dispatchers.IO) {
			repo.getAllProjects(realized)
		}
}