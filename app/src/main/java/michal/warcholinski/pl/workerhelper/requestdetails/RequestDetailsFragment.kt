package michal.warcholinski.pl.workerhelper.requestdetails

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
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.domain.requests.model.EmailDataModel
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.SendEmailDataViewState
import michal.warcholinski.pl.workerhelper.databinding.FragmentRequestDetailsBinding
import michal.warcholinski.pl.workerhelper.extension.gone
import michal.warcholinski.pl.workerhelper.extension.visible
import java.io.File

/**
 * Created by Michał Warcholiński on 2021-12-23.
 */
@AndroidEntryPoint
class RequestDetailsFragment : Fragment() {

	private var _binding: FragmentRequestDetailsBinding? = null
	private val binding get() = _binding!!

	private val args by navArgs<RequestDetailsFragmentArgs>()

	private val viewModel: RequestDetailsViewModel by viewModels()

	private var isInEditMode = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		sharedElementEnterTransition =
			TransitionInflater.from(context).inflateTransition(android.R.transition.move)
				.apply { duration = 300 }
		sharedElementReturnTransition = null
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentRequestDetailsBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.imagePreview.transitionName = args.imageTransitionName

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			when (viewState) {
				is BaseViewModel.ViewState.Data -> showDetails(viewState.data as RequestDataModel)
				BaseViewModel.ViewState.NoData -> showEmpty()
				BaseViewModel.ViewState.Loading -> TODO()
				is BaseViewModel.ViewState.Error -> TODO()
				BaseViewModel.ViewState.Finish -> findNavController().popBackStack()
				is BaseViewModel.ViewState.Info -> TODO()
				is SendEmailDataViewState -> sendEmail(viewState.emailDataModel)
			}
		})

		viewModel.isInEditMode.observe(viewLifecycleOwner, { isInEditMode ->
			this.isInEditMode = isInEditMode
			if (isInEditMode) {
				editModeView()
			} else {
				readOnlyView()
			}
			activity?.invalidateOptionsMenu()
		})
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
			.intent.setData(Uri.parse("mailto:"))
			.setAction(Intent.ACTION_SENDTO)
			.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
	}

	private fun editModeView() {
		binding.nameEdit.isEnabled = true
		binding.descEdit.isEnabled = true
	}

	private fun readOnlyView() {
		binding.nameEdit.isEnabled = false
		binding.descEdit.isEnabled = false
	}

	private fun showDetails(requestDataModel: RequestDataModel) {
		binding.nameEdit.setText(requestDataModel.name)
		binding.descEdit.setText(requestDataModel.desc)

		if (null != requestDataModel.photoPath) {
			binding.imagePreview.visible()
			binding.photoButton.gone()
			binding.localPhotosButton.gone()
			binding.filesButton.gone()

			Glide
				.with(this)
				.load(requestDataModel.photoPath)
				.into(binding.imagePreview)
		} else {
			binding.imagePreview.gone()
			binding.photoButton.visible()
			binding.localPhotosButton.visible()
			binding.filesButton.visible()
		}

		binding.info.gone()
	}

	private fun showEmpty() {
		binding.info.visible()
		binding.descriptionLayout.gone()
		binding.nameLayout.gone()
		binding.filesButton.gone()
		binding.photoButton.gone()
		binding.imagePreview.gone()
		Snackbar.make(binding.root, getString(R.string.no_data_found_info), Snackbar.LENGTH_SHORT)
			.show()
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		if (isInEditMode) {
			inflater.inflate(R.menu.menu_request_edit, menu)
		} else {
			inflater.inflate(R.menu.menu_request_details, menu)
		}
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_delete -> {
				viewModel.delete()
				true
			}
			R.id.action_edit -> {
				viewModel.editMode()
				true
			}
			R.id.action_send -> {
				viewModel.getDataToComposeEmail()
				true
			}
			R.id.action_save -> {
				viewModel.save(
					name = binding.nameEdit.text.toString(),
					desc = binding.descEdit.text.toString()
				)
				true
			}
			R.id.action_cancel -> {
				viewModel.readOnlyMode()
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