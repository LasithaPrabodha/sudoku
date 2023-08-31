package com.lasithaprabodha.sudoku.domain

interface IGameRepository {
    suspend fun saveGame(
        elapsedTime: Long,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateGame(
        game: SudokuPuzzle,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateNode(
        x: Int,
        y: Int,
        color: Int,
        elapsedTime: Long,
        onSuccess: (isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun createNewGame(
        settings: Settings,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun getCurrentGame(
        onSuccess: (currentGame: SudokuPuzzle, isComplete: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun getSettings(
        onSuccess: (Settings) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateSettings(
        settings: Settings,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    )
}