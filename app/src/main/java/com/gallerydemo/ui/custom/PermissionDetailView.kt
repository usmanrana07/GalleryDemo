package com.gallerydemo.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.StringRes
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.gallerydemo.R
import com.gallerydemo.databinding.LayoutPermissionDetailViewBinding
import com.gallerydemo.utils.OnActionCallback

@BindingMethods(
    BindingMethod(
        type = PermissionDetailView::class,
        attribute = "onAllowClick",
        method = "setOnAllowClickCallback"
    )
)
class PermissionDetailView : FrameLayout {
    private var itemViewBinding: LayoutPermissionDetailViewBinding? = null
    var title: String?
        get() = itemViewBinding?.tvTitle?.text?.toString()
        set(value) {
            itemViewBinding?.tvTitle?.text = value
        }
    var description: String?
        get() = itemViewBinding?.tvDescription?.text?.toString()
        set(value) {
            itemViewBinding?.tvDescription?.text = value
        }
    private var onAllowActionCallback: OnActionCallback? = null

    constructor(context: Context) : super(context) {
        initializeView(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeView(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initializeView(context, attrs, defStyleAttr)
    }

    private fun initializeView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

        itemViewBinding =
            LayoutPermissionDetailViewBinding.inflate(LayoutInflater.from(context), this, true)
                .also {
                    it.btnAllow.setOnClickListener { onAllowActionCallback?.invoke() }
                }

        val a =
            context.obtainStyledAttributes(attrs, R.styleable.PermissionDetailView, defStyleAttr, 0)
        try {
            val n = a.indexCount
            for (i in 0 until n) {
                when (val attr = a.getIndex(i)) {
                    R.styleable.PermissionDetailView_title -> title = a.getString(attr)
                    R.styleable.PermissionDetailView_android_text -> description = a.getString(attr)
                }
            }
        } finally {
            a.recycle()
        }
    }

    fun setOnAllowClickCallback(listener: OnActionCallback) {
        this.onAllowActionCallback = listener
    }

    fun setTitle(@StringRes resId: Int) {
        itemViewBinding?.tvTitle?.setText(resId)
    }

    fun setDescription(@StringRes resId: Int) {
        itemViewBinding?.tvDescription?.setText(resId)
    }

}