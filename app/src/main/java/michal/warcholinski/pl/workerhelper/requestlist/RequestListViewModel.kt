package michal.warcholinski.pl.workerhelper.requestlist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.requests.domain.DeleteRequestUseCase
import michal.warcholinski.pl.domain.requests.domain.GetAllProjectRequestsUseCase
import michal.warcholinski.pl.workerhelper.BaseViewModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-21.
 */
@HiltViewModel
class RequestListViewModel @Inject constructor(
	private val getAllRequestsCase: GetAllProjectRequestsUseCase,
	private val deleteRequestUseCase: DeleteRequestUseCase
) : BaseViewModel() {

	fun getAllRequests(projectId: Long) {
		viewModelScope.launch {
			_viewState.value = ViewState.Loading
			getAllRequestsCase.execute(projectId).collectLatest { requests ->
				_viewState.postValue(ViewState.Data(requests))
			}
		}
	}

	fun deleteRequest(id: Long) {
		viewModelScope.launch(Dispatchers.IO) {
			deleteRequestUseCase.execute(id)
		}
	}
}