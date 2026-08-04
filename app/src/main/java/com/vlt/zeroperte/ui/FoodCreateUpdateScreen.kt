package com.vlt.zeroperte.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ch.benlu.composeform.fields.DateField
import ch.benlu.composeform.fields.TextField
import ch.benlu.composeform.formatters.dateLong

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
    onSaveClick: () -> Unit = {}
) {

    var foodNameTest by remember { mutableStateOf("") }
    var expiryDateText by remember { mutableStateOf("") }
    var purchaseDateText by remember { mutableStateOf("") }
    var brandText by remember { mutableStateOf("") }
    var commentText by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("0") }

    val categories = listOf("Frais", "Surgelé", "Sec")

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
            IconButton(onClick = onCancelClick) {
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

            IconButton(onClick = onSaveClick) {
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
                fieldState = viewModel.form.expiredDate,
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

        TextField(
            label = "Quantité (optionnelle)",
            form = viewModel.form,
            fieldState = viewModel.form.amount
        )


        // --- Marque (optionnelle) ---
        TextField(
            label = "Marque (optionnelle)",
            form = viewModel.form,
            fieldState = viewModel.form.brand
        ).Field()

        // --- Marque (optionnelle) ---
        TextField(
            label = "Catégorie (optionnelle)",
            form = viewModel.form,
            fieldState = viewModel.form.category
        ).Field()

        // --- Commentaire (optionnel) ---
        TextField(
            label = "Commentaire (optionnelle)",
            form = viewModel.form,
            fieldState = viewModel.form.comment
        ).Field()
    }
}