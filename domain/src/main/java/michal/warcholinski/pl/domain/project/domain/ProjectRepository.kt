package michal.warcholinski.pl.domain.project.domain

import kotlinx.coroutines.flow.Flow
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import michal.warcholinski.pl.domain.requests.model.EmailDataModel

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */
interface ProjectRepository {

	suspend fun getAllProjects(realized: Boolean): Flow<List<ProjectDataModel>>

	suspend fun getDetails(id: Long): ProjectDataModel?

	suspend fun addProject(name: String, desc: String, phoneNo: String?, email: String?)

	suspend fun archive(projectId: Long)

	suspend fun delete(projectId: Long)

	suspend fun getEmailData(projectId: Long): EmailDataModel

	suspend fun createZipFile(projectId: Long)

	suspend fun deleteZipFile(projectId: Long)

}