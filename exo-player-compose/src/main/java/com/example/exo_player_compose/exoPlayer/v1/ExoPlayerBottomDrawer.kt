package com.example.exo_player_compose.exoPlayer.v1

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable

@ExperimentalMaterialApi
@Composable
internal fun BottomDrawerExoPlayer(
    drawerState:BottomDrawerState,
    drawerContent: (@Composable ColumnScope.() -> Unit)?,
    content: @Composable () -> Unit
) {
    if (drawerContent == null){
        content()
    }else {
        BottomDrawer(
            drawerState = drawerState,
            drawerContent = drawerContent,
            content = content
        )
    }
}