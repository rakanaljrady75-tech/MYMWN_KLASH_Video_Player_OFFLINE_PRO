package com.mymwn.klash

import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.mymwn.klash.databinding.ItemReelBinding

class ReelsAdapter(private val context: android.content.Context, private val items: List<VideoItem>) :
    RecyclerView.Adapter<ReelsAdapter.Holder>() {

    class Holder(val b: ItemReelBinding) : RecyclerView.ViewHolder(b.root) {
        var player: ExoPlayer? = null
        var locked = false
        var speedIndex = 1
        val speeds = floatArrayOf(0.5f, 1f, 1.25f, 1.5f, 2f)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = items[position]
        holder.b.title.text = item.name
        holder.b.likeButton.text = if (Prefs.isLiked(context, item.uri)) "♥" else "♡"

        holder.player = ExoPlayer.Builder(context).build().also { p ->
            holder.b.player.player = p
            p.setMediaItem(MediaItem.fromUri(item.uri))
            p.prepare()
            p.playWhenReady = true
            p.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }

        val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent) = true
            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (!holder.locked) toggleLike(holder, item, true)
                return true
            }
        })

        holder.b.player.setOnTouchListener { _, event ->
            if (holder.locked) return@setOnTouchListener true
            detector.onTouchEvent(event)
            false
        }

        holder.b.likeButton.setOnClickListener {
            animateButton(holder.b.likeButton)
            toggleLike(holder, item, false)
        }

        holder.b.speedButton.setOnClickListener {
            animateButton(holder.b.speedButton)
            holder.speedIndex = (holder.speedIndex + 1) % holder.speeds.size
            val speed = holder.speeds[holder.speedIndex]
            holder.player?.playbackParameters = PlaybackParameters(speed)
            holder.b.speedButton.text = "${speed}x"
        }

        holder.b.muteButton.setOnClickListener {
            animateButton(holder.b.muteButton)
            val p = holder.player ?: return@setOnClickListener
            p.volume = if (p.volume > 0f) 0f else 1f
            holder.b.muteButton.text = if (p.volume > 0f) "🔊" else "🔇"
        }

        holder.b.lockButton.setOnClickListener {
            animateButton(holder.b.lockButton)
            holder.locked = !holder.locked
            holder.b.lockButton.text = if (holder.locked) "🔓" else "🔒"
            Toast.makeText(context, if (holder.locked) "تم قفل اللمس" else "تم فتح اللمس", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleLike(holder: Holder, item: VideoItem, fromDoubleTap: Boolean) {
        val newState = !Prefs.isLiked(context, item.uri)
        Prefs.setLiked(context, item.uri, newState)
        holder.b.likeButton.text = if (newState) "♥" else "♡"
        if (newState && fromDoubleTap) {
            holder.b.heartOverlay.visibility = View.VISIBLE
            holder.b.heartOverlay.startAnimation(
                android.view.animation.AnimationUtils.loadAnimation(context, R.anim.heart_pop)
            )
            holder.b.heartOverlay.postDelayed({ holder.b.heartOverlay.visibility = View.GONE }, 450)
        }
    }

    private fun animateButton(v: View) {
        v.startAnimation(android.view.animation.AnimationUtils.loadAnimation(context, R.anim.button_press))
    }

    override fun onViewRecycled(holder: Holder) {
        holder.player?.release()
        holder.b.player.player = null
        holder.player = null
        super.onViewRecycled(holder)
    }

    override fun getItemCount() = items.size
}
