package michal.warcholinski.pl.workerhelper.addrequest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.FragmentAddRequestBinding


@AndroidEntryPoint
class AddRequestFragment : Fragment() {
	
	private var _binding: FragmentAddRequestBinding? = null
	private val binding get() = _binding!!
	
	private val args by navArgs<AddRequestFragmentArgs>()
	
	private val viewModel: AddRequestViewModel by viewModels()
	
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
		
		viewModel.addRequestViewState.observe(viewLifecycleOwner) { viewState ->
			if (viewState.finish)
				findNavController().popBackStack()
			
			val filePath = viewState.filePath
			
			if (null != filePath)
				loadImagePreview(filePath)
			
			if (null != viewState.errorMessage)
				showErrorMessage(viewState.errorMessage)
			
			binding.filesButton.isVisible = !viewState.loading && null == filePath
			binding.localPhotosButton.isVisible = !viewState.loading && null == filePath
			binding.photoButton.isVisible = !viewState.loading && null == filePath
			binding.imagePreview.isVisible = null != filePath
			
			binding.nameEdit.isEnabled = !viewState.loading
			binding.descEdit.isEnabled = !viewState.loading
			binding.addRequestProgressBar.isVisible = viewState.loading
		}
		
		findNavController()
				.currentBackStackEntry
				?.savedStateHandle
				?.getLiveData<Uri>("file_uri")
				?.observe(viewLifecycleOwner) { photoUri ->
					viewModel.selectPhotoWithPath(photoUri.path)
				}
		
		binding.photoButton.setOnClickListener { startCamera() }
		binding.localPhotosButton.setOnClickListener {
			findNavController().navigate(AddRequestFragmentDirections.toLocalAppFilesFragment())
		}
		binding.filesButton.setOnClickListener { openFileChooser() }
	}
	
	private fun showErrorMessage(errorMessage: String) {
		Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
				.show()
		viewModel.errorMessageShown()
	}
	
	private fun openFileChooser() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		
		try {
			startActivityForResult(
				Intent.createChooser(intent, getString(R.string.file_chooser_title)),
				150
			)
		} catch (ex: ActivityNotFoundException) {
			Toast.makeText(
				requireContext(), getString(R.string.file_manager_error),
				Toast.LENGTH_SHORT
			).show()
		}
	}
	
	private fun loadImagePreview(filePath: String) {
		Glide
				.with(this)
				.load(filePath)
				.into(binding.imagePreview)
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
		viewModel.addRequest(
			name = binding.nameEdit.text.toString(),
			desc = binding.descEdit.text.toString(),
		)
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}