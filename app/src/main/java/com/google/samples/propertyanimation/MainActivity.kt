/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.propertyanimation

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView


class MainActivity : AppCompatActivity() {

    /**  we’ve created lateinit variables to hold the views that we will refer to in the code */
    lateinit var star: ImageView
    lateinit var rotateButton: Button
    lateinit var translateButton: Button
    lateinit var scaleButton: Button
    lateinit var fadeButton: Button
    lateinit var colorizeButton: Button
    lateinit var showerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** these variables are initialized to appropriate values in onCreate() */
        star = findViewById(R.id.star)
        rotateButton = findViewById<Button>(R.id.rotateButton)
        translateButton = findViewById<Button>(R.id.translateButton)
        scaleButton = findViewById<Button>(R.id.scaleButton)
        fadeButton = findViewById<Button>(R.id.fadeButton)
        colorizeButton = findViewById<Button>(R.id.colorizeButton)
        showerButton = findViewById<Button>(R.id.showerButton)

        rotateButton.setOnClickListener {
            rotater()
        }

        translateButton.setOnClickListener {
            translated()
        }

        scaleButton.setOnClickListener {
            scalar()
        }

        fadeButton.setOnClickListener {
            fader()
        }

        colorizeButton.setOnClickListener {
            colorized()
        }

        showerButton.setOnClickListener {
            shower()
        }
    }

    /** Change the disableViewDuringAnimation() function to be an extension function on ObjectAnimator. This makes the
     *  function more concise to call, since it eliminates a parameter. It also makes the code a little more natural
     *  to read, by putting the animator-related functionality directly onto ObjectAnimator
     *  */
    private fun ObjectAnimator.disableViewDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }

    /** Next, you’ll see five methods that will be called by listeners to perform the functionality for the various
     * buttons (rotater(), translater(), etc.)
     * */
    private fun rotater() {
        /** create an animation that rotates the ImageView containing the star from a value of -360 to 0. This means
         * that the view, and thus the star inside it, will rotate in a full circle (360 degrees) around its center */
        val animator = ObjectAnimator.ofFloat(star, View.ROTATION, -360f, 0f)

        /** Change the duration property of the animator to 1000 milliseconds - default is 300 milliseconds */
        animator.duration = 1000

        /** we’d like to disable the ROTATE button as soon as the animation starts, and then re-enable
         * it when the animation ends. rotateButton is disabled as soon as the animation starts and re-enabled when
         * the animation ends. This way, each animation is completely separate from any other rotation animation,
         * avoiding the junk of restarting in the middle.
         * */
        animator.disableViewDuringAnimation(rotateButton)

        /** run the animation */
        animator.start()
    }

    private fun translated() {

        /** create an animation that moves the star to the right by 200 pixels */
        val animator = ObjectAnimator.ofFloat(star, View.TRANSLATION_X, 200f)

        /** You will change the animation to repeat, playing in reverse back to its starting position */
        // controls how many times it repeats after the first run)
        animator.repeatCount = 1
        // REVERSE or RESTART for repeating again from/to the same values)
        animator.repeatMode = ObjectAnimator.REVERSE
        // call the extension function
        animator.disableViewDuringAnimation(translateButton)

        /** run the animation */
        animator.start()
    }

    /** But you can instead use an intermediate object called PropertyValuesHolder to hold this information,
     * and then create a single ObjectAnimator with multiple PropertyValuesHolder objects. This single
     * animator will then run an animation on two or more of these sets of properties/values together.
     * */
    private fun scalar() {

        // create two PropertyValuesHolder objects, Scaling to a value of 4f means the star will scale to 4 times its default size.
        // start value will be assigned according to its current value and the target object when the animation begins (1x/1y to 4x/4y)
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 4f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 4f)
        // create an ObjectAnimator object, as before, but use the scaleX and scaleY objects you created above to specify the property/value information
        val animator = ObjectAnimator.ofPropertyValuesHolder(
            star, scaleX, scaleY)
        /** As with the translater() function, you want to make this a repeating/reversing animation to leave
         * the star’s SCALE_X and SCALE_Y properties at their default values (1.0) when the animation is done.
         * Do this by setting the appropriate repeatCount and repeatMode values on the animator */
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        // extension function to disable scaleButton during the animation
        animator.disableViewDuringAnimation(scaleButton)
        // run the animation
        animator.start()
    }


    /** Define the fader() function to fade out the view to 0 and then back to its starting value. This code is
     * essentially equivalent to the translater() function code you wrote before, except with a different property
     * and end value. */
    private fun fader() {
        val animator = ObjectAnimator.ofFloat(star, View.ALPHA, 0f)
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(fadeButton)
        animator.start()
    }

    /** One of the powerful things about ObjectAnimator, which might not be obvious from the examples so far, is that
     *  it can animate anything, as long as there is a property that the animator can access. In this animation, you
     *  will change the color of the star field background from black to red (and back). */
    @SuppressLint("ObjectAnimatorBinding")
    private fun colorized() {

        /** First, you will need an ObjectAnimator that can act on the appropriate type. You could use the
         * ObjectAnimator.ofInt() factory method, since View.setBackgroundColor(int) takes an int, but… that
         * would give us unexpected results. In the colorizer() function, create and run such an animator to
         * see the problem:
         * var animator = ObjectAnimator.ofInt(star.parent,"backgroundColor", Color.BLACK, Color.RED).start()
         *
         * What you need, instead, is an animator that knows how to interpret (and animate between) color values,
         * rather than simply the integers that represent those colors. Use a different factory method for the
         * animator, ObjectAnimator.ofArgb() */
        val animator = ObjectAnimator.ofArgb(
            star.parent,
            "backgroundColor",
            Color.BLACK,
            Color.RED
        )
        animator.duration = 500
        animator.repeatCount = 1
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.disableViewDuringAnimation(colorizeButton)
        animator.start()
    }


    private fun shower() {

        /** First, you’re going to need some local variables to hold state that we will need in the ensuing code. */

        // a reference to the star field ViewGroup (which is just the parent of the current star view).
        val container = star.parent as ViewGroup
        // the width and height of that container (which you will use to calculate the end translation values for our falling stars).
        val containerW = container.width
        val containerH = container.height
        // he default width and height of our star (which you will later alter with a scale factor to get different-sized stars).
        var starW: Float = star.width.toFloat()
        var starH: Float = star.height.toFloat()

        /** Create a new View to hold the star graphic (View containing a Drawable). Because the star is a VectorDrawable asset,
         * use an AppCompatImageView, which has the ability to host that kind of resource. Create the star and add it to the
         * background container. */
        val newStar = AppCompatImageView(this)
        newStar.setImageResource(R.drawable.ic_star)
        newStar.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)
        container.addView(newStar)
        /** Set the size of the star. Modify the star to have a random size, from .1x to 1.6x of its default size. Use this
         * scale factor to change the cached width/height values, because you will need to know the actual pixel height/width
         * for later calculations */
        newStar.scaleX = Math.random().toFloat() * 1.5f + .1f
        newStar.scaleY = newStar.scaleX
        starW *= newStar.scaleX
        starH *= newStar.scaleY

        /** Now position the new star. Horizontally, it should appear randomly somewhere from the left edge to the right edge.
         *  This code uses the width of the star to position it from half-way off the screen on the left (-starW / 2) to
         *  half-way off the screen on the right (with the star positioned at (containerW - starW / 2). The vertical positioning
         *  of the star will be handled later in the actual animation code. */
        newStar.translationX = Math.random().toFloat() *
                containerW - starW / 2

        /** the rotation will use a smooth linear motion (moving at a constant rate over the entire rotation animation), while
         * the falling animation will use an accelerating motion (simulating gravity pulling the star downward at a constantly
         * faster rate). So you'll create two animators and add an interpolator to each.. */
        // It animates the TRANSLATION_Y property, similar to what you did with TRANSLATION_X in the earlier translation task,
        // but causing vertical instead of horizontal motion. The code animates from -starH to (containerH + starH),
        // which effectively places it just off the container at the top and moves it until it’s just outside the container at the bottom
        val mover = ObjectAnimator.ofFloat(newStar, View.TRANSLATION_Y,
            -starH, containerH + starH)
        // The AccelerateInterpolator “interpolator” that we are setting on the star causes a gentle acceleration motion.
        mover.interpolator = AccelerateInterpolator(1f)
        // For the rotation animation, the star will rotate a random amount between 0 and 1080 degrees.
        val rotator = ObjectAnimator.ofFloat(newStar, View.ROTATION,
            (Math.random() * 1080).toFloat())
        // For the motion, we are simply using a LinearInterpolator, so the rotation will proceed at a constant rate as the star falls.
        rotator.interpolator = LinearInterpolator()

        // Create the AnimatorSet and add the child animators to it (along with information to play them in parallel)
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        // The default animation time of 300 milliseconds is too quick to enjoy the falling stars, so set the duration to
        // a random number between 500 and 2000 milliseconds, so stars fall at different speeds
        set.duration = (Math.random() * 1500 + 500).toLong()

        // Once newStar has fallen off the bottom of the screen, it should be removed from the container. Set a simple listener
        // to wait for the end of the animation and remove it. Then start the animation.
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                container.removeView(newStar)
            }
        })
        set.start()
    }

}
