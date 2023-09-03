package com.lasithaprabodha.sudoku.ui.newgame.buildlogic


import android.content.Context
import com.lasithaprabodha.sudoku.common.ProductionDispatcherProvider
import com.lasithaprabodha.sudoku.persistence.*
import com.lasithaprabodha.sudoku.ui.newgame.NewGameContainer
import com.lasithaprabodha.sudoku.ui.newgame.NewGameLogic
import com.lasithaprabodha.sudoku.ui.newgame.NewGameViewModel

internal fun buildNewGameLogic(
    container: NewGameContainer,
    viewModel: NewGameViewModel,
    context: Context
): NewGameLogic {
    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}