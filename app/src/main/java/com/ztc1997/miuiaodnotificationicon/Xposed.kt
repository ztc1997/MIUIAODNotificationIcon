package com.ztc1997.miuiaodnotificationicon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.ztc1997.miuiaodnotificationicon.extention.KXposedHelpers
import com.ztc1997.miuiaodnotificationicon.extention.toBitmap
import com.ztc1997.miuiaodnotificationicon.extention.toGrayscale
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

private const val ICON_PKG_DRAWABLE_MAP = "ICON_PKG_DRAWABLE_MAP"
private const val ICON_SIZE = 126

@Suppress("UNCHECKED_CAST")
class Xposed : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.miui.aod") return

        val AODViewClass = XposedHelpers.findClass(
            "com.miui.aod.AODView",
            lpparam.classLoader
        )

        KXposedHelpers.findAndHookConstructor(
            AODViewClass, Context::class.java, AttributeSet::class.java
        ) {
            afterHookedMethod {
                val ctx = it.args[0] as Context

                val mIconMap = XposedHelpers.getObjectField(it.thisObject, "mIconMap") as HashMap<String, Int>

                // Add missing packages to the map
                val pkgInfos =
                    ctx.packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES or PackageManager.GET_SERVICES)
                for (pkgInfo in pkgInfos) {
                    if (mIconMap.containsKey(pkgInfo.packageName)) continue

                    mIconMap[pkgInfo.packageName] = -1
                }
            }
        }

        val BadgetImageViewClass = XposedHelpers.findClass(
            "com.miui.aod.BadgetImageView",
            lpparam.classLoader
        )

        KXposedHelpers.findAndHookMethod(
            AODViewClass, "bindView", BadgetImageViewClass, Int::class.java
        ) {
            beforeHookedMethod {
                val pkg = (XposedHelpers.callMethod(it.thisObject, "getPkg", it.args[1])
                    ?: return@beforeHookedMethod) as String
                val badgetImageView = it.args[0] as ImageView

                // Check if the package is added by our module
                val mIconMap = XposedHelpers.getObjectField(it.thisObject, "mIconMap") as HashMap<String, Int>
                if (mIconMap[pkg] != -1) return@beforeHookedMethod

                badgetImageView.background = getOrLoadIcon(it.thisObject as View, pkg)
                badgetImageView.visibility = View.VISIBLE

                it.result = null
            }
        }
    }

    private fun getOrLoadIcon(aodView: View, pkg: String): Drawable {
        val iconPkgDrawableMap =
            (XposedHelpers.getAdditionalInstanceField(
                aodView,
                ICON_PKG_DRAWABLE_MAP
            ) as HashMap<String, Drawable>?)
                ?: HashMap()

        // Try to get icon from cache
        val drawable = iconPkgDrawableMap[pkg]
        if (drawable != null)
            return drawable

        // Load app icon
        val icon = aodView.context.packageManager.getApplicationIcon(pkg)

        // Resize icon
        val bitmap = icon.toBitmap()
        val bmpResized = Bitmap.createScaledBitmap(bitmap, ICON_SIZE, ICON_SIZE, true)

        // Grayscale icon
        val bmpGrayscaled = bmpResized.toGrayscale()

        val retIcon = BitmapDrawable(aodView.context.resources, bmpGrayscaled)

        // Store to cache
        iconPkgDrawableMap[pkg] = retIcon
        XposedHelpers.setAdditionalInstanceField(aodView, ICON_PKG_DRAWABLE_MAP, iconPkgDrawableMap)

        return retIcon
    }
}