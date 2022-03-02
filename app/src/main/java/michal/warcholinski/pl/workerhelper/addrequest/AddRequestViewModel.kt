package michal.warcholinski.pl.workerhelper.addrequest

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import michal.warcholinski.pl.domain.localfiles.domain.CopyGalleryFileUseCase
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
	private val copyGalleryFileUseCase: CopyGalleryFileUseCase,
	private val state: SavedStateHandle
) : BaseViewModel() {

	private val _copiedFilePath = MutableLiveData<String?>()
	val copiedFilePath: LiveData<String?>
		get() = _copiedFilePath

	fun addRequest(name: String, desc: String, filePath: String? = null) {
		viewModelScope.launch(Dispatchers.IO) {
			val projectId = state.get<Long>("projectId").orLongMin()
			addRequestCase.execute(projectId, name, desc, filePath)
			_viewState.postValue(ViewState.Finish)
		}
	}

	fun copyFile(uri: Uri?, projectName: String) {
		viewModelScope.launch {
			copyGalleryFileUseCase.invoke(uri, projectName).collect { localFilePath ->
				_copiedFilePath.value = localFilePath
			}
		}
	}
}