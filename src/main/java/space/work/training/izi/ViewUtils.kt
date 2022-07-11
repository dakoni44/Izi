package space.work.training.izi

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.Transformation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout


class ViewUtils {
    companion object {

        fun expandOrCollapseDownUp(v: View, exp_or_colpse: String) {
            var anim: TranslateAnimation? = null
            if (exp_or_colpse == "expand") {
                anim = TranslateAnimation(0.0f, 0.0f, v.height.toFloat(), 0.0f)
                v.visibility = View.VISIBLE
            } else {
                anim = TranslateAnimation(0.0f, 0.0f, 0.0f, (v.height).toFloat())
                val collapselistener: AnimationListener = object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        v.visibility = View.GONE
                    }
                }
                anim.setAnimationListener(collapselistener)
            }

            // To Collapse
            //
            anim.duration = 350
            anim.interpolator = AccelerateInterpolator(0.5f)
            v.startAnimation(anim)
        }

        fun expandOrCollapseUpDown(v: View, exp_or_colpse: String) {
            var anim: TranslateAnimation? = null
            if (exp_or_colpse == "expand") {
                anim = TranslateAnimation(0.0f, 0.0f, 0.0f, v.height.toFloat())
                v.visibility = View.VISIBLE
            } else {
                anim = TranslateAnimation(0.0f, 0.0f, (v.height).toFloat(), 0.0f)
                val collapselistener: AnimationListener = object : AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationRepeat(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        v.visibility = View.GONE
                    }
                }
                anim.setAnimationListener(collapselistener)
            }

            // To Collapse
            //
            anim.duration = 350
            anim.interpolator = AccelerateInterpolator(0.5f)
            v.startAnimation(anim)
        }

        fun expand(v: View) {
            if (v.visibility == View.VISIBLE) return
            val durations: Long
            val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                (v.parent as View).width,
                View.MeasureSpec.EXACTLY
            )
            val wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                0,
                View.MeasureSpec.UNSPECIFIED
            )
            v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = v.measuredHeight

            // Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            durations = ((targetHeight / v.context.resources
                .displayMetrics.density * 2.5)).toLong()

            v.alpha = 0.0F
            v.visibility = View.VISIBLE
            v.animate().alpha(1.0F).setDuration(durations).setListener(null)

            val a: Animation = object : Animation() {
                override fun applyTransformation(
                    interpolatedTime: Float,
                    t: Transformation
                ) {
                    v.layoutParams.height =
                        if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            // Expansion speed of 1dp/ms
            a.duration = durations
            v.startAnimation(a)
        }

        fun collapse(v: View) {
            if (v.visibility == View.GONE) return
            val durations: Long
            val initialHeight = v.measuredHeight
            val a: Animation = object : Animation() {
                override fun applyTransformation(
                    interpolatedTime: Float,
                    t: Transformation
                ) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            durations = (initialHeight / v.context.resources
                .displayMetrics.density * 2.5).toLong()

            v.alpha = 1.0F
            v.animate().alpha(0.0F).setDuration(durations)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        v.visibility = View.GONE
                        v.alpha = 1.0F
                    }
                })

            // Collapse speed of 1dp/ms
            a.duration = durations
            v.startAnimation(a)
        }
    }
}