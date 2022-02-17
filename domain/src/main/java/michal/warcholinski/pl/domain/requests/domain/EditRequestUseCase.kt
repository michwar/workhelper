package michal.warcholinski.pl.domain.requests.domain

import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
class EditRequestUseCase @Inject constructor(private val repo: RequestRepository) {

	suspend fun execute(id: Long, projectId: Long, name: String, desc: String, filePath: String?) {
		repo.saveRequest(id, projectId, name, desc, filePath)
	}
}