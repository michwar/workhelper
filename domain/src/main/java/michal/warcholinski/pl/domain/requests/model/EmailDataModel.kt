package michal.warcholinski.pl.domain.requests.model

/**
 * Created by Michał Warcholiński on 2022-01-15.
 */
data class EmailDataModel(
	val emailAddress: String?,
	val subject: String?,
	val extraText: String?,
	val filePath: String?
)