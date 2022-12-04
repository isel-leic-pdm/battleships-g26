package com.example.battleships.rankings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.ErrorMessage
import com.example.battleships.info.InfoActivity
import com.example.battleships.services.ApiException
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.utils.ErrorAlert
import pt.isel.battleships.R
import java.io.IOException

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
        val rankings = vm.rankings
        setContent {
            if (rankings != null) {
                if (rankings.isFailure) RankingsErrorMessage()
                else {
                    val rankingsList = rankings.getOrNull() ?: return@setContent
                    RankingsScreen(
                        rankingsList,
                        onRefreshRequested = vm::loadRankings,
                        NavigationHandlers(
                            onInfoRequested = { InfoActivity.navigate(origin = this) },
                            onBackRequested = { finish() }
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun RankingsErrorMessage() {
        ErrorMessage(
            onNonError = { vm.rankings?.getOrThrow() },
            onIoExceptionDismiss = { vm.loadRankings() },
            onApiExceptionDismiss = { finishAndRemoveTask() }
        )
    }
}