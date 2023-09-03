package com.lasithaprabodha.sudoku.ui.activegame.buildlogic

import android.content.Context
import com.lasithaprabodha.sudoku.common.ProductionDispatcherProvider
import com.lasithaprabodha.sudoku.persistence.GameRepositoryImpl
import com.lasithaprabodha.sudoku.persistence.LocalGameStorageImpl
import com.lasithaprabodha.sudoku.persistence.LocalSettingsStorageImpl
import com.lasithaprabodha.sudoku.persistence.LocalStatisticsStorageImpl
import com.lasithaprabodha.sudoku.persistence.settingsDataStore
import com.lasithaprabodha.sudoku.persistence.statsDataStore
import com.lasithaprabodha.sudoku.ui.activegame.ActiveGameContainer
import com.lasithaprabodha.sudoku.ui.activegame.ActiveGameLogic
import com.lasithaprabodha.sudoku.ui.activegame.ActiveGameViewModel

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {
    return ActiveGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(context.statsDataStore),
        ProductionDispatcherProvider
    )
}