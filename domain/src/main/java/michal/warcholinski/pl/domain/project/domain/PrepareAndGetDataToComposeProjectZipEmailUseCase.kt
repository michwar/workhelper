package michal.warcholinski.pl.domain.project.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-25.
 */
class PrepareAndGetDataToComposeProjectZipEmailUseCase @Inject constructor(
	private val repo: ProjectRepository
) {

	suspend fun execute(projectId: Long): EmailDataModel {
		return withContext(Dispatchers.IO) {
			repo.deleteZipFile(projectId)
			repo.createZipFile(projectId)
			repo.getEmailData(projectId)
		}
	}
}