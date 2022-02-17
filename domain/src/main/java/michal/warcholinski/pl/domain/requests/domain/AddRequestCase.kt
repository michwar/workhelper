package michal.warcholinski.pl.domain.requests.domain

import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-29.
 */
class AddRequestCase @Inject constructor(private val repo: RequestRepository) {

	suspend fun execute(projectId: Long, name: String, desc: String, filePath: String?) {
		repo.saveRequest(null, projectId, name, desc, filePath)
	}
}