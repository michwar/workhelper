package michal.warcholinski.pl.data.local.impl.project

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import michal.warcholinski.pl.data.local.dao.ProjectDao
import michal.warcholinski.pl.data.local.dao.RequestDao
import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.data.local.impl.EmailDataProvider
import michal.warcholinski.pl.data.local.impl.LocalAppFilesProvider
import michal.warcholinski.pl.data.local.mapper.ProjectMapper
import michal.warcholinski.pl.domain.project.domain.ProjectRepository
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import java.io.*
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-02-17.
 */
internal class ProjectRepositoryImpl @Inject constructor(
		private val projectDao: ProjectDao,
		private val requestDao: RequestDao,
		private val mapper: ProjectMapper,
		private val localAppFilesProvider: LocalAppFilesProvider,
		private val emailDataProvider: EmailDataProvider
) : ProjectRepository {
	
	override suspend fun getAllProjects(realized: Boolean): Flow<List<ProjectDataModel>> {
		return projectDao.getAll(realized)
				.map { list -> list.map { entity -> mapper.fromEntity(entity) } }
	}
	
	override suspend fun getDetails(id: Long): ProjectDataModel? {
		val projectEntity = projectDao.getById(id)
		
		return if (null != projectEntity)
			mapper.fromEntity(projectEntity)
		else
			null
	}
	
	override suspend fun addProject(name: String, desc: String, phoneNo: String?, email: String?) {
		projectDao.insert(ProjectEntity(null, name, desc, Date().time, phoneNo, email, realized = false))
	}
	
	override suspend fun archive(projectId: Long) {
		projectDao.archive(projectId)
	}
	
	override suspend fun delete(projectId: Long) {
		projectDao.delete(projectId)
	}
	
	override suspend fun getEmailData(projectId: Long): EmailDataModel {
		val project = projectDao.getById(projectId)
		return emailDataProvider.getEmailDataForProject(project)
	}
	
	override suspend fun createZipFile(projectId: Long) {
		val projectName = projectDao.getById(projectId)?.name ?: return
		
		val files = requestDao.getAllForProject(projectId).first()
				.mapNotNull { requestEntity -> requestEntity.photoPath }
		
		if (files.isEmpty()) return
		
		try {
			val appZipFilesDir = localAppFilesProvider.getZipFilesDir()
			val zipFile = File(appZipFilesDir, "${projectName}.zip")
			val dest = FileOutputStream(zipFile)
			val out = ZipOutputStream(BufferedOutputStream(dest))
			
			for (file in files) {
				val fin = FileInputStream(file)
				val origin = BufferedInputStream(fin)
				
				val zipEntry = ZipEntry(file)
				out.putNextEntry(zipEntry)
				
				out.write(origin.readBytes())
				origin.close()
			}
			out.close()
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	override suspend fun deleteZipFile(projectId: Long) {
		val project = projectDao.getById(projectId)
		if (null != project) {
			val projectName = project.name
			val zipFilesDir = localAppFilesProvider.getZipFilesDir()
			localAppFilesProvider.deleteFile(File(zipFilesDir, "${projectName}.zip").path)
		}
	}
}