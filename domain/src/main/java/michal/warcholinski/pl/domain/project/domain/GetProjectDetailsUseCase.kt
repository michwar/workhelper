package michal.warcholinski.pl.domain.project.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
class GetProjectDetailsUseCase @Inject constructor(private val repo: ProjectRepository) {

	suspend fun execute(id: Long): ProjectDataModel? {
		return withContext(Dispatchers.IO){
			repo.getDetails(id)
		}
	}
}