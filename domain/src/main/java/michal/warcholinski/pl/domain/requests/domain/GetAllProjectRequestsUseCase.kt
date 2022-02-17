package michal.warcholinski.pl.domain.requests.domain

import kotlinx.coroutines.flow.Flow
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-21.
 */
class GetAllProjectRequestsUseCase @Inject constructor(private val repo: RequestRepository) {

	fun execute(projectId: Long): Flow<List<RequestDataModel>> {
		return repo.getAllRequests(projectId)
	}
}