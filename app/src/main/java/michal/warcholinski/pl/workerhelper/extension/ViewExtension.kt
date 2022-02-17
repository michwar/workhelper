package michal.warcholinski.pl.workerhelper.extension

import android.view.View

/**
 * Created by Michał Warcholiński on 2021-12-27.
 */

fun View.visible() {
	this.visibility = View.VISIBLE
}

fun View.invisible() {
	this.visibility = View.INVISIBLE
}

fun View.gone() {
	this.visibility = View.GONE
}