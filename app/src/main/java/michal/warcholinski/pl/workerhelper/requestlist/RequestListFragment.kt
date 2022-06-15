package michal.warcholinski.pl.workerhelper.requestlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.jakewharton.rxbinding4.appcompat.queryTextChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.FragmentRequestListBinding
import michal.warcholinski.pl.workerhelper.extension.addTo
import michal.warcholinski.pl.workerhelper.extension.orLongMin
import michal.warcholinski.pl.workerhelper.projectdetail.ProjectDetailsFragmentDirections

@AndroidEntryPoint
class RequestListFragment : Fragment() {
	
	private var _binding: FragmentRequestListBinding? = null
	private val binding get() = _binding!!
	
	private val disposableBag = CompositeDisposable()
	private val viewModel: RequestListViewModel by viewModels()
	
	private val requestListAdapter = RequestListAdapter(projectName)
	
	private val projectId
		get() = arguments?.getLong(PROJECT_ID).orLongMin()
	
	private val projectName
		get() = arguments?.getString(PROJECT_NAME).orEmpty()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.fade)
		viewModel.getAllRequests(projectId)
	}
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?,
			savedInstanceState: Bundle?
	): View {
		_binding = FragmentRequestListBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		binding.fab.setOnClickListener { findNavController().navigate(ProjectDetailsFragmentDirections.toAddRequestFragment(projectId, projectName)) }
		
		binding.quickSearch.queryTextChanges()
				.skipInitialValue()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({
					requestListAdapter.filter(it.toString())
				}, {
					requestListAdapter.filter("")
				}).addTo(disposableBag)
		
		with(binding.requestListRecyclerView) {
			adapter = requestListAdapter
			layoutManager = LinearLayoutManager(context)
		}
		
		viewModel.requestListViewState.observe(viewLifecycleOwner) { viewState ->
			
			binding.progressBar.isVisible = viewState.loading
			binding.requestListRecyclerView.isVisible = !viewState.loading
			binding.quickSearch.isVisible = !viewState.loading
			binding.fab.isVisible = !viewState.loading
			
			requestListAdapter.update(viewState.requests, binding.quickSearch.query.toString())
		}
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
		disposableBag.clear()
	}
	
	companion object {
		
		private const val PROJECT_ID = "project_id"
		private const val PROJECT_NAME = "project_name"
		
		fun getInstance(projectId: Long, projectName: String) = RequestListFragment().apply {
			arguments = Bundle().apply {
				putLong(PROJECT_ID, projectId)
				putString(PROJECT_NAME, projectName)
			}
		}
	}
}