package michal.warcholinski.pl.domain.requests.model

/**
 * Created by Michał Warcholiński on 2021-12-21.
 */
data class RequestDataModel(
	val id: Long,
	val projectId: Long,
	val name: String,
	val dateOfCreate: Long,
	val photoPath: String?,
	val desc: String
)