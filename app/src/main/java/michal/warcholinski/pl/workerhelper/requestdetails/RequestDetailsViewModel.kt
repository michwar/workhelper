package michal.warcholinski.pl.workerhelper.requestdetails

import android.net.Uri
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.localfiles.domain.CopyGalleryFileUseCase
import michal.warcholinski.pl.domain.requests.domain.DeleteRequestUseCase
import michal.warcholinski.pl.domain.requests.domain.EditRequestUseCase
import michal.warcholinski.pl.domain.requests.domain.GetDataToComposeRequestEmailUseCase
import michal.warcholinski.pl.domain.requests.domain.GetRequestDetailsCase
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import michal.warcholinski.pl.workerhelper.extension.orLongMin
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-27.
 */
@HiltViewModel
class RequestDetailsViewModel @Inject constructor(
		private val requestDetailsUseCase: GetRequestDetailsCase,
		private val deleteRequestUseCase: DeleteRequestUseCase,
		private val editRequestUseCase: EditRequestUseCase,
		private val getDataToComposeEmailUseCase: GetDataToComposeRequestEmailUseCase,
		private val copyGalleryFileUseCase: CopyGalleryFileUseCase,
		private val state: SavedStateHandle
) : ViewModel() {
	
	data class RequestDetailsViewState(
			val requestDetails: RequestDataModel? = null,
			val filePath: String? = null,
			val loading: Boolean = false,
			val finish: Boolean = false,
			val emailDataModel: EmailDataModel? = null,
			val errorMessage: String? = null
	)
	
	private val _requestDetailsViewState = MutableLiveData(RequestDetailsViewState())
	val requestDetailsViewState: LiveData<RequestDetailsViewState>
		get() = _requestDetailsViewState
	
	init {
		viewModelScope.launch {
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(loading = true)
			
			val requestDetails =
				requestDetailsUseCase.execute(state.get<Long>("requestId").orLongMin())
			
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(
				requestDetails = requestDetails,
				filePath = requestDetails?.photoPath,
				loading = false
			)
		}
	}
	
	fun delete() {
		viewModelScope.launch {
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(loading = true)
			
			deleteRequestUseCase.execute(state.get<Long>("requestId").orLongMin())
			
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(finish = true)
		}
	}
	
	fun save(name: String, desc: String) {
		viewModelScope.launch {
			if (name.isEmpty()) {
				_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(errorMessage = "Name can not be empty")
				return@launch
			}
			
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(loading = true)
			
			val id = state.get<Long>("requestId").orLongMin()
			val projectId = state.get<Long>("projectId").orLongMin()
			val filePath = _requestDetailsViewState.value?.filePath
			editRequestUseCase.execute(id, projectId, name, desc, filePath)
			
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(finish = true)
		}
	}
	
	fun getDataToComposeEmail() {
		viewModelScope.launch {
			val projectId = state.get<Long>("projectId").orLongMin()
			val requestId = state.get<Long>("requestId").orLongMin()
			
			val emailDataModel = getDataToComposeEmailUseCase.execute(projectId, requestId)
			
			_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(emailDataModel = emailDataModel)
		}
	}
	
	fun copyFile(uri: Uri?, projectName: String) {
		viewModelScope.launch {
			copyGalleryFileUseCase.invoke(uri, projectName).collect { localFilePath ->
				_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(filePath = localFilePath)
			}
		}
	}
	
	fun emailDataUsed() {
		_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(emailDataModel = null)
	}
	
	fun selectPhotoWithPath(path: String?) {
		_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(filePath = path)
	}
	
	fun errorMessageShown() {
		_requestDetailsViewState.value = _requestDetailsViewState.value?.copy(errorMessage = null)
	}
}