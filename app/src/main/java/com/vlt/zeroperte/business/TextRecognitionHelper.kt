package com.vlt.zeroperte.business

import android.content.Context
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

object TextRecognitionHelper {

    @OptIn(ExperimentalGetImage::class)
    fun recognizeTextFromImage(imageProxy: ImageProxy, onResult: (String) -> Unit) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val mediaImage = imageProxy.image

        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val date = recognizeDatePattern(visionText)
                    if (date != null) {
                        onResult(date)
                    }
                }
                .addOnFailureListener { exception ->
                    onResult("Recognition failed: ${exception.localizedMessage}")
                }
        }
    }

    fun recognizeDatePattern(text: Text): String? {
        for (block in text.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text

                val datePattern = "^(0[1-9]|[1-2][0-9]|3[0-1])/(0[1-9]|1[0-2])/[0-9][0-9][0-9][0-9]$"
                val regex = Regex(datePattern)

                if(regex.matches(lineText)){
                    return lineText
                }

            }
        }
        return null
    }
}