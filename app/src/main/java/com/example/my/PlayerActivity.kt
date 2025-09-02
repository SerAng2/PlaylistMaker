package com.example.my

import Track
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {

    private var track: Track? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player) // Ваш макет из XML
        val toolbar = findViewById<MaterialToolbar>(R.id.backPlaylist)
        toolbar.setNavigationOnClickListener {
            // Закрываем текущую активность и возвращаемся назад
            finish()
        }
        // Получаем данные из Intent
        track = savedInstanceState?.getParcelable("TRACK_DATA", Track::class.java)
            ?: intent.getParcelableExtra("TRACK_DATA", Track::class.java)

        if (track != null) {
            displayTrackInfo(track!!)
        } else {
            // Обработка случая, когда трек не передан
            finish()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        track?.let {
            outState.putParcelable("TRACK_DATA", it)
        }
    }
    private fun displayTrackInfo(track: Track) {
        // Загрузка обложки
        val coverImageView = findViewById<ImageView>(R.id.coverPlaylist)
        Glide.with(this)
            .load(track.getCoverArtwork())
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.ic_launcher_foreground) // Добавьте placeholder в ресурсы
            .into(coverImageView)

        // Установка названия альбома/трека
        findViewById<TextView>(R.id.nameAlbum).text = track.trackName
        findViewById<TextView>(R.id.nameMusic).text = track.artistName

        // Установка длительности
        findViewById<TextView>(R.id.playingTime).text = track.trackTime

        // Остальные поля (если есть данные)
         findViewById<TextView>(R.id.durationTime).text = track.trackTime
         findViewById<TextView>(R.id.albumName).text = track.collectionName
         findViewById<TextView>(R.id.yearMusic).text = track.releaseDate?.substring(0, 4)
         findViewById<TextView>(R.id.genreMusic).text = "${track.primaryGenreName ?: "Неизвестно"}"
         findViewById<TextView>(R.id.countryMusic).text = "${track.country ?: "Неизвестно"}"
    }
}
