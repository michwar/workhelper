package michal.warcholinski.pl.workerhelper.addrequest

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.requests.domain.AddRequestCase
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.extension.orLongMin
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2021-12-29.
 */
@HiltViewModel
class AddRequestViewModel @Inject constructor(
	private val addRequestCase: AddRequestCase,
	private val state: SavedStateHandle
) : BaseViewModel() {

	fun addRequest(name: String, desc: String, filePath: String? = null) {
		viewModelScope.launch(Dispatchers.IO) {
			val projectId = state.get<Long>("projectId").orLongMin()
			addRequestCase.execute(projectId, name, desc, filePath)
			_viewState.postValue(ViewState.Finish)
		}
	}
}