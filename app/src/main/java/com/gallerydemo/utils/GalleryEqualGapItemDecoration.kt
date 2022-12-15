package com.gallerydemo.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class GalleryEqualGapItemDecoration(private val spanCount: Int, private val spacing: Int) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.let { adapter ->
            val layoutParams = view.layoutParams as GridLayoutManager.LayoutParams
            val spanIndex = layoutParams.spanIndex
            val layoutPosition = layoutParams.viewLayoutPosition
            val itemCount = adapter.itemCount
            val leftEdge = spanIndex == 0
            val rightEdge = spanIndex == spanCount - 1
            val topEdge = spanIndex < spanCount
            val bottomEdge = layoutPosition >= itemCount - spanCount
            val halfSpacing = spacing / 2
            outRect[if (leftEdge) spacing * 2 else if (rightEdge) 0 else spacing, if (topEdge) spacing else halfSpacing, if (rightEdge) spacing * 2 else if (leftEdge) 0 else spacing] =
                if (bottomEdge) spacing else 0
        }
    }
}