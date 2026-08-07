package com.vlt.zeroperte.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vlt.zeroperte.ui.theme.extendedDark
import com.vlt.zeroperte.ui.theme.extendedLight
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodDetailItem(
    val name: String,
    val brand: String?,
    val purchaseDate: LocalDate?,
    val expiryDate: LocalDate,
    val comment: String?,
    val remainingDays: Int,
    val photo: ImageVector? = null
)

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun FoodDetailScreen(
    modifier: Modifier = Modifier,
    foodId: Long?,
    viewModel: FoodDetailViewModel,
    onBackClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val extendedColors = if (isSystemInDarkTheme()) extendedDark else extendedLight


    val foodDetail = FoodDetailItem(
        name = "Test",
        brand = "Test",
        purchaseDate = null,
        expiryDate = LocalDate.of(2026, 8, 12),
        comment = null,
        remainingDays = 5,
        photo = null
    )

    val statusColor = when {
        foodDetail.remainingDays <= 0 -> extendedColors.expiredCard
        foodDetail.remainingDays <= 5 -> extendedColors.expiredSoonCard
        else -> extendedColors.validCard
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // --- Barre du haut : retour + titre + suppression ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Détails aliment",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.onBackground
            ) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Supprimer l'aliment",
                        tint = MaterialTheme.colorScheme.background
                    )
                }
            }
        }

        // --- Photo + statut de péremption ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = foodDetail.photo ?: Icons.Filled.Photo,
                    contentDescription = "Photo de l'aliment",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (foodDetail.remainingDays <= 0) {
                        "Expiré"
                    } else {
                        "Expire dans ${foodDetail.remainingDays} jours"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor.onColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // --- Champs en lecture seule ---
        ReadOnlyDetailField(
            label = "Nom de l'aliment",
            value = foodDetail.name
        )

        ReadOnlyDetailField(
            label = "Date d'achat",
            value = foodDetail.purchaseDate?.format(dateFormatter) ?: "mm/dd/yyyy"
        )

        ReadOnlyDetailField(
            label = "Marque",
            value = foodDetail.brand?.takeIf { it.isNotBlank() } ?: "N/A"
        )

        ReadOnlyDetailField(
            label = "Commentaire",
            value = foodDetail.comment?.takeIf { it.isNotBlank() } ?: "Saisir"
        )
    }
}

/**
 * Champ visuellement identique à un OutlinedTextField (même label
 * "coupant" la bordure), mais readOnly=true : l'utilisateur ne peut
 * pas taper dedans, cohérent avec la spec UX ("données en lecture seule").
 */
@Composable
private fun ReadOnlyDetailField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )
}