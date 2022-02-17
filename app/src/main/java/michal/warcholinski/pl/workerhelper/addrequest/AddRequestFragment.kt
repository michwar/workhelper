package michal.warcholinski.pl.workerhelper.addrequest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.workerhelper.BaseViewModel
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.FragmentAddRequestBinding
import michal.warcholinski.pl.workerhelper.extension.gone
import michal.warcholinski.pl.workerhelper.extension.showELog
import michal.warcholinski.pl.workerhelper.extension.visible


@AndroidEntryPoint
class AddRequestFragment : Fragment() {

	private var _binding: FragmentAddRequestBinding? = null
	private val binding get() = _binding!!

	private val args by navArgs<AddRequestFragmentArgs>()

	private val viewModel: AddRequestViewModel by viewModels()

	private var imageUri: Uri? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enterTransition =
			TransitionInflater.from(context).inflateTransition(R.transition.slide_right)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {

		_binding = FragmentAddRequestBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel.viewState.observe(viewLifecycleOwner, { viewState ->
			when (viewState) {
				BaseViewModel.ViewState.Finish -> findNavController().popBackStack()
			}
		})

		findNavController()
			.currentBackStackEntry
			?.savedStateHandle
			?.getLiveData<Uri>("file_uri")
			?.observe(viewLifecycleOwner, { photoUri ->
				imageUri = photoUri
				loadImagePreview()
				showELog("Received uri of a photo: $photoUri")
			})

		binding.photoButton.setOnClickListener { startCamera() }
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

	private fun loadImagePreview() {
		Glide
			.with(this)
			.load(imageUri)
			.into(binding.imagePreview)

		binding.imagePreview.visible()
		binding.photoButton.gone()
		binding.filesButton.gone()
		binding.localPhotosButton.gone()
	}

	private fun startCamera() {
		findNavController().navigate(AddRequestFragmentDirections.toTakePhotoFragment(args.projectName))
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_add_request, menu)
		super.onCreateOptionsMenu(menu, inflater)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_add -> {
				addRequest()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == 150 && resultCode == Activity.RESULT_OK) {
			showELog("${data?.toUri(0)}")
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun addRequest() {
		val name = binding.nameEdit.text.toString()
		if (name.isEmpty()) {
			Toast.makeText(requireContext(), "name can not be empty", Toast.LENGTH_LONG).show()
			return
		}

		viewModel.addRequest(
			name = name,
			desc = binding.descEdit.text.toString(),
			filePath = imageUri?.path
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}