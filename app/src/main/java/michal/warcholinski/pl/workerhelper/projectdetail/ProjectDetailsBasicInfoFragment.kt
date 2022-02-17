package michal.warcholinski.pl.workerhelper.projectdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.databinding.FragmentProjectDetailsBasicInfoBinding
import michal.warcholinski.pl.workerhelper.extension.formatDate
import michal.warcholinski.pl.workerhelper.extension.orLongMin

/**
 * Created by Michał Warcholiński on 2022-01-09.
 */
@AndroidEntryPoint
class ProjectDetailsBasicInfoFragment : Fragment() {

	private var _binding: FragmentProjectDetailsBasicInfoBinding? = null
	private val binding get() = _binding!!

	private val viewModel: ProjectDetailsViewModel by viewModels()

	private val projectId
		get() = arguments?.getLong(PROJECT_ID).orLongMin()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.getDetails(projectId)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentProjectDetailsBasicInfoBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			when (viewState) {
				is BaseViewModel.ViewState.Data -> showDataView(viewState.data as ProjectDataModel)
				is BaseViewModel.ViewState.Error -> {
				}
				BaseViewModel.ViewState.Finish -> {
				}
				is BaseViewModel.ViewState.Info -> {
				}
				BaseViewModel.ViewState.Loading -> {
				}
				BaseViewModel.ViewState.NoData -> showEmptyView()
			}
		})

		binding.phone.setOnClickListener { openCallDialer() }
	}

	private fun openCallDialer() {
		val intent = Intent(Intent.ACTION_DIAL)
		intent.data = Uri.parse("tel:${binding.phone.value}")
		startActivity(intent)
	}

	private fun showDataView(projectDataModel: ProjectDataModel) {
		binding.name.value = projectDataModel.name
		binding.desc.value = projectDataModel.desc
		binding.createDate.value = projectDataModel.createDate.formatDate()
		binding.email.value = projectDataModel.email.orEmpty()
		binding.phone.value = projectDataModel.phoneNo.orEmpty()
	}

	private fun showEmptyView() {

	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}

	companion object {
		private const val PROJECT_ID = "project_id"

		fun getInstance(projectId: Long) = ProjectDetailsBasicInfoFragment().apply {
			arguments = Bundle().apply { putLong(PROJECT_ID, projectId) }
		}
	}
}