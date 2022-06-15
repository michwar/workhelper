package michal.warcholinski.pl.workerhelper.addrequest.localappfiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.localfiles.domain.DeleteLocalAppFileUseCase
import michal.warcholinski.pl.domain.localfiles.domain.GetUnusedLocalAppFilesUseCase
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import michal.warcholinski.pl.workerhelper.BaseViewModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
@HiltViewModel
class LocalAppFilesViewModel @Inject constructor(
		private val getLocalAppFilesUseCase: GetUnusedLocalAppFilesUseCase,
		private val deleteLocalAppFileUseCase: DeleteLocalAppFileUseCase
) : BaseViewModel() {
	
	data class LocalAppFilesViewState(
			val localFiles: List<FileDataModel> = emptyList(),
			val messageInfo: String? = null
	)
	
	private val _localAppFilesViewState = MutableLiveData(LocalAppFilesViewState())
	val localAppFilesViewState: LiveData<LocalAppFilesViewState>
		get() = _localAppFilesViewState
	
	init {
		viewModelScope.launch {
			getLocalAppFilesUseCase.execute().collectLatest { files ->
				_localAppFilesViewState.value = _localAppFilesViewState.value?.copy(localFiles = files)
			}
		}
	}
	
	fun deleteFile(fileToDelete: FileDataModel) {
		viewModelScope.launch {
			val fileDeleted = deleteLocalAppFileUseCase.execute(fileToDelete.path)
			val info = if (fileDeleted) {
				"${fileToDelete.name} deleted"
			} else {
				"${fileToDelete.name} could not be deleted"
			}
			_localAppFilesViewState.value = _localAppFilesViewState.value?.copy(messageInfo = info)
		}
	}
	
	fun infoShown() {
		_localAppFilesViewState.value = _localAppFilesViewState.value?.copy(messageInfo = null)
	}
}