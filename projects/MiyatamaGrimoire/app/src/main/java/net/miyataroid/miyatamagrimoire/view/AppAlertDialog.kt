package net.miyataroid.miyatamagrimoire.view

import android.app.AlertDialog
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AppAlertDialog(
    title: String,
    message: String,
    positiveText: String,
    negativeText: String,
    onClickPositive: () -> Unit,
    onClickNegative: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(title)
        },
        text = {
            Text(message)
        },
        onDismissRequest = {},
        confirmButton = {
            TextButton(
                onClick = onClickPositive,
            ) {
                Text(text = positiveText)
            }
         },
        dismissButton = {
            TextButton(
                onClick = onClickNegative,
            ) {
                Text(text = negativeText)
            }
        },
    )
}