package com.example.testml.mainScreen

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.testml.MainViewModel
import com.example.testml.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    photoPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
){
    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController){
        systemUiController.setSystemBarsColor( color = Color.Transparent)

        onDispose {  }
    }

    var showPhotoPicker by remember{ mutableStateOf(false) }

    val isBottomSheetVisible = remember {
        viewModel.isBottomSheetVisible
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        if(showPhotoPicker) {
            PickPhoto(photoPicker)
            showPhotoPicker = !showPhotoPicker
        }

        if(isBottomSheetVisible.value){
            ModalBottomSheetDialog(isBottomSheetVisible)
        }

        Image(
            painter = painterResource(id = R.drawable.doctor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(15.dp))
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Pulsating {
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(100.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { showPhotoPicker = !showPhotoPicker }
                            .padding(15.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun Pulsating(pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(modifier = Modifier.scale(scale)) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomSheetDialog(
    isBottomSheetVisible: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch {
                isBottomSheetVisible.value = false
            }
        },
        sheetState = sheetState,
        containerColor = Color.White,
        content = {
            ModelBottomSheetContent()
        }
    )
}

@Composable
fun ModelBottomSheetContent(){
    WhatIsThis()
    Steps()
}

@Composable
fun WhatIsThis(){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row(
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min)
        ) {
            Divider(
                color = Color.Green,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(3.dp)
            )
            Text(
                text = "Данная модель определяет наличие сколиоза по рентген снимку. " +
                        "Пользователю выводится класс (сколиоз либо здоровая спина) и вероятность, с которой модель классифицировала снимок",
                modifier = Modifier
                    .padding(start = 5.dp),
                textAlign = TextAlign.Center,
                color = Color.Black.copy(0.6f)
            )
        }
    }
}

@Composable
fun Steps(){
    val steps = listOf(
        Step(
            title = "📌 Нажмите на кнопку +",
            number = 1
        ),
        Step(
            title = "📸 Выберите нужную фотографию",
            number = 2
        ),
        Step(
            title = "💯 Получите результат",
            number = 3
        )
    )

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        steps.forEach { 
            StepsItem(step = it)
        }
    }
}

@Composable
fun StepsItem(step: Step){
    Row(
        modifier = Modifier
            .height(80.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min),
        ) {
            Text(
                text = step.number.toString(),
                modifier = Modifier
                    .drawBehind {
                        drawCircle(
                            color = Color.Green,
                            radius = 50f
                        )
                    },
                color = Color.Black.copy(0.6f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = step.title, color = Color.Black.copy(0.6f))
        }
    }
}

@Composable
fun PickPhoto(photoPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>){
    photoPicker.launch(
        PickVisualMediaRequest(
            ActivityResultContracts.PickVisualMedia.ImageOnly
        )
    )
}

data class Step(
    val title: String = "",
    val number: Int = 1
)