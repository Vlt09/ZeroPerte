package com.vlt.zeroperte.ui.Composable

import android.app.Activity
import android.content.ContentValues.TAG
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.benlu.composeform.fields.DateField
import ch.benlu.composeform.fields.TextField
import ch.benlu.composeform.formatters.dateLong
import com.vlt.zeroperte.business.TextRecognitionHelper
import com.vlt.zeroperte.ui.FoodList
import com.vlt.zeroperte.ui.ViewModel.FoodCreateUpdateViewModel
import com.vlt.zeroperte.utils.Converters
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.Executors

/**
 * Écran d'ajout/modification d'aliment.
 *
 * NOTE : version statique pour l'instant (état local via `remember`).
 * Le câblage vers un vrai FoodCreateUpdateViewModel (validation, sauvegarde,
 * pré-remplissage en mode édition) reste à faire ensuite.
 * Le champ de date est ici un simple texte — un vrai DatePickerDialog
 * Material3 est une étape séparée, à connecter sur onClick du champ.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCreateUpdateScreen(
    modifier: Modifier = Modifier,
    viewModel: FoodCreateUpdateViewModel = hiltViewModel(),
    foodId: Long?,
    navController: NavHostController,
    activity: Activity
) {

    val viewState = viewModel.viewState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope() // Use when User trigger Saved button
    val snackbarHostState = remember { SnackbarHostState() }
    val controller = remember {
        LifecycleCameraController(activity.applicationContext)
            .apply {
                setEnabledUseCases(
                    CameraController.IMAGE_CAPTURE
                )
            }
    }

    LaunchedEffect(viewState.value, foodId) {
        when (viewState.value) {
            is FoodCreateUpdateViewModel.ViewState.Create -> {
                snackbarHostState.showSnackbar("Aliment enregistré")
            }
            FoodCreateUpdateViewModel.ViewState.Updated -> {
                snackbarHostState.showSnackbar("Aliment mis à jour")
            }
            FoodCreateUpdateViewModel.ViewState.Failure -> {
                snackbarHostState.showSnackbar("Problème pendant l'enregistrement de l'aliment")
            }
            FoodCreateUpdateViewModel.ViewState.Waiting -> {}
            else -> {}
        }

        if(foodId != null){
            viewModel.fetchFood(foodId)
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 32.dp)
            ) {
                // --- Barre du haut : Annuler + titre ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {

                    Box(){
                    IconButton(onClick = {
                        navController.navigate(FoodList)
                    }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Annuler",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(end = 13.dp)
                            )
                        }

                        Text(
                            text = "Annuler",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                    }


                    Text(
                        text = "Nouvel aliment",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 37.dp)
                    )

                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.save()
                            navController.navigate(FoodList)
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Enregistrer",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 25.dp, top = 3.dp)
                                .size(32.dp)
                        )
                    }

                }

            }

            fun localDateToDate(localDate: LocalDate): Date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

            when (viewState.value) {
                is FoodCreateUpdateViewModel.ViewState.Update -> {
                    val updateContent = viewState.value as FoodCreateUpdateViewModel.ViewState.Update

                    viewModel.form.name.state.value = updateContent.resource.name
                    viewModel.form.expiryDate.state.value =
                        localDateToDate(updateContent.resource.expiryDate)
                    viewModel.form.brand.state.value = updateContent.resource.brand
                    viewModel.form.datePurchased.state.value = if (updateContent.resource.datePurchased != null)
                        localDateToDate(updateContent.resource.datePurchased) else null
                    viewModel.form.amount.state.value = updateContent.resource.amount.toString()
                    viewModel.form.comment.state.value = updateContent.resource.comment
                    viewModel.form.category.state.value = updateContent.resource.category


                }
                else -> {}
            }

            FormFieldsUi(viewModel, controller, viewState)

        }
    }

}

@Composable
private fun FormFieldsUi(
    viewModel: FoodCreateUpdateViewModel,
    controller: LifecycleCameraController,
    viewState: State<FoodCreateUpdateViewModel.ViewState>,
) {

    var recognizedDate by remember { mutableStateOf("") }

    // Entry name
    TextField(
        label = "Nom de l'aliment",
        form = viewModel.form,
        fieldState = viewModel.form.name
    ).Field()

    // --- Date de péremption (obligatoire) ---
    Box() {
        DateField(
            label = "Date de péremption",
            form = viewModel.form,
            fieldState = viewModel.form.expiryDate,
            formatter = ::dateLong,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ).Field()

        var showCameraDialog by remember { mutableStateOf(false) }

        IconButton(
            onClick = { showCameraDialog = true },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Prendre une photo de la date de péremption"
            )
        }

        if (showCameraDialog) {
            CameraBox {
                if (it != null){
                    recognizedDate = it
                    viewModel.form.expiryDate.state.value = Converters.fromStringDateToDate(it)
                    Log.i(TAG, "recognizedDate $recognizedDate")
                }
                showCameraDialog = false
            }
        }
    }


    // --- Date d'achat (optionnelle) ---
    DateField(
        label = "Date d'achat (optionnelle)",
        form = viewModel.form,
        fieldState = viewModel.form.datePurchased,
        formatter = ::dateLong,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()

    // Amount
    TextField(
        label = "Quantité (optionnelle)",
        form = viewModel.form,
        fieldState = viewModel.form.amount,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)

    ).Field()


    // --- Marque (optionnelle) ---
    TextField(
        label = "Marque (optionnelle)",
        form = viewModel.form,
        fieldState = viewModel.form.brand,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()

    // --- Marque (optionnelle) ---
    TextField(
        label = "Catégorie (optionnelle)",
        form = viewModel.form,
        fieldState = viewModel.form.category,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()

    // --- Commentaire (optionnel) ---
    TextField(
        label = "Commentaire (optionnelle)",
        form = viewModel.form,
        fieldState = viewModel.form.comment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()
}

@Composable
internal fun SaveSuccessMessage(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2_500)
        onFinished()
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Aliment enregistré",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
internal fun SaveErrorMessage(
    message: String = "Problème pendant l'enregistrement de l'aliment"
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CameraBox(onTextRecognized: (String?) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    // Bind camera
    LaunchedEffect(Unit) {
        try {
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch (e: Exception) {
            Log.e("CameraBox", "Camera initialization failed", e)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(1f)
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Button(
            onClick = {
                imageCapture.takePicture(
                    cameraExecutor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            TextRecognitionHelper.recognizeTextFromImage( image) {
                                onTextRecognized(it)
                            }
                            Log.i("CameraPreview", "Capture sucess")

                        }
                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraPreview", "Capture failed: ${exception.message}", exception)
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {
            Text("Capture")
        }
    }
}