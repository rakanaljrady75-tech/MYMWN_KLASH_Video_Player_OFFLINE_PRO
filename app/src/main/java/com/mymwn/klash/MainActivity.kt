package com.mymwn.klash

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mymwn.klash.databinding.ActivityMainBinding
import com.mymwn.klash.databinding.ScreenAboutBinding
import com.mymwn.klash.databinding.ScreenHomeBinding
import com.mymwn.klash.databinding.ScreenListBinding
import com.mymwn.klash.databinding.ScreenReelsBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val videos = mutableListOf<VideoItem>()

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        refreshVideos()
        showHome()
        if (it) showWelcome()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navHome.setOnClickListener { showHome() }
        binding.navReels.setOnClickListener { showReels() }
        binding.navLikes.setOnClickListener { showLikes() }
        binding.navFolders.setOnClickListener { showFolders() }
        binding.navAbout.setOnClickListener { showAbout() }

        checkPermissionAndStart()
    }

    private fun checkPermissionAndStart() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= 33)
            Manifest.permission.READ_MEDIA_VIDEO else Manifest.permission.READ_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            refreshVideos()
            showHome()
            showWelcome()
        } else requestPermission.launch(permission)
    }

    private fun refreshVideos() {
        videos.clear()
        videos.addAll(VideoRepository.loadVideos(this))
    }

    private fun showWelcome() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.dialog_welcome, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val messages = resources.getStringArray(R.array.developer_welcome_messages)
        view.findViewById<TextView>(R.id.greeting).text = messages[Random.nextInt(messages.size)]
        view.findViewById<TextView>(R.id.close).setOnClickListener { dialog.dismiss() }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()
    }

    private fun showHome() {
        val b = ScreenHomeBinding.inflate(layoutInflater)
        binding.content.removeAllViews()
        binding.content.addView(b.root)
        b.openReels.setOnClickListener { showReels() }
        b.openLikes.setOnClickListener { showLikes() }
        b.openFolders.setOnClickListener { showFolders() }
    }

    private fun showReels() {
        refreshVideos()
        val b = ScreenReelsBinding.inflate(layoutInflater)
        binding.content.removeAllViews()
        binding.content.addView(b.root)
        b.reelsEmpty.visibility = if (videos.isEmpty()) TextView.VISIBLE else TextView.GONE
        if (videos.isNotEmpty()) {
            b.reelsPager.adapter = ReelsAdapter(this, videos)
            b.reelsPager.orientation = androidx.viewpager2.widget.ViewPager2.ORIENTATION_VERTICAL
            b.reelsPager.offscreenPageLimit = 1
        }
    }

    private fun showLikes() {
        refreshVideos()
        showList("الفيديوهات المعجبة", videos.filter { Prefs.isLiked(this, it.uri) })
    }

    private fun showFolders() {
        refreshVideos()
        showList("فيديوهات الجهاز", videos)
    }

    private fun showList(title: String, items: List<VideoItem>) {
        val b = ScreenListBinding.inflate(layoutInflater)
        binding.content.removeAllViews()
        binding.content.addView(b.root)
        b.listTitle.text = title
        b.list.layoutManager = LinearLayoutManager(this)
        b.list.adapter = VideoAdapter(items)
        b.empty.visibility = if (items.isEmpty()) TextView.VISIBLE else TextView.GONE
        b.list.visibility = if (items.isEmpty()) TextView.GONE else TextView.VISIBLE
    }

    private fun showAbout() {
        val b = ScreenAboutBinding.inflate(layoutInflater)
        binding.content.removeAllViews()
        binding.content.addView(b.root)
        b.contactTelegram.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/YM_IBB")))
        }
    }
}
