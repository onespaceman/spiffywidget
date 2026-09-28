package one.spaceman.spiffywidget.data

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import one.spaceman.spiffywidget.worker.WidgetWorkManager

fun registerWallpaperListener(context: Context) {
    // Update color palette on wallpaper change
    try {
        WallpaperManager.getInstance(context).addOnColorsChangedListener(
            { _, which ->
                if (which and WallpaperManager.FLAG_SYSTEM != 0) {
                    WidgetWorkManager(context).updateNow(arrayOf(WidgetWorkManager.PartialUpdate.CALENDAR))
                }
            },
            Handler(context.mainLooper)
        )
    } catch (_: Exception) { }
}

fun getWallpaper(context: Context): Bitmap {
    return WallpaperManager
        .getInstance(context)
        .getBuiltInDrawable(WallpaperManager.FLAG_SYSTEM)
        .toBitmap()
}

fun getWallpaperColors(context: Context): Palette {
    val wallpaper = getWallpaper(context)
    return Palette.from(wallpaper).maximumColorCount(8).generate()
}