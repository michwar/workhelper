package michal.warcholinski.pl.domain.requests.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-15.
 */
class GetDataToComposeRequestEmailUseCase @Inject constructor(private val repo: RequestRepository) {
	
	suspend fun execute(projectId: Long, requestId: Long): EmailDataModel {
		return withContext(Dispatchers.IO) {
			repo.getEmailData(projectId, requestId)
		}
	}
}