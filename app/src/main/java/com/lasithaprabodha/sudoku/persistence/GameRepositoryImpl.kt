package com.lasithaprabodha.sudoku.persistence

import com.lasithaprabodha.sudoku.domain.GameStorageResult
import com.lasithaprabodha.sudoku.domain.IGameDataStorage
import com.lasithaprabodha.sudoku.domain.IGameRepository
import com.lasithaprabodha.sudoku.domain.ISettingsStorage
import com.lasithaprabodha.sudoku.domain.Settings
import com.lasithaprabodha.sudoku.domain.SettingsStorageResult
import com.lasithaprabodha.sudoku.domain.SudokuPuzzle

class GameRepositoryImpl(
    private val gameStorage: IGameDataStorage,
    private val settingsStorage: ISettingsStorage
) : IGameRepository {
    override suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val getCurrentGameResult = gameStorage.getCurrentGame()) {
            is GameStorageResult.OnSuccess -> {
                gameStorage.updateGame(
                    getCurrentGameResult.currentGame.copy(
                        elapsedTime = elapsedTime
                    )
                )

                onSuccess()
            }

            is GameStorageResult.OnError -> {
                onError(getCurrentGameResult.exception)
            }
        }


    }

    override suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val updateGameResult = gameStorage.updateGame(game)) {
            is GameStorageResult.OnSuccess -> onSuccess()
            is GameStorageResult.OnError -> onError(updateGameResult.exception)
        }
    }

    override suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val result = gameStorage.updateNode(x, y, color, elapsedTime)) {
            is GameStorageResult.OnSuccess -> onSuccess(
                puzzleIsComplete(result.currentGame)
            )

            is GameStorageResult.OnError -> onError(
                result.exception
            )
        }
    }

    override suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Request current game
        when (val getCurrentGameResult = gameStorage.getCurrentGame()) {
            // Current game returns onSuccess; forward to caller onSuccess
            is GameStorageResult.OnSuccess -> onSuccess(
                getCurrentGameResult.currentGame,
                puzzleIsComplete(
                    getCurrentGameResult.currentGame
                )
            )
            // Current game returns onError
            is GameStorageResult.OnError -> {
                // Request current Settings from settingsStorage
                when (val getSettingsResult = settingsStorage.getSettings()) {
                    // settingsStorage returns onSuccess
                    is SettingsStorageResult.OnSuccess -> {
                        // Write game update to gameStorage (to ensure consistent state between front and back end)
                        when (val updateGameResult =
                            createAndWriteNewGame(getSettingsResult.settings)) {
                            // gameStorage returns onSuccess; forward to caller onSuccess
                            is GameStorageResult.OnSuccess -> onSuccess(
                                updateGameResult.currentGame,
                                puzzleIsComplete(
                                    updateGameResult.currentGame
                                )
                            )
                            // gameStorage returns onError; forward to caller onError
                            is GameStorageResult.OnError -> onError(updateGameResult.exception)
                        }
                    }
                    // settingsStorage returns onError
                    is SettingsStorageResult.OnError -> onError(getSettingsResult.exception)
                    is SettingsStorageResult.OnComplete -> {}
                }
            }
        }
    }

    override suspend fun createNewGame(
        settings: Settings,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        when (val updateSettingsResult = settingsStorage.updateSettings(settings)) {
            is SettingsStorageResult.OnComplete -> {
                when (val updateGameResult = createAndWriteNewGame(settings)) {
                    is GameStorageResult.OnSuccess -> onSuccess()
                    is GameStorageResult.OnError -> onError(updateGameResult.exception)
                }
            }

            is SettingsStorageResult.OnError -> onError(updateSettingsResult.exception)
            is SettingsStorageResult.OnSuccess -> onSuccess()
        }
    }


    private suspend fun createAndWriteNewGame(settings: Settings): GameStorageResult {
        return gameStorage.updateGame(
            SudokuPuzzle(
                settings.boundary,
                settings.difficulty
            )
        )
    }

    override suspend fun getSettings(onSuccess: (Settings) -> Unit, onError: (Exception) -> Unit) {
        when (val getSettingsResult = settingsStorage.getSettings()) {
            is SettingsStorageResult.OnSuccess -> onSuccess(getSettingsResult.settings)
            is SettingsStorageResult.OnError -> onError(getSettingsResult.exception)
            is SettingsStorageResult.OnComplete -> {}
        }
    }

    override suspend fun updateSettings(
        settings: Settings,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        settingsStorage.updateSettings(settings)
        onSuccess()
    }

}