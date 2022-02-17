package michal.warcholinski.pl.domain.requests.domain

import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-15.
 */
class GetDataToComposeRequestEmailUseCase @Inject constructor(private val repo: RequestRepository) {

	suspend fun execute(projectId: Long, requestId: Long): EmailDataModel {
		return repo.getEmailData(projectId, requestId)
	}
}