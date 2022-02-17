package michal.warcholinski.pl.workerhelper

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Michał Warcholiński on 2022-01-02.
 */
abstract class BaseViewModel : ViewModel() {

	protected val _viewState = MutableLiveData<ViewStateInterface<Any>>()
	val viewState: LiveData<ViewStateInterface<Any>> = _viewState

	 sealed interface ViewStateInterface<out T>

	 sealed class ViewState<out T : Any> : ViewStateInterface<T> {
		data class Data<out T : Any>(val data: T) : ViewState<T>()
		object NoData : ViewState<Nothing>()
		data class Error(val msg: String) : ViewState<Nothing>()
		object Finish : ViewState<Nothing>()
		object Loading : ViewState<Nothing>()
		class Info(val message: String) : ViewState<Nothing>()
	}
}