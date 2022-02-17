package michal.warcholinski.pl.workerhelper

import michal.warcholinski.pl.domain.requests.model.EmailDataModel

/**
 * Created by Michał Warcholiński on 2022-01-15.
 */

class SendEmailDataViewState(val emailDataModel: EmailDataModel) : BaseViewModel.ViewState<Nothing>()

class LocalAppFileDeletedViewState(val result: Boolean, val fileName: String) : BaseViewModel.ViewState<Nothing>()

