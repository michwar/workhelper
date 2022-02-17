package michal.warcholinski.pl.workerhelper.addrequest.localappfiles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import michal.warcholinski.pl.workerhelper.R

/**
 * Created by Michał Warcholiński on 2022-01-06.
 */
class SwipeLeftToDeleteCallback(private val onFileDeleted: (Int) -> Unit)
	: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

	private val background: ColorDrawable = ColorDrawable(Color.RED)

	override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
		onFileDeleted(viewHolder.adapterPosition)
	}

	override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
	                         dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

		val itemView = viewHolder.itemView
		ResourcesCompat.getDrawable(itemView.context.resources, R.drawable.ic_delete, null)
		val backgroundCornerOffset = 20

		when {
			dX < 0 -> { // Swiping to the left
				background.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset,
					itemView.top, itemView.right, itemView.bottom)
			}
			else -> { // view is unSwiped
				background.setBounds(0, 0, 0, 0)
			}
		}

		val paint = Paint().apply {
			textSize = 40F
			color = Color.WHITE
			textAlign = Paint.Align.CENTER
		}

		background.draw(c)
		c.drawText(itemView.context.getString(R.string.action_delete),
			itemView.right.toFloat() - 200,
			(itemView.top + itemView.height / 2).toFloat(),
			paint)
	}

	override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
		return false
	}
}