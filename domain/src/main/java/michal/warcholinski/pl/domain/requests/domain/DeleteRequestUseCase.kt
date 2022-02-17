package michal.warcholinski.pl.domain.requests.domain

import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
class DeleteRequestUseCase @Inject constructor(private val repo: RequestRepository) {

	suspend fun execute(id: Long) {
		repo.deleteRequest(id)
	}
}