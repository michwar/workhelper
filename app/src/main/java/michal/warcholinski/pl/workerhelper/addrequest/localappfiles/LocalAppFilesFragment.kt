package michal.warcholinski.pl.workerhelper.addrequest.localappfiles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import michal.warcholinski.pl.workerhelper.databinding.FragmentLocalAppFilesBinding
import michal.warcholinski.pl.workerhelper.extension.gone
import michal.warcholinski.pl.workerhelper.extension.visible

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
@AndroidEntryPoint
class LocalAppFilesFragment : Fragment() {
	
	private var _binding: FragmentLocalAppFilesBinding? = null
	
	private val binding
		get() = _binding!!
	
	private val swipeToDeleteCallback =
		SwipeLeftToDeleteCallback { deletedPosition ->
			viewModel.deleteFile(localAppFilesAdapter.getFileByPosition(deletedPosition))
			localAppFilesAdapter.delete(deletedPosition)
		}
	
	private val localAppFilesAdapter = LocalAppFilesAdapter { fileDataModel ->
		fileSelected(fileDataModel)
	}
	
	private fun fileSelected(fileDataModel: FileDataModel) {
		findNavController().previousBackStackEntry?.savedStateHandle?.set("file_uri", fileDataModel.uri)
		findNavController().popBackStack()
	}
	
	private val viewModel: LocalAppFilesViewModel by viewModels()
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentLocalAppFilesBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		with(binding.locaAppFilesRecycler) {
			adapter = localAppFilesAdapter
			layoutManager = LinearLayoutManager(context)
			ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(this)
		}
		
		viewModel.localAppFilesViewState.observe(viewLifecycleOwner) { viewState ->
			if (viewState.localFiles.isEmpty()) {
				showEmptyViewInfo()
			} else {
				hideEmptyViewInfo()
			}
			loadFiles(viewState.localFiles)
			
			if (null != viewState.messageInfo) {
				showInfo(viewState.messageInfo)
				viewModel.infoShown()
			}
		}
	}
	
	private fun showEmptyViewInfo() {
		binding.emptyInfo.visible()
	}
	
	private fun hideEmptyViewInfo() {
		binding.emptyInfo.gone()
	}
	
	private fun showInfo(message: String) {
		Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
	}
	
	private fun loadFiles(fileUris: List<FileDataModel>) {
		localAppFilesAdapter.update(newFiles = fileUris)
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}