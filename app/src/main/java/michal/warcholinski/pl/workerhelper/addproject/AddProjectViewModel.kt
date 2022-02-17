package michal.warcholinski.pl.workerhelper.addproject

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

	fun addProject(name: String, desc: String, phoneNo: String, mail: String) {
		if (name.isEmpty()) {
			_viewState.value = ViewState.Info("Name can not be empty")
			return
		}

		viewModelScope.launch {
			addProjectUseCase.execute(name, desc, phoneNo, mail)
			_viewState.value = ViewState.Finish
		}
	}
}