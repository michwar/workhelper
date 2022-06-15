package michal.warcholinski.pl.domain.requests.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-27.
 */
class GetRequestDetailsCase @Inject constructor(private val repo: RequestRepository) {
	
	suspend fun execute(requestId: Long): RequestDataModel? {
		return withContext(Dispatchers.IO) {
			repo.getDetails(requestId)
		}
	}
}