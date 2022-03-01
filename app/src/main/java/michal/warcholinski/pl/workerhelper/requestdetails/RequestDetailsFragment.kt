package michal.warcholinski.pl.workerhelper.requestdetails

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
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
import michal.warcholinski.pl.workerhelper.addrequest.AddRequestFragmentDirections
import michal.warcholinski.pl.workerhelper.databinding.FragmentRequestDetailsBinding
import michal.warcholinski.pl.workerhelper.extension.gone
import michal.warcholinski.pl.workerhelper.extension.showELog
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

	private var imagePath: String? = null

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
				is BaseViewModel.ViewState.Data -> {
					val requestDataModel = viewState.data as RequestDataModel
					imagePath = requestDataModel.photoPath
					showDetails(requestDataModel)
				}
				BaseViewModel.ViewState.NoData -> showEmpty()
				BaseViewModel.ViewState.Loading -> TODO()
				is BaseViewModel.ViewState.Error -> TODO()
				BaseViewModel.ViewState.Finish -> findNavController().popBackStack()
				is BaseViewModel.ViewState.Info -> TODO()
				is SendEmailDataViewState -> sendEmail(viewState.emailDataModel)
			}
		})

		findNavController()
			.currentBackStackEntry
			?.savedStateHandle
			?.getLiveData<Uri>("file_uri")
			?.observe(viewLifecycleOwner, { photoUri ->
				imagePath = photoUri?.path
				showRequestImage(photoPath = photoUri?.path)
			})

		binding.photoButton.setOnClickListener { findNavController().navigate(RequestDetailsFragmentDirections.toTakePhotoFragment(args.projectName)) }
		binding.localPhotosButton.setOnClickListener {
			findNavController().navigate(AddRequestFragmentDirections.toLocalAppFilesFragment())
		}
		binding.filesButton.setOnClickListener { openFileChooser() }
	}

	private fun openFileChooser() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "*/*"
		intent.addCategory(Intent.CATEGORY_OPENABLE)

		try {
			startActivityForResult(
				Intent.createChooser(intent, "Select a File to Upload"),
				150)
		} catch (ex: ActivityNotFoundException) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(requireContext(), "Please install a File Manager.",
				Toast.LENGTH_SHORT).show()
		}
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

	private fun showDetails(requestDataModel: RequestDataModel) {
		binding.nameEdit.setText(requestDataModel.name)
		binding.descEdit.setText(requestDataModel.desc)

		showRequestImage(requestDataModel.photoPath)

		binding.info.gone()
	}

	private fun showRequestImage(photoPath: String?) {
		if (null != photoPath) {
			binding.imagePreview.visible()
			binding.photoButton.gone()
			binding.localPhotosButton.gone()
			binding.filesButton.gone()

			Glide
				.with(this)
				.load(photoPath)
				.into(binding.imagePreview)
		} else {
			binding.imagePreview.gone()
			binding.photoButton.visible()
			binding.localPhotosButton.visible()
			binding.filesButton.visible()
		}
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

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == 150 && resultCode == Activity.RESULT_OK) {
			showELog("${data?.data}")
			imagePath = data?.data?.path
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_request_details, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_delete -> {
				viewModel.delete()
				true
			}
			R.id.action_send -> {
				viewModel.getDataToComposeEmail()
				true
			}
			R.id.action_save -> {
				viewModel.save(
					name = binding.nameEdit.text.toString(),
					desc = binding.descEdit.text.toString(),
					filePath = imagePath
				)
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