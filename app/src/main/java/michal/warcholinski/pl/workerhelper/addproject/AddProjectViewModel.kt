package michal.warcholinski.pl.workerhelper.addproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.project.domain.AddProjectUseCase
import michal.warcholinski.pl.workerhelper.BaseViewModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */

@HiltViewModel
class AddProjectViewModel @Inject constructor(
	private val addProjectUseCase: AddProjectUseCase
) : BaseViewModel() {

	private val _addProjectViewState = MutableLiveData(AddProjectViewState())
	val addProjectViewState: LiveData<AddProjectViewState>
		get() = _addProjectViewState

	data class AddProjectViewState(
		val loading: Boolean = false,
		val info: String? = null,
		val finish: Boolean = false
	)

	fun addProject(name: String, desc: String, phoneNo: String, mail: String) {
		if (name.isEmpty()) {
			_addProjectViewState.value =
				_addProjectViewState.value?.copy(info = "Name can not be empty")
			return
		}
		_addProjectViewState.value = _addProjectViewState.value?.copy(loading = true)

		viewModelScope.launch {
			addProjectUseCase.execute(name, desc, phoneNo, mail)
			_addProjectViewState.value = _addProjectViewState.value?.copy(finish = true)
		}
	}

	fun infoShown() {
		_addProjectViewState.value = _addProjectViewState.value?.copy(info = null)
	}
}