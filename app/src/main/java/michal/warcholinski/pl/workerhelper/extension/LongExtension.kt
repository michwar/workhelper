package michal.warcholinski.pl.workerhelper.extension

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Michał Warcholiński on 2021-12-27.
 */

fun Long?.orLongMin(): Long {
	return this ?: Long.MIN_VALUE
}

fun Long.formatDate(): String {
	val dateFormatter = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
	return dateFormatter.format(Date(this))
}