package michal.warcholinski.pl.workerhelper.projectlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.project.domain.GetAllProjectsUseCase
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@HiltViewModel
class ProjectListViewModel @Inject constructor(
		private val getAllProjectsUseCase: GetAllProjectsUseCase
) : ViewModel() {
	
	data class ProjectListViewState(val projects: List<ProjectDataModel> = emptyList())
	
	private val _projectListViewState = MutableLiveData(ProjectListViewState())
	val projectListViewState: LiveData<ProjectListViewState>
		get() = _projectListViewState
	
	fun getAllProjects(realized: Boolean) {
		viewModelScope.launch {
			getAllProjectsUseCase.execute(realized).collect {
				_projectListViewState.value = _projectListViewState.value?.copy(projects = it)
			}
		}
	}
}

