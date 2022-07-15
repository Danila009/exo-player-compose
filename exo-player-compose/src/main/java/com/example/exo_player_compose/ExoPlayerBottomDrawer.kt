package com.example.exo_player_compose

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable

@ExperimentalMaterialApi
@Composable
internal fun BottomDrawerExoPlayer(
    drawerState:BottomDrawerState,
    drawerContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    BottomDrawer(
        drawerState = drawerState,
        drawerContent = drawerContent,
        content = content
    )
}