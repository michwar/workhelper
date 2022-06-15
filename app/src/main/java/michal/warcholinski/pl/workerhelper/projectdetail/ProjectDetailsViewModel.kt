package michal.warcholinski.pl.workerhelper.projectdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.project.domain.ArchiveProjectUseCase
import michal.warcholinski.pl.domain.project.domain.DeleteProjectUseCase
import michal.warcholinski.pl.domain.project.domain.GetProjectDetailsUseCase
import michal.warcholinski.pl.domain.project.domain.PrepareAndGetDataToComposeProjectZipEmailUseCase
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import michal.warcholinski.pl.workerhelper.BaseViewModel
import javax.inject.Inject

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@HiltViewModel
class ProjectDetailsViewModel @Inject constructor(
		private val getProjectDetailsUseCase: GetProjectDetailsUseCase,
		private val deleteProjectUseCase: DeleteProjectUseCase,
		private val archiveProjectUseCase: ArchiveProjectUseCase,
		private val getZipProjectDataToComposeEmailUseCase: PrepareAndGetDataToComposeProjectZipEmailUseCase
) : BaseViewModel() {
	
	data class ProjectDetailsViewState(
			val project: ProjectDataModel? = null,
			val emailData: EmailDataModel? = null,
			val loading: Boolean = false,
			val finish: Boolean = false
	)
	
	private val _projectDetailsViewState = MutableLiveData(ProjectDetailsViewState())
	val projectDetailsViewState: LiveData<ProjectDetailsViewState>
		get() = _projectDetailsViewState
	
	fun getDetails(id: Long) {
		viewModelScope.launch {
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(loading = true)
			val projectDataModel = getProjectDetailsUseCase.execute(id)
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(project = projectDataModel, loading = false)
		}
	}
	
	fun deleteProject(id: Long) {
		viewModelScope.launch {
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(loading = true)
			deleteProjectUseCase.execute(id)
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(finish = true)
		}
	}
	
	fun archiveProject(id: Long) {
		viewModelScope.launch {
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(loading = true)
			archiveProjectUseCase.execute(id)
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(finish = true)
		}
	}
	
	fun zipAndSend(id: Long) {
		viewModelScope.launch {
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(loading = true)
			val emailDataModel = getZipProjectDataToComposeEmailUseCase.execute(id)
			_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(loading = false, emailData = emailDataModel)
		}
	}
	
	fun emailDataUsed() {
		_projectDetailsViewState.value = _projectDetailsViewState.value?.copy(emailData = null)
	}
}