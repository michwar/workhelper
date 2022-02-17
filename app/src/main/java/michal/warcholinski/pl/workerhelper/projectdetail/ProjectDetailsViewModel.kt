package michal.warcholinski.pl.workerhelper.projectdetail

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.project.domain.ArchiveProjectUseCase
import michal.warcholinski.pl.domain.project.domain.DeleteProjectUseCase
import michal.warcholinski.pl.domain.project.domain.GetProjectDetailsUseCase
import michal.warcholinski.pl.domain.project.domain.PrepareAndGetDataToComposeProjectZipEmailUseCase
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.SendEmailDataViewState
import michal.warcholinski.pl.workerhelper.extension.showELog
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

	fun getDetails(id: Long) {
		viewModelScope.launch {
			val projectDataModel = getProjectDetailsUseCase.execute(id)

			val newViewState = if (null != projectDataModel)
				ViewState.Data(projectDataModel)
			else
				ViewState.NoData

			_viewState.postValue(newViewState)
		}
	}

	fun deleteProject(id: Long) {
		viewModelScope.launch {
			_viewState.value = ViewState.Loading
			deleteProjectUseCase.execute(id)
			_viewState.value = ViewState.Finish
		}
	}

	fun archiveProject(id: Long) {
		viewModelScope.launch {
			archiveProjectUseCase.execute(id)
			_viewState.value = ViewState.Finish
		}
	}

	fun zipAndSend(id: Long) {
		viewModelScope.launch {
			val emailDataModel = getZipProjectDataToComposeEmailUseCase.execute(id)
			_viewState.value = SendEmailDataViewState(emailDataModel)
		}
	}
}