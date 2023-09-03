package com.lasithaprabodha.sudoku.ui.activegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.lasithaprabodha.sudoku.R
import com.lasithaprabodha.sudoku.common.makeToast
import com.lasithaprabodha.sudoku.ui.SudokuTheme
import com.lasithaprabodha.sudoku.ui.activegame.buildlogic.buildActiveGameLogic
import com.lasithaprabodha.sudoku.ui.newgame.NewGameActivity

class  ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {
    private lateinit var logic: ActiveGameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel();

        setContent {
            SudokuTheme {
                ActiveGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }

    override fun showError() = makeToast(getString(R.string.generic_error))

    override fun onNewGameClick() {
      startActivity(Intent(this, NewGameActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)

        finish()
    }
}