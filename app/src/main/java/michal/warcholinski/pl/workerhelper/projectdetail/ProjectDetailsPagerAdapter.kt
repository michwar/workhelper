package michal.warcholinski.pl.workerhelper.projectdetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import michal.warcholinski.pl.workerhelper.requestlist.RequestListFragment

/**
 * Created by Michał Warcholiński on 2022-01-09.
 */
class ProjectDetailsPagerAdapter(
	fragment: Fragment,
	private val projectId: Long,
	private val projectName: String,
	private val labels: List<String>
) : FragmentStateAdapter(fragment) {

	override fun getItemCount(): Int = 2

	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> {
				ProjectDetailsBasicInfoFragment.getInstance(projectId)
			}
			else -> RequestListFragment.getInstance(projectId, projectName)
		}
	}

	fun getTabName(position: Int) =
		when (position) {
			0 -> labels[0]
			else -> labels[1]
		}
}