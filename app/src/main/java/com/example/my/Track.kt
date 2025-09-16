import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val genre: String?,
    val country: String?,
    val previewUrl: String
) : Parcelable {
    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}

data class SearchResponse(
    val resultCount: Int?,
    val results: List<TrackResponse>?,
)

data class TrackResponse(
    val trackName: String?,
    val artistName: String?,
    val releaseDate: String?,
    val trackTimeMillis: Long?,
    val collectionId: Int?,
    val artworkUrl100: String,
    val country: String?,
    val trackId: Int?,
    val primaryGenreName: String?,
    val collectionName: String?,
    val previewUrl: String
)


