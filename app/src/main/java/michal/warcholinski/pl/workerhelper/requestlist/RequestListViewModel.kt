package michal.warcholinski.pl.workerhelper.requestlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.requests.domain.DeleteRequestUseCase
import michal.warcholinski.pl.domain.requests.domain.GetAllProjectRequestsUseCase
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-21.
 */
@HiltViewModel
class RequestListViewModel @Inject constructor(
		private val getAllRequestsCase: GetAllProjectRequestsUseCase,
		private val deleteRequestUseCase: DeleteRequestUseCase
) : ViewModel() {
	
	data class RequestListViewState(
			val requests: List<RequestDataModel> = emptyList(),
			val loading: Boolean = false
	)
	
	private val _requestListViewState = MutableLiveData(RequestListViewState())
	val requestListViewState: LiveData<RequestListViewState>
		get() = _requestListViewState
	
	fun getAllRequests(projectId: Long) {
		viewModelScope.launch {
			_requestListViewState.value = _requestListViewState.value?.copy(loading = true)
			getAllRequestsCase.execute(projectId).collectLatest { requests ->
				_requestListViewState.value = _requestListViewState.value?.copy(loading = false, requests = requests)
			}
		}
	}
	
	fun deleteRequest(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			deleteRequestUseCase.execute(id)
		}
	}
}