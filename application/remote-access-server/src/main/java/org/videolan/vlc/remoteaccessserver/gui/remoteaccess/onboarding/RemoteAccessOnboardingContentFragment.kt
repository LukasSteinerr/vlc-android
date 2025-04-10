/*
 * ************************************************************************
 *  RemoteAccessOnboardingContentFragment.kt
 * *************************************************************************
 * Copyright © 2025 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * **************************************************************************
 *
 *
 */

package org.videolan.vlc.remoteaccessserver.gui.remoteaccess.onboarding

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.videolan.vlc.gui.view.MiniVisualizer
import org.videolan.vlc.remoteaccessserver.R


class RemoteAccessOnboardingContentFragment : RemoteAccessOnboardingFragment() {
    private lateinit var titleView: TextView
    private lateinit var vizu: MiniVisualizer
    private lateinit var filesLL: LinearLayout
    private lateinit var playbackLL: LinearLayout
    private lateinit var medialibraryLL: LinearLayout

    private val animationLoop = AnimatorSet()

    override fun getDefaultViewForTalkback() = titleView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.remote_access_onboarding_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView = view.findViewById(R.id.welcome_title)
        filesLL = view.findViewById(R.id.files)
        playbackLL = view.findViewById(R.id.playback)
        medialibraryLL = view.findViewById(R.id.medialibrary)
        vizu = view.findViewById(R.id.vizu)

        vizu.start()


        val appearingSets = ArrayList<AnimatorSet>()
        arrayOf(medialibraryLL, filesLL, playbackLL).forEach {
            val bumpAnimationX = ObjectAnimator.ofFloat(it, View.SCALE_X, 1F, 1.2F, 1F)
            bumpAnimationX.interpolator = OvershootInterpolator()
            bumpAnimationX.duration = 1500

            val bumpAnimationY = ObjectAnimator.ofFloat(it, View.SCALE_Y, 1F, 1.2F, 1F)
            bumpAnimationY.interpolator = OvershootInterpolator()
            bumpAnimationY.duration = 1500

            val set = AnimatorSet()
            set.playTogether(bumpAnimationX, bumpAnimationY)
            appearingSets.add(set)
        }

        animationLoop.playSequentially(appearingSets.toMutableList() as List<Animator>?)
        animationLoop.doOnEnd {
            lifecycleScope.launch {
                try {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        delay(2000)
                        animationLoop.start()
                    }
                } catch (_: IllegalStateException) {
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        animationLoop.start()
    }

    override fun onPause() {
        super.onPause()
        vizu.stop()
        animationLoop.cancel()
    }

    companion object {
        fun newInstance(): RemoteAccessOnboardingContentFragment {
            return RemoteAccessOnboardingContentFragment()
        }
    }
}