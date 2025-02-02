package org.koitharu.kotatsu.details.ui.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.koitharu.kotatsu.base.ui.list.decor.AbstractSelectionItemDecoration
import org.koitharu.kotatsu.utils.ext.getThemeColor
import com.google.android.material.R as materialR

class ChaptersSelectionDecoration(context: Context) : AbstractSelectionItemDecoration() {

	private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
	private val radius = context.resources.getDimension(materialR.dimen.abc_control_corner_material)

	init {
		paint.color = context.getThemeColor(materialR.attr.colorSecondaryContainer, Color.LTGRAY)
		paint.style = Paint.Style.FILL
	}

	override fun onDrawBackground(
		canvas: Canvas,
		parent: RecyclerView,
		child: View,
		bounds: RectF,
		state: RecyclerView.State,
	) {
		canvas.drawRoundRect(bounds, radius, radius, paint)
	}
}