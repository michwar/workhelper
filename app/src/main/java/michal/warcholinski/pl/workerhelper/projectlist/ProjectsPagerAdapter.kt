package michal.warcholinski.pl.workerhelper.projectlist

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Michał Warcholiński on 2022-01-12.
 */
class ProjectsPagerAdapter(fragment: Fragment, private val labels: List<String>) : FragmentStateAdapter(fragment) {

	override fun getItemCount(): Int = 2

	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> {
				ProjectListFragment.getInstance(realized = false)
			}
			else -> ProjectListFragment.getInstance(realized = true)
		}
	}

	fun getTabName(position: Int) =
		when (position) {
			0 -> labels[0]
			else -> labels[1]
		}
}