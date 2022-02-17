package michal.warcholinski.pl.workerhelper.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import michal.warcholinski.pl.workerhelper.R
import michal.warcholinski.pl.workerhelper.databinding.ValueLabelActionLayoutBinding

/**
 * Created by Michał Warcholiński on 2022-01-10.
 */
class ValueLabelActionView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ConstraintLayout(context, attrs, defStyleAttr) {

	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	constructor(context: Context) : this(context, null, 0)

	private var _binding: ValueLabelActionLayoutBinding? = null
	private val binding
		get() = _binding!!

	var label = ""
		set(value) {
			field = value
			binding.label.text = value
			invalidate()
		}

	var value = ""
		set(value) {
			field = value
			binding.value.text = value
			invalidate()
		}

	var icon: Drawable? = null
		set(value) {
			field = value
			value?.let {
				binding.icon.setImageDrawable(value)
			}
			invalidate()
		}

	init {
		_binding = ValueLabelActionLayoutBinding.inflate(LayoutInflater.from(context), this)

		val set = ConstraintSet()
		set.clone(this)

		set.connect(binding.label.id, ConstraintSet.TOP, this.id, ConstraintSet.TOP)
		set.connect(binding.value.id, ConstraintSet.TOP, binding.label.id, ConstraintSet.BOTTOM)
		set.connect(binding.value.id, ConstraintSet.BOTTOM, this.id, ConstraintSet.BOTTOM)
		set.connect(binding.value.id, ConstraintSet.START, binding.icon.id, ConstraintSet.END)
		set.connect(binding.icon.id, ConstraintSet.TOP, binding.label.id, ConstraintSet.BOTTOM)
		set.connect(binding.icon.id, ConstraintSet.START, this.id, ConstraintSet.START)

		setConstraintSet(set)

		context.obtainStyledAttributes(attrs, R.styleable.ValueLabelActionView).let { typedArray ->
			label =
				typedArray.getString(R.styleable.ValueLabelActionView_valueLabelActionViewLabelText)
					.orEmpty()
			value =
				typedArray.getString(R.styleable.ValueLabelActionView_valueLabelActionViewValueText)
					.orEmpty()
			icon =
				typedArray.getDrawable(R.styleable.ValueLabelActionView_valueLabelActionViewIcon)

			typedArray.recycle()
		}
	}
}