package michal.warcholinski.pl.workerhelper.addrequest

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import michal.warcholinski.pl.domain.localfiles.domain.GetLocalAppFilesDirUseCase
import java.io.File
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-22.
 */
@HiltViewModel
class TakePhotoViewModel @Inject constructor(
		private val getLocalAppFilesDirUseCase: GetLocalAppFilesDirUseCase
) : ViewModel() {
	
	val getLocalAppFilesDir: File
		get() = getLocalAppFilesDirUseCase.execute()
}