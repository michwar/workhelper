package michal.warcholinski.pl.workerhelper.addrequest.localappfiles

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.localfiles.domain.DeleteLocalAppFileUseCase
import michal.warcholinski.pl.domain.localfiles.domain.GetUnusedLocalAppFilesUseCase
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.LocalAppFileDeletedViewState
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
@HiltViewModel
class LocalAppFilesViewModel @Inject constructor(
	private val getLocalAppFilesUseCase: GetUnusedLocalAppFilesUseCase,
	private val deleteLocalAppFileUseCase: DeleteLocalAppFileUseCase
) : BaseViewModel() {

	init {
		viewModelScope.launch {
			getLocalAppFilesUseCase.execute().collectLatest { files ->
				_viewState.postValue(ViewState.Data(files))
			}
		}
	}

	fun deleteFile(fileToDelete: FileDataModel) {
		viewModelScope.launch {
			val fileDeleted = deleteLocalAppFileUseCase.execute(fileToDelete.path)

			_viewState.value = LocalAppFileDeletedViewState(fileDeleted, fileToDelete.name)
		}
	}
}