package michal.warcholinski.pl.data.local.impl

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Michał Warcholiński on 2022-01-04.
 */
@Singleton
internal class SettingsManager @Inject constructor(private val sharedPreferences: SharedPreferences) {

	private val defineAdditionalMailInfoKey = "define_additional_mail_info"
	private val additionalMailInfoValueKey = "additional_mail_info_value"
	private val useProjectNameAsEmailSubjectKey = "use_project_name_as_email_subject"
	private val addProjectDescToEmailKey = "add_project_desc_to_email"
	private val addRequestDescToEmailKey = "add_request_desc_to_email"

	val defineAdditionalEmailInfo: Boolean
		get() = sharedPreferences.getBoolean(defineAdditionalMailInfoKey, false)

	val additionalEmailInfoValue: String
		get() = sharedPreferences.getString(additionalMailInfoValueKey, "").orEmpty()

	val useProjectNameAsEmailSubject: Boolean
		get() = sharedPreferences.getBoolean(useProjectNameAsEmailSubjectKey, false)

	val addProjectDescToEmail: Boolean
		get() = sharedPreferences.getBoolean(addProjectDescToEmailKey, false)

	val addRequestDescToEmail: Boolean
		get() = sharedPreferences.getBoolean(addRequestDescToEmailKey, false)
}