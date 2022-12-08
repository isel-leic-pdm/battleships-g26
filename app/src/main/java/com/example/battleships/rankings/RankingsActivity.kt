package com.example.battleships.rankings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.ErrorMessage
import com.example.battleships.info.InfoActivity
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.RefreshingState

class RankingsActivity : ComponentActivity() {

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, RankingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private val useCases by lazy {
        (application as DependenciesContainer).useCases
    }

    @Suppress("UNCHECKED_CAST")
    val vm by viewModels<RankingsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RankingsViewModel(useCases) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.loadRankings()
        setContent {
            val gameRankingResult = vm.rankings
            val refreshingState: RefreshingState =
                if (vm.isLoading) RefreshingState.Refreshing
                else RefreshingState.Idle

            // if null, displays empty list of rankings
            if (gameRankingResult == null || gameRankingResult.isSuccess) {
                RankingsScreen(
                    gameRankingResult?.getOrNull(),
                    refreshingState,
                    onRefresh = vm::loadRankings,
                    NavigationHandlers(
                        onInfoRequested = { InfoActivity.navigate(origin = this) },
                        onBackRequested = { finish() }
                    )
                )
            }
            else {
                ErrorMessage(
                    onNonError = { vm.rankings?.getOrThrow() },
                    onIoExceptionDismiss = { vm.loadRankings() },
                    onApiExceptionDismiss = { finishAndRemoveTask() }
                )
            }
        }
    }
}