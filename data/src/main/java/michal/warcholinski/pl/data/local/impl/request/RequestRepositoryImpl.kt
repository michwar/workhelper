package michal.warcholinski.pl.data.local.impl.request

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import michal.warcholinski.pl.data.local.dao.ProjectDao
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.data.local.impl.EmailDataProvider
import michal.warcholinski.pl.data.local.impl.SettingsManager
import michal.warcholinski.pl.data.local.mapper.RequestMapper
import michal.warcholinski.pl.domain.requests.domain.RequestRepository
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import java.util.*
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */
internal class RequestRepositoryImpl @Inject constructor(
	private val projectDao: ProjectDao,
	private val requestDao: RequestDao,
	private val mapper: RequestMapper,
	private val emailDataProvider: EmailDataProvider
) : RequestRepository {

	override fun getAllRequests(projectId: Long): Flow<List<RequestDataModel>> {
		return requestDao.getAllForProject(projectId)
			.map { requests ->
				requests.map { requestEntity -> mapper.fromEntity(requestEntity) }
			}
	}

	override suspend fun getDetails(id: Long): RequestDataModel? {
		val requestEntity = requestDao.getById(id)
		return if (null != requestEntity)
			mapper.fromEntity(requestEntity)
		else
			null
	}

	override suspend fun saveRequest(id: Long?, projectId: Long, name: String, desc: String, filePath: String?) {
		if (null == id)
			requestDao.insert(RequestEntity(id, projectId, name, Date().time, filePath, desc))
		else
			requestDao.update(RequestEntity(id, projectId, name, Date().time, filePath, desc))
	}

	override suspend fun deleteRequest(id: Long) {
		requestDao.delete(id)
	}

	override suspend fun getEmailData(projectId: Long, requestId: Long): EmailDataModel {
		val project = projectDao.getById(projectId)
		val request = requestDao.getById(requestId)
		return emailDataProvider.getEmailDataForRequest(project, request)
	}
}