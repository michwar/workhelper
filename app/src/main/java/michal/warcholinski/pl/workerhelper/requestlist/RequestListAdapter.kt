package michal.warcholinski.pl.workerhelper.requestlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import michal.warcholinski.pl.domain.requests.model.RequestDataModel
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.RequestItemViewBinding
import michal.warcholinski.pl.workerhelper.extension.formatDate
import michal.warcholinski.pl.workerhelper.projectdetail.ProjectDetailsFragmentDirections

/**
 * Created by Michał Warcholiński on 2021-12-21.
 */
class RequestListAdapter : RecyclerView.Adapter<RequestListAdapter.RequestViewHolder>() {

	private val requests = arrayListOf<RequestDataModel>()
	private val filteredRequests = arrayListOf<RequestDataModel>()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
		val binding =
			RequestItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return RequestViewHolder(binding)
	}

	override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
		val request = filteredRequests[position]
		with(holder) {
			name.text = request.name
			dateOfAdd.text = request.dateOfCreate.formatDate()
			desc.text = request.desc
			icon.transitionName = request.id.toString() + "THUMBNAIL"

			Glide.with(holder.itemView.context)
				.load(request.photoPath)
				.centerCrop()
				.override(300, 500)
				.placeholder(R.drawable.ic_image)
				.into(holder.icon)
		}
	}

	override fun getItemId(position: Int): Long = filteredRequests[position].id

	override fun getItemCount(): Int = filteredRequests.size

	fun update(newRequests: List<RequestDataModel>, search: String) {
		requests.clear()
		requests.addAll(newRequests)

		filter(search)
	}

	fun filter(search: String) {
		val newFilteredProjects =
			if (search.isEmpty())
				requests
			else
				requests.filter { it.name.contains(search, true) || it.desc.contains(search, true) }

		updateList(newFilteredProjects)

		filteredRequests.clear()
		filteredRequests.addAll(newFilteredProjects)
	}

	private fun updateList(newRequests: List<RequestDataModel>) {
		DiffUtil.calculateDiff(object : DiffUtil.Callback() {

			override fun getOldListSize(): Int = filteredRequests.size

			override fun getNewListSize(): Int = newRequests.size

			override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				val oldRequest = filteredRequests[oldItemPosition]
				val newRequest = newRequests[newItemPosition]

				return oldRequest.id == newRequest.id
			}

			override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				val oldRequest = filteredRequests[oldItemPosition]
				val newRequest = newRequests[newItemPosition]

				return oldRequest.name == newRequest.name &&
						oldRequest.desc == newRequest.desc &&
						oldRequest.photoPath == newRequest.photoPath
			}
		}).dispatchUpdatesTo(this)
	}

	inner class RequestViewHolder(binding: RequestItemViewBinding) :
		RecyclerView.ViewHolder(binding.root) {

		val name = binding.requestName
		val dateOfAdd = binding.dateOfAdd
		val desc = binding.requestDesc
		val icon = binding.requestIcon

		init {
			binding.root.setOnClickListener { view ->
				val request = requests[adapterPosition]

				val extras = FragmentNavigatorExtras(
					binding.requestIcon to request.id.toString() + "THUMBNAIL"
				)

				view.findNavController()
					.navigate(ProjectDetailsFragmentDirections.toRequestDetailsFragment(
						requestId = request.id,
						imageTransitionName = request.id.toString() + "THUMBNAIL",
						projectId = request.projectId
					), extras)
			}
		}
	}
}