package michal.warcholinski.pl.workerhelper.projectlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding4.appcompat.queryTextChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import michal.warcholinski.pl.workerhelper.databinding.FragmentProjectListBinding
import michal.warcholinski.pl.workerhelper.extension.addTo


/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
@AndroidEntryPoint
class ProjectListFragment : Fragment() {
	
	private var _binding: FragmentProjectListBinding? = null
	private val binding get() = _binding!!
	
	private val disposableBag = CompositeDisposable()
	private val viewModel: ProjectListViewModel by viewModels()
	private val projectListAdapter = ProjectListAdapter()
	private val realized
		get() = arguments?.getBoolean(REALIZED, false) ?: false
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		viewModel.getAllProjects(realized)
	}
	
	override fun onCreateView(
			inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
	): View {
		_binding = FragmentProjectListBinding.inflate(inflater, container, false)
		return binding.root
	}
	
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		
		with(binding.projectsRecyclerView) {
			adapter = projectListAdapter
			layoutManager = LinearLayoutManager(context)
		}
		
		binding.projectSearch.queryTextChanges()
				.skipInitialValue()
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({
					projectListAdapter.filter(it.toString())
				}, {
					projectListAdapter.filter("")
				}).addTo(disposableBag)
		
		viewModel.projectListViewState.observe(viewLifecycleOwner) { viewState ->
			projectListAdapter.update(viewState.projects, binding.projectSearch.query.toString())
		}
	}
	
	override fun onDestroyView() {
		super.onDestroyView()
		disposableBag.clear()
		_binding = null
	}
	
	companion object {
		
		private const val REALIZED = "REALIZED"
		
		fun getInstance(realized: Boolean) = ProjectListFragment().apply {
			arguments = Bundle().apply { putBoolean(REALIZED, realized) }
		}
	}
}