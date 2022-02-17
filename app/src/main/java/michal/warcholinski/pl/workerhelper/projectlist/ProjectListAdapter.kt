package michal.warcholinski.pl.workerhelper.projectlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import michal.warcholinski.pl.domain.project.model.ProjectDataModel
import michal.warcholinski.pl.workerhelper.databinding.ProjectItemViewBinding
import michal.warcholinski.pl.workerhelper.extension.formatDate

/**
 * Created by Michał Warcholiński on 2022-01-08.
 */
class ProjectListAdapter : RecyclerView.Adapter<ProjectListAdapter.ProjectViewHolder>() {

	private val projects: ArrayList<ProjectDataModel> = arrayListOf()
	private val filteredProjects = arrayListOf<ProjectDataModel>()

	override fun getItemCount(): Int = filteredProjects.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
		val binding =
			ProjectItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ProjectViewHolder(binding)
	}

	override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
		val project = filteredProjects[position]
		with(holder) {
			name.text = project.name
			desc.text = project.desc
			createDate.text = project.createDate.formatDate()
		}
	}

	fun update(newProjects: List<ProjectDataModel>, search: String) {
		projects.clear()
		projects.addAll(newProjects)

		filter(search)
	}

	fun filter(search: String) {
		val newFilteredProjects =
			if (search.isEmpty())
				projects
			else
				projects.filter { it.name.contains(search, true) || it.desc.contains(search, true) }

		updateList(newFilteredProjects)

		filteredProjects.clear()
		filteredProjects.addAll(newFilteredProjects)
	}

	private fun updateList(newProjects: List<ProjectDataModel>) {
		DiffUtil.calculateDiff(object : DiffUtil.Callback() {
			override fun getOldListSize(): Int = filteredProjects.size

			override fun getNewListSize(): Int = newProjects.size

			override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				return filteredProjects[oldItemPosition].id == newProjects[newItemPosition].id
			}

			override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				val oldProject = filteredProjects[oldItemPosition]
				val newProject = projects[newItemPosition]

				return oldProject.name == newProject.name &&
						oldProject.desc == newProject.desc &&
						oldProject.createDate == newProject.createDate
			}
		}).dispatchUpdatesTo(this)
	}

	inner class ProjectViewHolder(binding: ProjectItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
		val name = binding.name
		val desc = binding.desc
		val createDate = binding.createDate

		init {
			binding.root.setOnClickListener { view ->
				val project = projects[adapterPosition]
				view.findNavController()
					.navigate(ProjectsPagerFragmentDirections.toProjectDetailsFragment(project.id, project.realized, project.name))
			}
		}
	}
}