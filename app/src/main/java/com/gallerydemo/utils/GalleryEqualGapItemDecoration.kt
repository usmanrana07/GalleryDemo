package com.gallerydemo.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class GalleryEqualGapItemDecoration(private var spanCount: Int, private val spacing: Int) :
    RecyclerView.ItemDecoration() {
    private val startEndExtraSpacing: Int = spacing
    private val startEndParentSpace: Int = spacing + startEndExtraSpacing
    private val extraSpacingEqualBurdenToCenter: Int by lazy {
        if (startEndExtraSpacing % 2 == 0) (startEndExtraSpacing / 2) else (startEndExtraSpacing / 2f).roundToInt() + 1
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (view.layoutParams is GridLayoutManager.LayoutParams) {
            val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
            if (spanCount > 2)
                setRecForMoreThanTwoSpans(outRect, layoutParams, parent)
            else
                setRecForTwoSpans(outRect, layoutParams, parent)
        }
    }

    private fun setRecForTwoSpans(
        outRect: Rect,
        layoutParams: GridLayoutManager.LayoutParams,
        parent: RecyclerView
    ) {
        val spanIndex = layoutParams.spanIndex
        val layoutPosition = layoutParams.viewLayoutPosition
        val itemCount = parent.adapter?.itemCount ?: 0
        val halfSpacing = spacing / 2

        val topSpacing = if (spanIndex < spanCount  /*topEdge*/) spacing else halfSpacing
        val bottomSpacing =
            if (layoutPosition >= itemCount - spanCount  /*bottomEdge*/) spacing else 0
        when (spanIndex) {
            0 -> {
                outRect.set(startEndParentSpace, topSpacing, halfSpacing, bottomSpacing)
            }
            else -> {
                outRect.set(halfSpacing, topSpacing, startEndParentSpace, bottomSpacing)
            }
        }

    }

    private fun setRecForMoreThanTwoSpans(
        outRect: Rect,
        layoutParams: GridLayoutManager.LayoutParams,
        parent: RecyclerView
    ) {
        val spanIndex = layoutParams.spanIndex
        val layoutPosition = layoutParams.viewLayoutPosition
        val itemCount = parent.adapter?.itemCount ?: 0
        val halfSpacing = spacing / 2

        val topSpacing = if (spanIndex < spanCount  /*topEdge*/) spacing else halfSpacing
        val bottomSpacing =
            if (layoutPosition >= itemCount - spanCount  /*bottomEdge*/) spacing else 0
        when (spanIndex) {
            0 -> {
                outRect.set(
                    startEndParentSpace,
                    topSpacing,
                    halfSpacing - startEndExtraSpacing,
                    bottomSpacing
                )
            }
            spanCount - 1 -> {
                outRect.set(
                    halfSpacing - startEndExtraSpacing,
                    topSpacing,
                    startEndParentSpace,
                    bottomSpacing
                )
            }
            else -> {
                // add neighbouring burden to 2nd and 2nd last column from 1st and last item spacing as those contain more spacing than others which may reduce their size that others otherwise
                outRect.set(
                    if (spanIndex == 1) halfSpacing + extraSpacingEqualBurdenToCenter else halfSpacing,
                    topSpacing,
                    if (spanIndex == spanCount - 2) halfSpacing + extraSpacingEqualBurdenToCenter else halfSpacing,
                    bottomSpacing
                )
            }
        }
    }
}