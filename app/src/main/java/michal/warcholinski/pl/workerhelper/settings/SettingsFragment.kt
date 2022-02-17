package michal.warcholinski.pl.workerhelper.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.workerhelper.R

/**
 * Created by Michał Warcholiński on 2022-01-03.
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.preferences, rootKey)
	}
}