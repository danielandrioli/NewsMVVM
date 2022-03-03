package com.dboy.newsmvvm.api.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Article(
    @PrimaryKey var id: Int? = null,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source?,
    val title: String,
    val url: String,
    val urlToImage: String?
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other != null && other is Article){
            if (this.url == other.url){
                return true
            }
        }
        return false
    }

    override fun hashCode(): Int {
        return this.url.hashCode()
    }
}
// When I undo a deletion after a swipe gesture in the FavoriteNewsFragment, the article goes to the same place it was before.
// However, this happens only if I have an id Int as PrimaryKey. If I set the url as PrimaryKey, the article goes down below the saved list
// after undoing a deletion.