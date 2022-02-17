package michal.warcholinski.pl.workerhelper.addrequest.localappfiles

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import michal.warcholinski.pl.domain.localfiles.model.FileDataModel
import michal.warcholinski.pl.workerhelper.databinding.LocalAppFileItemViewBinding

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
class LocalAppFilesAdapter(private val onFileSelected: (FileDataModel) -> Unit) : RecyclerView.Adapter<LocalAppFilesAdapter.LocalAppFileHolder>() {

	private val files = arrayListOf<FileDataModel>()

	override fun getItemCount(): Int = files.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalAppFileHolder {
		val binding =
			LocalAppFileItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return LocalAppFileHolder(binding)
	}

	override fun onBindViewHolder(holder: LocalAppFileHolder, position: Int) {
		val file = files[position]
		holder.fileName.text = file.name
		Glide
			.with(holder.itemView.context)
			.load(file.path)
			.into(holder.filePreview)
	}

	fun update(newFiles: List<FileDataModel>) {
		DiffUtil.calculateDiff(object : DiffUtil.Callback() {

			override fun getOldListSize(): Int = files.size

			override fun getNewListSize(): Int = newFiles.size

			override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				return files[oldItemPosition].name == newFiles[newItemPosition].name
			}

			override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
				return files[oldItemPosition].path == newFiles[newItemPosition].path
			}
		}).dispatchUpdatesTo(this)

		files.clear()
		files.addAll(newFiles)
	}

	fun delete(deletedPosition: Int) {
		files.removeAt(deletedPosition)
		notifyItemRemoved(deletedPosition)
	}

	fun getFileByPosition(position: Int): FileDataModel {
		return files[position]
	}

	inner class LocalAppFileHolder(binding: LocalAppFileItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
		val fileName = binding.fileName
		val filePreview = binding.filePreview

		init {
			binding.root.setOnClickListener {
				val filePath = files[adapterPosition]
				onFileSelected(filePath)
			}
		}
	}
}