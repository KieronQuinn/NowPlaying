package android.app.search

import android.content.Intent
import android.net.Uri

class SearchAction {

    class Builder(val packageName: String, val name: String) {

        fun setIntent(intent: Intent): Builder = this
        fun setData(uri: Uri) = this
        fun setPackage(packageName: String) = this

        fun build() = SearchAction()
    }

}