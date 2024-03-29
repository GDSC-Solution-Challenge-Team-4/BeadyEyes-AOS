package com.pointer.beadyeyes.util

import android.content.Context
import android.graphics.Bitmap
import com.pointer.beadyeyes.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteBuffer.allocateDirect
import java.nio.ByteOrder

private const val IMAGE_SIZE = 224

fun loadImageBufferFromBitmap(bitmap: Bitmap): ByteBuffer {
    val byteBuffer = allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3)
    byteBuffer.order(ByteOrder.nativeOrder())

    val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
    bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

    var pixelIndex = 0
    for (i in 0 until IMAGE_SIZE) {
        for (j in 0 until IMAGE_SIZE) {
            val pixel = pixels[pixelIndex++]
            byteBuffer.putFloat(((pixel shr 16) and 0xFF) * (1f / 255f))
            byteBuffer.putFloat(((pixel shr 8) and 0xFF) * (1f / 255f))
            byteBuffer.putFloat((pixel and 0xFF) * (1f / 255f))
        }
    }

    return byteBuffer
}

fun classifyImage(context: Context, bitmap: Bitmap): String {
    val model = Model.newInstance(context)

    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

    val byteBuffer = loadImageBufferFromBitmap(bitmap)

    inputFeature0.loadBuffer(byteBuffer)

    val outputs = model.process(inputFeature0)
    val outputFeature0 = outputs.outputFeature0AsTensorBuffer

    val confidences = outputFeature0.floatArray
    val maxIndex = confidences.indexOfFirst { it == confidences.max() }


    val classes = arrayOf(
        "10원",
        "50원",
        "100원",
        "500원",
        "1000원",
        "5000원",
        "10000원",
        "50000원",
        "1다임",
        "쿼터달러",
        "1달러",
        "2달러",
        "5달러",
        "10달러",
        "20달러",
        "50달러",
        "100달러"
    )

    val result = classes[maxIndex]

    model.close()
    return result
}
