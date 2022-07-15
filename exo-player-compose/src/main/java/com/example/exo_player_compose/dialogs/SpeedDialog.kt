package com.example.exo_player_compose.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SpeedDialog(
    onDismissRequest: () -> Unit,
    onClick:(Float) -> Unit
) {
    val speeds = listOf(0.25f,0.5f,0.75f,1f,1.25f,1.5f,2f,2.5f,3f)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        text = {
            LazyColumn(content = {
                items(speeds){ item ->
                    TextButton(
                        modifier = Modifier.padding(5.dp),
                        onClick = {
                            onClick(item)
                            onDismissRequest()
                        }) {
                        Text(
                            text = item.toString(),
                            modifier = Modifier.padding(10.dp),
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            })
        },
        buttons = {

        }
    )
}