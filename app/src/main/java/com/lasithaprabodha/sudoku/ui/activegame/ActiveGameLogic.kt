package com.lasithaprabodha.sudoku.ui.activegame

import com.lasithaprabodha.sudoku.common.BaseLogic
import com.lasithaprabodha.sudoku.common.DispatcherProvider
import com.lasithaprabodha.sudoku.domain.IGameRepository
import com.lasithaprabodha.sudoku.domain.IStatisticsRepository
import com.lasithaprabodha.sudoku.domain.SudokuPuzzle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ActiveGameLogic(
    private val container: ActiveGameContainer?,
    private val viewModel: ActiveGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider
) : BaseLogic<ActiveGameEvent>(), CoroutineScope {

    init {
        jobTracker = Job()
    }

    private val Long.timeOffset: Long
        get() {
            return if (this <= 0) 0
            else this - 1
        }


    private var timerTracker: Job? = null

    inline fun startCoroutineTimer(
        crossinline action: () -> Unit
    ) = launch {
        while (true) {
            action()
            delay(1000)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    override fun onEvent(event: ActiveGameEvent) {
        when (event) {
            is ActiveGameEvent.OnInput -> onInput(
                event.input,
                viewModel.timerState
            )

            ActiveGameEvent.OnNewGameClicked -> onNewGameClicked()
            ActiveGameEvent.OnStart -> onStart()
            ActiveGameEvent.OnStop -> onStop()
            is ActiveGameEvent.OnTileFocused -> onTileFocused(event.x, event.y)
        }

    }

    private fun onTileFocused(x: Int, y: Int) {
        viewModel.updateFocusState(x, y)
    }

    private fun onStop() {
        if (viewModel.isCompleteState) {
            cancelStuff()
        } else {
            launch {
                gameRepo.saveGame(
                    viewModel.timerState.timeOffset,
                    { cancelStuff() },
                    {
                        cancelStuff()
                        container?.showError()
                    }
                )
            }
        }
    }

    private fun cancelStuff() {
        if (timerTracker?.isCancelled == false) timerTracker?.cancel()
        jobTracker.cancel()
    }

    private fun onStart() =
        launch {
            gameRepo.getCurrentGame(
                { puzzle, isComplete ->
                    viewModel.initializeBoardState(
                        puzzle, isComplete
                    )
                    if (!isComplete) timerTracker = startCoroutineTimer {
                        viewModel.updateTimerState()
                    }
                }, {
                    container?.onNewGameClick()
                }
            )
        }

    private fun onNewGameClicked() = launch {
        viewModel.showLoadingState()

        if (viewModel.isCompleteState) {
            navigateToNewGame()
        } else {
            gameRepo.getCurrentGame(
                { puzzle, _ -> updateWithTime(puzzle) },
                { container?.showError() }
            )
        }
    }

    private fun updateWithTime(puzzle: SudokuPuzzle) = launch {
        gameRepo.updateGame(
            puzzle.copy(elapsedTime = viewModel.timerState.timeOffset),
            { navigateToNewGame() },
            {
                navigateToNewGame()
                container?.showError()
            })
    }

    private fun navigateToNewGame() {
        cancelStuff()
        container?.onNewGameClick()
    }

    private fun onInput(input: Int, elapsedTime: Long) = launch {
        var focusedTile: SudokuTile? = null

        viewModel.boardState.values.forEach {
            if (it.hasFocus) focusedTile = it
        }

        if (focusedTile != null) {
            gameRepo.updateNode(
                focusedTile!!.x,
                focusedTile!!.y,
                input,
                elapsedTime,
                { isComplete ->
                    focusedTile?.let {
                        viewModel.updateBoardState(
                            it.x,
                            it.y,
                            input,
                            false
                        )
                    }

                    if (isComplete) {
                        timerTracker?.cancel()
                        checkIfNewRecord()
                    }
                },
                { container?.showError() }
            )
        }
    }

    private fun checkIfNewRecord() = launch {
        statsRepo.updateStatistic(
            viewModel.timerState,
            viewModel.difficulty,
            viewModel.boundary,
            { isRecord ->
                viewModel.isNewRecordState = isRecord
                viewModel.updateCompleteState()
            },
            {
                container?.showError()
                viewModel.updateCompleteState()
            }
        )
    }
}