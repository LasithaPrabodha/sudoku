package com.lasithaprabodha.sudoku.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lasithaprabodha.sudoku.R
import com.lasithaprabodha.sudoku.ui.SudokuTheme
import com.lasithaprabodha.sudoku.ui.lightGrey
import com.lasithaprabodha.sudoku.ui.mainTitle

@Composable
fun LoadingPreview() {
    SudokuTheme {
        LoadingScreen()
    }
}

@Composable
fun LoadingScreen() {
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(
                    R.string.logo_description
                )
            )

            LinearProgressIndicator(
                color = lightGrey,
                modifier = Modifier
                    .width(128.dp)
                    .padding(16.dp)
            )

            Text(
                text = stringResource(id = R.string.loading),
                style = mainTitle.copy(color = MaterialTheme.colors.secondary),
                modifier = Modifier.wrapContentSize()
            )
        }
    }
}