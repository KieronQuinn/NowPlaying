package android.app.search

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.ShortcutInfo
import android.net.Uri
import android.os.Bundle
import android.os.UserHandle

class SearchTarget {

    class Builder(resultType: Int, layoutType: String?, id: String) {
        fun setParentId(parentId: String) = this
        fun setPackageName(packageName: String) = this
        fun setUserHandle(userHandle: UserHandle) = this
        fun setShortcutInfo(shortcutInfo: ShortcutInfo) = this
        fun setAppWidgetProviderInfo(appWidgetProviderInfo: AppWidgetProviderInfo) = this
        fun setSliceUri(sliceUri: Uri) = this
        fun setSearchAction(searchAction: SearchAction) = this
        fun setExtras(extras: Bundle) = this
        fun setScore(score: Float) = this
        fun setHidden(hidden: Boolean) = this
        fun setShouldHide(shouldHide: Boolean) = this
        fun build() = SearchTarget()
    }

}