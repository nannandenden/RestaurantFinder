package com.example.restaurantfinder.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class BasicSpaceItemDecoration(
    private val paddingStart: Int = 32,
    private val paddingTop: Int = 16,
    private val paddingEnd: Int = 32,
    private val paddingBottom: Int = 16): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }
}