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
import michal.warcholinski.pl.workerhelper.extension.visible


@AndroidEntryPoint
class AddRequestFragment : Fragment() {

	private var _binding: FragmentAddRequestBinding? = null
	private val binding get() = _binding!!

	private val args by navArgs<AddRequestFragmentArgs>()

	private val viewModel: AddRequestViewModel by viewModels()

	private var imagePath: String? = null

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

		viewModel.copiedFilePath.observe(viewLifecycleOwner, { copiedFilePath ->
			if (null == copiedFilePath)
				binding.filesButton.isEnabled = true
			else {
				imagePath = copiedFilePath
				loadImagePreview()
			}
		})

		findNavController()
			.currentBackStackEntry
			?.savedStateHandle
			?.getLiveData<Uri>("file_uri")
			?.observe(viewLifecycleOwner, { photoUri ->
				imagePath = photoUri.path
				loadImagePreview()
			})

		binding.photoButton.setOnClickListener { startCamera() }
		binding.localPhotosButton.setOnClickListener {
			findNavController().navigate(AddRequestFragmentDirections.toLocalAppFilesFragment())
		}
		binding.filesButton.setOnClickListener { openFileChooser() }
	}

	private fun openFileChooser() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"

		try {
			startActivityForResult(
				Intent.createChooser(intent, "Select a File to Upload"),
				150)
		} catch (ex: ActivityNotFoundException) {
			Toast.makeText(requireContext(), "Please install a File Manager.",
				Toast.LENGTH_SHORT).show()
		}
	}

	private fun loadImagePreview() {
		if (null != imagePath) {
			binding.imagePreview.visible()
			binding.photoButton.gone()
			binding.filesButton.gone()
			binding.localPhotosButton.gone()

			Glide
				.with(this)
				.load(imagePath)
				.into(binding.imagePreview)

		} else {
			binding.imagePreview.gone()
			binding.photoButton.visible()
			binding.filesButton.visible()
			binding.localPhotosButton.visible()
		}
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
			binding.filesButton.isEnabled = false
			copyFile(data?.data)
		}
		super.onActivityResult(requestCode, resultCode, data)
	}

	private fun copyFile(uri: Uri?) {
		viewModel.copyFile(uri, args.projectName)
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
			filePath = imagePath
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}