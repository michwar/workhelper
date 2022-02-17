package michal.warcholinski.pl.workerhelper.extension

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Created by Michał Warcholiński on 2022-01-13.
 */
fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
	compositeDisposable.add(this)
}