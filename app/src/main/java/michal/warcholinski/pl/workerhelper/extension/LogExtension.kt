package michal.warcholinski.pl.workerhelper.extension

import android.util.Log

/**
 * Created by Michał Warcholiński on 2021-12-27.
 */

fun Any.showELog(msg: String) {
	Log.e(this::class.java.simpleName, msg)
}