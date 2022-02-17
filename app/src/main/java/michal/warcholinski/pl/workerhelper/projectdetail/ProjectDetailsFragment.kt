package michal.warcholinski.pl.workerhelper.projectdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.SendEmailDataViewState
import michal.warcholinski.pl.workerhelper.databinding.FragmentProjectDetailsBinding
import michal.warcholinski.pl.workerhelper.extension.visible
import java.io.File

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@AndroidEntryPoint
class ProjectDetailsFragment : Fragment() {

	private var _binding: FragmentProjectDetailsBinding? = null
	private val binding get() = _binding!!

	private val args by navArgs<ProjectDetailsFragmentArgs>()

	private val viewModel: ProjectDetailsViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		viewModel.getDetails(args.projectId)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentProjectDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val projectDetailsPagerAdapter =
			ProjectDetailsPagerAdapter(this, args.projectId, args.projectName,
				listOf(getString(R.string.basic_info), getString(R.string.request_list)))

		with(binding.projectDetailsPager) {
			adapter = projectDetailsPagerAdapter
		}

		TabLayoutMediator(binding.projectDetailTabLayout, binding.projectDetailsPager) { tab, position ->
			tab.text = projectDetailsPagerAdapter.getTabName(position)
		}.attach()

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			when (viewState) {
				BaseViewModel.ViewState.Finish -> findNavController().popBackStack()
				BaseViewModel.ViewState.Loading -> loadingView()
				is SendEmailDataViewState -> sendEmail(viewState.emailDataModel)
			}
		})
	}

	private fun loadingView() {
		binding.progressBar.visible()
	}

	private fun sendEmail(emailDataModel: EmailDataModel) {
		val emailIntent = composeEmailIntent(emailDataModel)
		startActivity(emailIntent)
	}

	private fun composeEmailIntent(emailDataModel: EmailDataModel): Intent {
		val sharedFileUri =
			emailDataModel.filePath?.let { filePath ->
				FileProvider.getUriForFile(requireContext(),
					requireContext().packageName + ".fileprovider", File(filePath))
			}

		return ShareCompat.IntentBuilder(requireContext())
			.setStream(sharedFileUri)
			.setText(emailDataModel.extraText)
			.setSubject(emailDataModel.subject)
			.intent.setData(Uri.parse("mailto:${emailDataModel.emailAddress}"))
			.setAction(Intent.ACTION_SENDTO)
			.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_project_details, menu)
		menu.findItem(R.id.action_archive).isVisible = !args.realized
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_delete -> {
				viewModel.deleteProject(args.projectId)
				true
			}
			R.id.action_archive -> {
				viewModel.archiveProject(args.projectId)
				true
			}
			R.id.zip_and_send -> {
				viewModel.zipAndSend(args.projectId)
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