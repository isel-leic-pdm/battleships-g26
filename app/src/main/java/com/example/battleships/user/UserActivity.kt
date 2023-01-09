package com.example.battleships.user

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.TAG
import com.example.battleships.utils.ErrorAlert
import com.example.battleships.utils.getWith
import okhttp3.internal.wait
import pt.isel.battleships.R


class UserActivity : ComponentActivity() {

    companion object {
        val USER_ID : String? = null
        fun navigate(origin: Activity, id : Int) {
            with(origin) {
                val intent = Intent(this, UserActivity::class.java)
                Log.e(TAG, "id = $id")
                intent.putExtra(USER_ID, id)
                startActivity(intent)
            }
        }
    }

    private val useCases by lazy {
        (application as DependenciesContainer).useCases
    }

    @Suppress("UNCHECKED_CAST")
    val vm by viewModels<UserViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserViewModel(useCases) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.getUserById(userId)
        setContent {
            val context = LocalContext.current
            val user = vm.user?.getWith(context)
            if(user != null) UserScreen(user) { finish() }
            /*
            else ErrorAlert(
                title = R.string.error_api_title,
                message = R.string.error_could_not_reach_api,
                rightButtonText = R.string.error_exit_button_text,
                onRightButton = { finish() }
            )

             */
        }
    }

    private val userId: Int
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(USER_ID, Int::class.java) ?: TODO()
            else
                intent.getIntExtra(USER_ID,0)
}