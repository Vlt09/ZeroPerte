package com.vlt.zeroperte.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.benlu.composeform.fields.DateField
import ch.benlu.composeform.fields.TextField
import ch.benlu.composeform.formatters.dateLong
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    onCancelClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    navController: NavHostController
) {


    var foodNameTest by remember { mutableStateOf("") }
    var expiryDateText by remember { mutableStateOf("") }
    var purchaseDateText by remember { mutableStateOf("") }
    var brandText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("0") }

    val viewState = viewModel.viewState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope() // Use when User trigger Saved button
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewState.value) {
        when (viewState.value) {
            is FoodCreateUpdateViewModel.ViewState.Modify -> {
                snackbarHostState.showSnackbar("Aliment enregistré")
            }
            FoodCreateUpdateViewModel.ViewState.Failure -> {
                snackbarHostState.showSnackbar("Problème pendant l'enregistrement de l'aliment")
            }
            FoodCreateUpdateViewModel.ViewState.Waiting -> {}
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
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // --- Barre du haut : Annuler + titre ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 32.dp)
                ) {
                    IconButton(onClick = {
                        navController.navigate(Screen.FoodList.route)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Annuler",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Text(
                        text = "Annuler",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Nouvel aliment",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(start = 24.dp)
                    )

                    IconButton(onClick = {
                        coroutineScope.launch {
                            viewModel.save()
                            navController.navigate(Screen.FoodList.route)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Enregistrer",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                }

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

                    IconButton(
                        onClick = { /* TODO: ouvrir l'appareil photo */ },
                        modifier = Modifier.align(Alignment.CenterEnd)
                            .padding(bottom = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PhotoCamera,
                            contentDescription = "Prendre une photo de la date de péremption"
                        )
                    }
                }


                // --- Date d'achat (optionnelle) ---
                DateField(
                    label = "Date d'achat (optionnelle)",
                    form = viewModel.form,
                    fieldState = viewModel.form.purchasedDate,
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
        }
    }

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
