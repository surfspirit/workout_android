package ru.workout24.view

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.RelativeLayout
import ru.workout24.R
import kotlinx.android.synthetic.main.spinner_layout.view.*


class SpinnerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    RelativeLayout(context, attrs, defStyleAttr) {


    var isOpened = false
    init {
        initSpinner(attrs)
    }

    private fun initSpinner(attrs: AttributeSet?) {
    }


    init {
        val inflater = context
            .getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.spinner_layout, this, true)
        spinner1.setPopupBackgroundDrawable(null);

        spinner1.setOnItemSelectedEvenIfUnchangedListener(object : OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                System.out.println("NOTHING setOnItemSelectedEvenIfUnchangedListener")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                System.out.println("Selected $p2 setOnItemSelectedEvenIfUnchangedListener")
                isOpened = false
               // rotateReverse()

            }

        })
        spinner1.setOnTouchListener { _, _ ->
            if (!isOpened) {
                isOpened = true
              //  rotate()
            }

            false;
        }

    }

    private fun rotate() {
        val anim = RotateAnimation(0.0f, 90.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = 0
        anim.duration = 200
        anim.fillAfter = true
        iv_spinner_image.startAnimation(anim)
    }
    private fun rotateReverse() {
        val anim = RotateAnimation(90.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.interpolator = LinearInterpolator()
        anim.repeatCount = 0
        anim.duration = 200
        anim.fillAfter = true
        iv_spinner_image.startAnimation(anim)
    }

}