package com.vlt.zeroperte.ui.Composable

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.benlu.composeform.fields.DateField
import ch.benlu.composeform.fields.PickerField
import ch.benlu.composeform.fields.TextField
import ch.benlu.composeform.formatters.dateLong
import com.vlt.zeroperte.R
import com.vlt.zeroperte.business.TextRecognitionHelper
import com.vlt.zeroperte.data.model.domain.AppLanguage
import com.vlt.zeroperte.composeforms.FoodGroup
import com.vlt.zeroperte.ui.FoodList
import com.vlt.zeroperte.ui.ViewModel.AppSettingsViewModel
import com.vlt.zeroperte.ui.ViewModel.FoodCreateUpdateViewModel
import com.vlt.zeroperte.utils.Converters
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCreateUpdateScreen(
    modifier: Modifier = Modifier,
    viewModel: FoodCreateUpdateViewModel = hiltViewModel(),
    foodId: Long?,
    navController: NavHostController,
    activity: Activity,
    appSettingsViewModel: AppSettingsViewModel
) {

    val viewState = viewModel.viewState.collectAsStateWithLifecycle()
    val appSettingsState = appSettingsViewModel.uiState.collectAsStateWithLifecycle()
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

    val savedMessage = stringResource(R.string.food_create_update_saved_snackbar)
    val updatedMessage = stringResource(R.string.food_create_update_updated_snackbar)
    val errorMessage = stringResource(R.string.common_save_error)

    LaunchedEffect(viewState.value, foodId) {
        when (viewState.value) {
            is FoodCreateUpdateViewModel.ViewState.Create -> {
                snackbarHostState.showSnackbar(savedMessage)
            }
            FoodCreateUpdateViewModel.ViewState.Updated -> {
                snackbarHostState.showSnackbar(updatedMessage)
            }
            FoodCreateUpdateViewModel.ViewState.Failure -> {
                snackbarHostState.showSnackbar(errorMessage)
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
                                contentDescription = stringResource(R.string.common_cancel),
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(end = 13.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.common_cancel),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                    }


                    Text(
                        text = stringResource(R.string.food_create_update_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 37.dp)
                    )

                    AppSettingsActions(appSettingsViewModel = appSettingsViewModel)
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
                    viewModel.form.category.state.value = updateContent.resource.category?.let { FoodGroup(it, stringResource(it.labelId)) }

                }
                else -> {}
            }

            FormFieldsUi(viewModel, controller, viewState, appSettingsState)

            IconButton(
                onClick = {
                    coroutineScope.launch {
                        viewModel.save()
                        navController.navigate(FoodList)
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .align(Alignment.CenterHorizontally)

            )
            {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = stringResource(R.string.food_create_update_content_desc_save),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 25.dp, top = 3.dp)
                        .size(32.dp)
                )
            }


        }
    }

}

@Composable
private fun FormFieldsUi(
    viewModel: FoodCreateUpdateViewModel,
    controller: LifecycleCameraController,
    viewState: State<FoodCreateUpdateViewModel.ViewState>,
    appSettingsState: State<AppSettingsViewModel.AppSettingsUiState>
) {

    var recognizedDate by remember { mutableStateOf("") }
    var showCameraDialog by remember { mutableStateOf(false) }
    var foodPhoto by remember { mutableStateOf<Bitmap?>(null) }


    /*Row(horizontalArrangement = Arrangement.Center) {

        Box(
            modifier = Modifier
                .padding(top = 15.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable{showCameraDialog = true},
            contentAlignment = Alignment.Center
        ) {
            if (foodPhoto == null){
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "Photo de l'aliment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
            }
            else{
                Image(bitmap = foodPhoto!!.asImageBitmap(), contentDescription = "Photo de l'aliment")
            }
        }

        if (showCameraDialog) {
            Dialog(
                onDismissRequest = { showCameraDialog = false },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                /*CameraBox { imageProxy ->
                    foodPhoto = imageProxy.toBitmap()
                }*/
            }
        }

        Column(modifier = Modifier.padding(start = 5.dp)) {
            // Entry name
            TextField(
                label = stringResource(R.string.common_food_name_label),
                form = viewModel.form,
                fieldState = viewModel.form.name
            ).Field()

            // --- Date de péremption (obligatoire) ---
            Box() {
                DateField(
                    label = stringResource(R.string.food_create_update_label_expiry_date),
                    form = viewModel.form,
                    fieldState = viewModel.form.expiryDate,
                    formatter = ::dateLong,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ).Field()

                IconButton(
                    onClick = { showCameraDialog = true },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = stringResource(R.string.food_create_update_content_desc_take_expiry_photo)
                    )
                }

                if (showCameraDialog) {
                    Dialog(
                        onDismissRequest = { showCameraDialog = false },
                        properties = DialogProperties(
                            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
                        )
                    ){
                        CameraBox {image ->
                            TextRecognitionHelper.recognizeTextFromImage( image) { date ->
                                if (date != null){
                                    recognizedDate = date
                                    viewModel.form.expiryDate.state.value = Converters.fromStringDateToDate(date)
                                    Log.i(TAG, "recognizedDate $recognizedDate")
                                }
                            }
                            showCameraDialog = false
                        }
                    }
                }
            }
        }
    }*/

    // Entry name
    TextField(
        label = stringResource(R.string.common_food_name_label),
        form = viewModel.form,
        fieldState = viewModel.form.name
    ).Field()

    // Date de péremption (obligatoire)
    Box() {
        DateField(
            label = stringResource(R.string.food_create_update_label_expiry_date),
            form = viewModel.form,
            fieldState = viewModel.form.expiryDate,
            formatter = ::dateLong,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ).Field()

        IconButton(
            onClick = { showCameraDialog = true },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = stringResource(R.string.food_create_update_content_desc_take_expiry_photo)
            )
        }

        if (showCameraDialog) {
            Dialog(
                onDismissRequest = { showCameraDialog = false },
                properties = DialogProperties(
                    usePlatformDefaultWidth = false, decorFitsSystemWindows = false
                )
            ) {
                CameraBox { image ->
                    TextRecognitionHelper.recognizeTextFromImage(image) { date ->
                        if (date != null) {
                            recognizedDate = date
                            viewModel.form.expiryDate.state.value =
                                Converters.fromStringDateToDate(date)
                            Log.i(TAG, "recognizedDate $recognizedDate")
                        }
                    }
                    showCameraDialog = false
                }
            }
        }
    }


        // --- Date d'achat (optionnelle) ---
    DateField(
        label = stringResource(R.string.food_create_update_label_purchase_date),
        form = viewModel.form,
        fieldState = viewModel.form.datePurchased,
        formatter = ::dateLong,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()

    // Amount
    TextField(
        label = stringResource(R.string.food_create_update_label_amount),
        form = viewModel.form,
        fieldState = viewModel.form.amount,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)

    ).Field()


    // Marque (optionnelle)
    TextField(
        label = stringResource(R.string.food_create_update_label_brand),
        form = viewModel.form,
        fieldState = viewModel.form.brand,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()

    /**
     * Met à jour la liste de nom de catégorie possible selon le langage sélectionné.
     * Cette mise à jour se fait ici et non pas dans FoodForms car les ressources xml
     * sont accessible seulement à l'exécution
     */
    @Composable
    fun translateCategoryFieldState(){
        viewModel.form.category.options = viewModel.form.category.options
            .map {foodGroup -> foodGroup?.let { FoodGroup(it.category,
                                stringResource(foodGroup.category.labelId)) } }
            .toMutableList()

        Log.d("FoodCreateUpdateScreen", "category option = ${viewModel.form.category.options}")
    }

    translateCategoryFieldState()

    // Catégorie (optionnelle)
    PickerField(
        label = stringResource(R.string.food_create_update_label_category),
        form = viewModel.form,
        fieldState = viewModel.form.category,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ).Field()


    // Commentaire (optionnel)
    TextField(
        label = stringResource(R.string.food_create_update_label_comment),
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
            text = stringResource(R.string.food_create_update_saved_snackbar),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
internal fun SaveErrorMessage(
    message: String = stringResource(R.string.common_save_error)
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
fun CameraBox(onCameraProcess: (ImageProxy) -> Unit) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    modifier = Modifier.padding(end = 25.dp),
                    text = { Text(text = stringResource(R.string.food_create_update_fab_take_photo)) },
                    onClick = { capturePhoto(context, cameraController, onCameraProcess) },
                    icon = { Icon(imageVector = Icons.Default.Camera, contentDescription = stringResource(R.string.food_create_update_camera_capture_content_desc)) }
                )
        }) { paddingValues ->
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }.also { previewView ->
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifeCycleOwner)
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.food_create_update_camera_permission_message),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.food_create_update_allow_camera_button))
                }
            }
        }
    }
}

private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onTakenPhoto: (ImageProxy) -> Unit
) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            onTakenPhoto(image)
            image.close()
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraContent", "Error capturing image", exception)
        }
    })
}