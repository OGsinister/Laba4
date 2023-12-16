package com.example.testml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testml.Result
import com.example.testml.ml.MobilenetV2Scolioswithmetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category

class MainViewModel(): ViewModel() {

    val selectedImage = mutableStateOf<Bitmap?>(null)

    var diagnosis = MutableStateFlow(Result())

    val isBottomSheetVisible = mutableStateOf(false)

    fun classifyImage(context: Context, bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            val model = MobilenetV2Scolioswithmetadata.newInstance(context)
            val image = TensorImage.fromBitmap(bitmap)
            val outputs = model.process(image)

            val result = outputs.probabilityAsCategoryList.apply {
                sortByDescending {
                    it.score
                }
            }

            diagnosis.value = Result(
                label = result[0].label,
                score = result[0].score
            )

            model.close()
        }
    }
}

data class Result(
    var label: String? = "",
    var score: Float? = 0.0f
)