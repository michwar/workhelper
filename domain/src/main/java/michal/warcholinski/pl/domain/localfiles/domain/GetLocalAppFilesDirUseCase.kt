package michal.warcholinski.pl.domain.localfiles.domain

import java.io.File
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-22.
 */
class GetLocalAppFilesDirUseCase @Inject constructor(private val repo: LocalAppFilesRepository) {

	fun execute(): File {
		return repo.getDir()
	}
}