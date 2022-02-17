package michal.warcholinski.pl.workerhelper.addproject

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.FragmentAddProjectBinding

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@AndroidEntryPoint
class AddProjectFragment : Fragment() {

	private var _binding: FragmentAddProjectBinding? = null

	private val binding
		get() = _binding!!

	private val viewModel: AddProjectViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentAddProjectBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			when (viewState) {
				BaseViewModel.ViewState.Finish -> findNavController().popBackStack()
				is BaseViewModel.ViewState.Info -> showInfo(viewState.message)
			}
		})
	}

	private fun showInfo(message: String) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_add_project, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_add -> {
				addProject()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	private fun addProject() {
		val name = binding.nameEdit.text.toString()
		val desc = binding.descEdit.text.toString()
		val phoneNo = binding.phoneEdit.text.toString()
		val mail = binding.mailEdit.text.toString()
		viewModel.addProject(name, desc, phoneNo, mail)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}