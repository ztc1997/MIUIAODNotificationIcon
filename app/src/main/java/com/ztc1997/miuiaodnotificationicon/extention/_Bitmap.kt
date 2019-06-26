package com.ztc1997.miuiaodnotificationicon.extention

import android.graphics.*


fun Bitmap.toGrayscale(): Bitmap {
    val width: Int = this.width
    val height: Int = this.height

    val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
    val c = Canvas(bmpGrayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    //Set the matrix to affect the saturation of colors.
    //A value of 0 maps the color to gray-scale.
    cm.setSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    c.drawBitmap(this, 0f, 0f, paint)
    return bmpGrayscale
}