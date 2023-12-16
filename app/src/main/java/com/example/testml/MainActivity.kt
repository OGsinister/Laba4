package com.example.testml

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testml.mainScreen.MainScreen
import com.example.testml.navigation.Screens
import com.example.testml.ui.theme.TestMLTheme
import com.example.testml.ui.theme.topAppBarColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestMLTheme {
                val context = LocalContext.current
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()

                WindowCompat.setDecorFitsSystemWindows(window, false)

                val photoPicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) {
                    if (it != null) {
                        viewModel.selectedImage.value = BitmapFactory.decodeStream(
                            context.contentResolver.openInputStream(it)
                        )
                        navController.navigate(Screens.ResultScreen.route)
                    } else { }
                }

                Navigation(
                    navController = navController,
                    viewModel = viewModel,
                    photoPicker = photoPicker
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Navigation(
    navController: NavHostController,
    viewModel: MainViewModel,
    photoPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>
){
    NavHost(navController = navController, startDestination = Screens.MainScreen.route) {
        composable(Screens.MainScreen.route){
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text  = "Laba 4",
                                color = Color.White
                            )
                        },
                        actions = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.isBottomSheetVisible.value =
                                            !viewModel.isBottomSheetVisible.value
                                    },
                                tint = Color.White
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(topAppBarColor)
                    )
                },
                content = {
                    MainScreen(
                        viewModel = viewModel,
                        photoPicker = photoPicker
                    )
                }
            )
        }
        composable(Screens.ResultScreen.route){
            ResultScreen(viewModel = viewModel, navController)
        }
    }
}