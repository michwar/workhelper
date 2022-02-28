package michal.warcholinski.pl.data.local.impl

import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import java.io.File
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-02-28.
 */
internal class EmailDataProvider @Inject constructor(private val localAppFilesProvider: LocalAppFilesProvider,
                                                     private val settingsManager: SettingsManager) {


	fun getEmailDataForProject(project: ProjectEntity?): EmailDataModel {
		val subject = prepareSubject(project)
		val extraEmailText = prepareExtraEmailText(project, null)

		val projectName = project?.name
		val zipFilePath = if (null != projectName) {
			val zipFilesDir = localAppFilesProvider.getZipFilesDir()
			File(zipFilesDir, "${projectName}.zip").path
		} else
			null

		return EmailDataModel(project?.email, subject, extraEmailText, zipFilePath)
	}

	private fun prepareSubject(project: ProjectEntity?): String? {
		return if (settingsManager.useProjectNameAsEmailSubject)
			project?.name
		else null
	}

	fun getEmailDataForRequest(project: ProjectEntity?, request: RequestEntity?): EmailDataModel {
		val subject = prepareSubject(project)
		val extraEmailText = prepareExtraEmailText(project, request)

		return EmailDataModel(project?.email, subject, extraEmailText, request?.photoPath)
	}

	private fun prepareExtraEmailText(project: ProjectEntity?, request: RequestEntity?): String? {
		var extraEmailText = ""

		if (settingsManager.addProjectDescToEmail) {
			val projectDesc = project?.desc
			if (!projectDesc.isNullOrEmpty()) {
				extraEmailText += projectDesc
				extraEmailText += "\n"
			}
		}

		if (settingsManager.addRequestDescToEmail) {
			val requestDesc = request?.desc
			if (!requestDesc.isNullOrEmpty()) {
				extraEmailText += requestDesc
				extraEmailText += "\n"
			}
		}
		if (settingsManager.defineAdditionalEmailInfo) {
			val additionalEmailUserInfo = settingsManager.additionalEmailInfoValue
			if (additionalEmailUserInfo.isNotEmpty()) {
				extraEmailText += additionalEmailUserInfo
			}
		}

		return if (extraEmailText.isEmpty()) null else extraEmailText
	}
}