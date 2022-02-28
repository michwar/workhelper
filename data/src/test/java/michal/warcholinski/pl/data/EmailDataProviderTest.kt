package michal.warcholinski.pl.data

import io.mockk.every
import io.mockk.mockk
import michal.warcholinski.pl.data.local.entity.ProjectEntity
import michal.warcholinski.pl.data.local.entity.RequestEntity
import michal.warcholinski.pl.data.local.impl.EmailDataProvider
import michal.warcholinski.pl.data.local.impl.LocalAppFilesProvider
import michal.warcholinski.pl.data.local.impl.SettingsManager
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

/**
 * Created by Michał Warcholiński on 2022-02-28.
 */
class EmailDataProviderTest {

	private val localAppFilesProvider = mockk<LocalAppFilesProvider>()
	private val settingsManager = mockk<SettingsManager>()
	private val emailDataProvider = EmailDataProvider(localAppFilesProvider, settingsManager)

	private val projectName = "Project name"
	private val email = "email@email.com"
	private val projectDesc = "Project desc"
	private val requestDesc = "Request desc"
	private val additionalEmailInfo = "Additional email info"

	@Test
	fun `request email data when all settings flags are set to true test`() {
		val projectEntity =
			ProjectEntity(1L, projectName, projectDesc, 1L, null, email, false)
		val requestEntity =
			RequestEntity(2L, 1L, "requestName", 1L, "test/path", requestDesc)

		mockSettings(useProjectNameAsEmailSubject = true, addProjectDescToEmail = true,
			addRequestDescToEmail = true, defineAdditionalEmailInfo = true)

		val expectedEmailText = "$projectDesc\n$requestDesc\n$additionalEmailInfo"

		val emailDataModel = emailDataProvider.getEmailDataForRequest(projectEntity, requestEntity)

		assertEquals(email, emailDataModel.emailAddress)
		assertEquals(projectName, emailDataModel.subject)
		assertEquals("test/path", emailDataModel.filePath)
		assertEquals(expectedEmailText, emailDataModel.extraText)
	}

	@Test
	fun `project email data when all settings flags are set to true test`() {
		val expectedProjectZipFilePath = "test_zip\\$projectName.zip"
		val projectEntity =
			ProjectEntity(1L, projectName, projectDesc, 1L, null, email, false)

		every { localAppFilesProvider.getZipFilesDir() }.returns(File("test_zip"))

		mockSettings(useProjectNameAsEmailSubject = true, addProjectDescToEmail = true,
			addRequestDescToEmail = true, defineAdditionalEmailInfo = true)

		val expectedEmailText = "$projectDesc\n$additionalEmailInfo"

		val emailDataModel = emailDataProvider.getEmailDataForProject(projectEntity)

		assertEquals(email, emailDataModel.emailAddress)
		assertEquals(projectName, emailDataModel.subject)
		assertEquals(expectedProjectZipFilePath, emailDataModel.filePath)
		assertEquals(expectedEmailText, emailDataModel.extraText)
	}

	@Test
	fun `request email data when all setting's flags are set to false test`() {
		val projectEntity =
			ProjectEntity(1L, projectName, projectDesc, 1L, null, email, false)
		val requestEntity =
			RequestEntity(2L, 1L, "requestName", 1L, null, requestDesc)

		mockSettings(useProjectNameAsEmailSubject = false, addProjectDescToEmail = false,
			addRequestDescToEmail = false, defineAdditionalEmailInfo = false)

		val emailDataModel = emailDataProvider.getEmailDataForRequest(projectEntity, requestEntity)

		assertEquals(email, emailDataModel.emailAddress)
		assertEquals(null, emailDataModel.subject)
		assertEquals(null, emailDataModel.filePath)
		assertEquals(null, emailDataModel.extraText)
	}

	@Test
	fun `project email data when all setting's flags are set to false test`() {
		val expectedProjectZipFilePath = "test_zip\\$projectName.zip"
		val projectEntity =
			ProjectEntity(1L, projectName, projectDesc, 1L, null, email, false)

		every { localAppFilesProvider.getZipFilesDir() }.returns(File("test_zip"))
		mockSettings(useProjectNameAsEmailSubject = false, addProjectDescToEmail = false,
			addRequestDescToEmail = false, defineAdditionalEmailInfo = false)

		val emailDataModel = emailDataProvider.getEmailDataForProject(projectEntity)

		assertEquals(email, emailDataModel.emailAddress)
		assertEquals(null, emailDataModel.subject)
		assertEquals(expectedProjectZipFilePath, emailDataModel.filePath)
		assertEquals(null, emailDataModel.extraText)
	}

	@Test
	fun `email data related to project or request when project and request is null test`() {
		mockSettings(useProjectNameAsEmailSubject = true, addProjectDescToEmail = true,
			addRequestDescToEmail = true, defineAdditionalEmailInfo = true)

		val requestEmailData = emailDataProvider.getEmailDataForRequest(null, null)
		assertEquals(null, requestEmailData.emailAddress)
		assertEquals(null, requestEmailData.subject)
		assertEquals(null, requestEmailData.filePath)
		assertEquals(additionalEmailInfo, requestEmailData.extraText)

		val projectEmailData = emailDataProvider.getEmailDataForProject(null)
		assertEquals(null, projectEmailData.emailAddress)
		assertEquals(null, projectEmailData.subject)
		assertEquals(null, projectEmailData.filePath)
		assertEquals(additionalEmailInfo, projectEmailData.extraText)
	}

	private fun mockSettings(useProjectNameAsEmailSubject: Boolean, addProjectDescToEmail: Boolean,
	                         addRequestDescToEmail: Boolean, defineAdditionalEmailInfo: Boolean) {
		every { settingsManager.useProjectNameAsEmailSubject }.returns(useProjectNameAsEmailSubject)
		every { settingsManager.addProjectDescToEmail }.returns(addProjectDescToEmail)
		every { settingsManager.addRequestDescToEmail }.returns(addRequestDescToEmail)
		every { settingsManager.defineAdditionalEmailInfo }.returns(defineAdditionalEmailInfo)
		every { settingsManager.additionalEmailInfoValue }.returns(additionalEmailInfo)
	}

}