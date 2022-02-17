package michal.warcholinski.pl.domain.requests.domain

import kotlinx.coroutines.flow.Flow
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import michal.warcholinski.pl.domain.requests.model.RequestDataModel

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */
interface RequestRepository {

	fun getAllRequests(projectId: Long): Flow<List<RequestDataModel>>

	suspend fun getDetails(id: Long): RequestDataModel?

	suspend fun saveRequest(id: Long?, projectId: Long, name: String, desc: String, filePath: String?)

	suspend fun deleteRequest(id: Long)

	suspend fun getEmailData(projectId: Long, requestId: Long): EmailDataModel
}