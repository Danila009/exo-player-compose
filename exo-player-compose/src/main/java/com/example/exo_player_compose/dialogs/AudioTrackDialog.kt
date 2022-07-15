package com.example.exo_player_compose.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

internal data class AudioTrack(
    val position:Int,
    val name:String
)

@Composable
internal fun AudioTrackDialog(
    audioTrack:ArrayList<String>,
    onDismissRequest:() -> Unit,
    onClickAudioTrack:(AudioTrack) -> Unit
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            LazyColumn(content = {
                itemsIndexed(audioTrack){ index, item ->
                    TextButton(
                        modifier = Modifier.padding(5.dp),
                        onClick = { onClickAudioTrack(
                            AudioTrack(
                                position = index,
                                name = item
                            )
                        ) }
                    ) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(5.dp),
                            fontWeight = FontWeight.W600
                        )
                    }
                }
            })
        },
        buttons = {

        }
    )
}