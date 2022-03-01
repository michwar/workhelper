package michal.warcholinski.pl.workerhelper.requestdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.requests.domain.DeleteRequestUseCase
import michal.warcholinski.pl.domain.requests.domain.EditRequestUseCase
import michal.warcholinski.pl.domain.requests.domain.GetDataToComposeRequestEmailUseCase
import michal.warcholinski.pl.domain.requests.domain.GetRequestDetailsCase
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.SendEmailDataViewState
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
	private val state: SavedStateHandle) : BaseViewModel() {

	init {
		viewModelScope.launch(Dispatchers.IO) {
			val requestDetails =
				requestDetailsUseCase.execute(state.get<Long>("requestId").orLongMin())

			val viewState = if (null != requestDetails)
				ViewState.Data(requestDetails)
			else ViewState.NoData

			_viewState.postValue(viewState)
		}
	}

	fun delete() {
		viewModelScope.launch(Dispatchers.IO) {
			deleteRequestUseCase.execute(state.get<Long>("requestId").orLongMin())
			_viewState.postValue(ViewState.Finish)
		}
	}

	fun save(name: String, desc: String, filePath: String?) {
		viewModelScope.launch {
			val id = state.get<Long>("requestId").orLongMin()
			val projectId = state.get<Long>("projectId").orLongMin()
			editRequestUseCase.execute(id, projectId, name, desc, filePath)
			_viewState.postValue(ViewState.Finish)
		}
	}

	fun getDataToComposeEmail() {
		viewModelScope.launch(Dispatchers.IO) {
			val projectId = state.get<Long>("projectId").orLongMin()
			val requestId = state.get<Long>("requestId").orLongMin()

			val emailDataModel = getDataToComposeEmailUseCase.execute(projectId, requestId)
			_viewState.postValue(SendEmailDataViewState(emailDataModel))
		}
	}
}