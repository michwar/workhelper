package michal.warcholinski.pl.domain.project.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
class AddProjectUseCase @Inject constructor(private val repo: ProjectRepository) {

	suspend fun execute(name: String, desc: String, phoneNo: String?, email: String) {
		withContext(Dispatchers.IO) {
			repo.addProject(name, desc, phoneNo, email)
		}
	}
}