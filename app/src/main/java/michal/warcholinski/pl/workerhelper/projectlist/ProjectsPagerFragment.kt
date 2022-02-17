package michal.warcholinski.pl.workerhelper.projectlist

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.FragmentProjectsPagerBinding

/**
 * Created by Michał Warcholiński on 2022-01-12.
 */
@AndroidEntryPoint
class ProjectsPagerFragment : Fragment() {

	private var _binding: FragmentProjectsPagerBinding? = null
	private val binding get() = _binding!!

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentProjectsPagerBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val projectsPagerAdapter = ProjectsPagerAdapter(this,
			listOf(getString(R.string.in_progress), getString(R.string.realized)))

		with(binding.projectsPager) {
			adapter = projectsPagerAdapter
		}

		TabLayoutMediator(binding.projectsTabLayout, binding.projectsPager) { tab, position ->
			tab.text = projectsPagerAdapter.getTabName(position)
		}.attach()

		binding.addProjectFab.setOnClickListener { findNavController().navigate(ProjectsPagerFragmentDirections.toAddProjectFragment()) }
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_projects_pager, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> {
				findNavController().navigate(ProjectsPagerFragmentDirections.toSettingsFragment())
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}