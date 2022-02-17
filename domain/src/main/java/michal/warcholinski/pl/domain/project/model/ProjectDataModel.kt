package michal.warcholinski.pl.domain.project.model

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
data class ProjectDataModel(val id: Long,
                            val name: String,
                            val desc: String,
                            val createDate: Long,
                            val phoneNo: String?,
                            val email: String?,
                            val realized: Boolean)