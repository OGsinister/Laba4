package com.example.testml

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition", "StateFlowValueCalledInComposition")
@Composable
fun ResultScreen(
    viewModel: MainViewModel,
    navController: NavController
){
    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController){
        systemUiController.setSystemBarsColor( color = Color.Transparent)

        onDispose {  }
    }

    val selectedImage = viewModel.selectedImage.value
    val context = LocalContext.current

    viewModel.classifyImage(context, selectedImage!!)

    val diagnosisXrayState = viewModel.diagnosis.collectAsState()

    val isSearchVisible = remember{
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(contentAlignment = Alignment.Center){
            viewModel.selectedImage.value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(224.dp, 224.dp)
                        .fillMaxWidth(.9f)
                        .wrapContentHeight(Alignment.Top, true)
                        .scale(1f, 1.8f)
                        .blur(70.dp, BlurredEdgeTreatment.Unbounded)
                        .alpha(.5f)
                )
            }

            viewModel.selectedImage.value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(250.dp)
                )
            }
        }

        diagnosisXrayState.value.label?.let {
            Text(
                text = it,
                color = Color.White,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ){
            CircularProgressBar(
                percentage = diagnosisXrayState.value.score,
                number = 100
            )
        }

        Button(
            onClick = {
                isSearchVisible.value = !isSearchVisible.value
            },
            modifier = Modifier.padding(top = 50.dp)
        ) {
            Text("Узнать больше про ${diagnosisXrayState.value.label}")
        }

        if(isSearchVisible.value){
            diagnosisXrayState.value.label?.let {
                navigateToGoogle(it)
            }
        }
    }
}

@Composable
fun CircularProgressBar(
    percentage: Float?,
    number: Int,
    fontSize: TextUnit = 12.sp,
    radius: Dp = 50.dp,
    color: Color = Color.Green,
    strokeWidth: Dp = 8.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
){
    var animationPlayed by remember{
        mutableStateOf(false)
    }

    val curPerc = (if(animationPlayed) percentage else 0f)?.let {
        animateFloatAsState(
            targetValue = it,
            label = "",
            animationSpec = tween(
                durationMillis = animDuration,
                delayMillis = animDelay
            )
        )
    }

    LaunchedEffect(key1 = true){
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ){
        Canvas(modifier = Modifier.size(radius * 2f)){
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * curPerc!!.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${curPerc!!.value * number} %",
            color = Color.White,
            textAlign = TextAlign.Center,
            fontSize = fontSize
        )
    }
}

fun classificationMapper(route: String): String{
    if(route == "scoliosis") return "scoliosis"
    if(route == "normal") return "healthy back"
    return "not found"
}

@Composable
fun navigateToGoogle(route: String){
    val googleRoute = classificationMapper(route)
    val uri = Uri.parse("https://www.google.com/search?q=$googleRoute")
    val activity = LocalContext.current as Activity
    activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
}