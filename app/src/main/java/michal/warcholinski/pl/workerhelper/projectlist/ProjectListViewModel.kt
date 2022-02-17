package michal.warcholinski.pl.workerhelper.projectlist

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.project.domain.GetAllProjectsUseCase
import michal.warcholinski.pl.workerhelper.BaseViewModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@HiltViewModel
class ProjectListViewModel @Inject constructor(
	private val getAllProjectsUseCase: GetAllProjectsUseCase
) : BaseViewModel() {

	fun getAllProjects(realized: Boolean) {
		viewModelScope.launch {
			getAllProjectsUseCase.execute(realized).collect {
				_viewState.postValue(ViewState.Data(it))
			}
		}
	}
}

