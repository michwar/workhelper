package michal.warcholinski.pl.workerhelper.addrequest

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.localfiles.domain.CopyGalleryFileUseCase
import michal.warcholinski.pl.domain.requests.domain.AddRequestCase
import michal.warcholinski.pl.workerhelper.extension.orLongMin
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-29.
 */
@HiltViewModel
class AddRequestViewModel @Inject constructor(
		private val addRequestCase: AddRequestCase,
		private val copyGalleryFileUseCase: CopyGalleryFileUseCase,
		private val state: SavedStateHandle
) : ViewModel() {
	
	data class AddRequestViewState(
			val filePath: String? = null,
			val loading: Boolean = false,
			val finish: Boolean = false,
			val errorMessage: String? = null
	)
	
	private val _addRequestViewState = MutableLiveData(AddRequestViewState())
	val addRequestViewState: LiveData<AddRequestViewState>
		get() = _addRequestViewState
	
	fun addRequest(name: String, desc: String) {
		viewModelScope.launch {
			if (name.isEmpty()) {
				_addRequestViewState.value = _addRequestViewState.value?.copy(errorMessage = "Name can not be empty")
				return@launch
			}
			
			val filePath = _addRequestViewState.value?.filePath
			if (null != filePath) {
				_addRequestViewState.value = _addRequestViewState.value?.copy(loading = true)
				val projectId = state.get<Long>("projectId").orLongMin()
				addRequestCase.execute(projectId, name, desc, filePath)
				_addRequestViewState.value = _addRequestViewState.value?.copy(finish = true)
			} else {
				_addRequestViewState.value = _addRequestViewState.value?.copy(errorMessage = "Choose any file")
			}
		}
	}
	
	fun copyFile(uri: Uri?, projectName: String) {
		viewModelScope.launch {
			_addRequestViewState.value = _addRequestViewState.value?.copy(loading = true)
			copyGalleryFileUseCase.invoke(uri, projectName).collect { localFilePath ->
				_addRequestViewState.value = _addRequestViewState.value?.copy(filePath = localFilePath, loading = false)
			}
		}
	}
	
	fun selectPhotoWithPath(path: String?) {
		_addRequestViewState.value = _addRequestViewState.value?.copy(filePath = path)
	}
	
	fun errorMessageShown() {
		_addRequestViewState.value = _addRequestViewState.value?.copy(errorMessage = null)
	}
}