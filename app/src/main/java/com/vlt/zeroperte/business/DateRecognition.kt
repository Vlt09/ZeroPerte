package com.vlt.zeroperte.business

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class DateRecognition @Inject constructor() : ImageAnalysis.Analyzer {

    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    internal val _dateRecognition: MutableStateFlow<String?> = MutableStateFlow(null)
    val dateRecognition = _dateRecognition.asStateFlow()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            dateRecognition(image, imageProxy)
        }
    }

    internal fun dateRecognition(image: InputImage, imageProxy: ImageProxy) {

        val result = recognizer.process(image)
            .addOnSuccessListener { result ->
                val resultText = result.text
                for (block in result.textBlocks) {
                    val blockText = block.text
                    val blockCornerPoints = block.cornerPoints
                    val blockFrame = block.boundingBox
                    for (line in block.lines) {
                        val lineText = line.text
                        val lineCornerPoints = line.cornerPoints
                        val lineFrame = line.boundingBox
                        for (element in line.elements) {
                            val elementText = element.text
                            val elementCornerPoints = element.cornerPoints
                            val elementFrame = element.boundingBox

                            val datePattern = "^(0[1-9]|[1-2][0-9]|3[0-1])/(0[1-9]|1[0-2])/[0-9][0-9][0-9][0-9]$"
                            val regex = Regex(datePattern)

                            if(regex.matches(elementText))
                                _dateRecognition.value = elementText
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                _dateRecognition.value = null
            }
            .addOnCompleteListener { e -> imageProxy.close() }
    }
}