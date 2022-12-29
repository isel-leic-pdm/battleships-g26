package com.example.battleships.info

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.battleships.ui.CenteredTopAppBar
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.battleships.R

@Composable
fun InfoScreen(
    navigationHandlers: NavigationHandlers = NavigationHandlers(),
    onSendEmailRequested: () -> Unit = { },
    onOpenUrlRequested: (Uri) -> Unit = { },
){
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("InfoScreen"),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { CenteredTopAppBar(
                title = stringResource(R.string.info_screen_top_app_bar_text),
                navigationHandlers)
            },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Authors(
                    onSendEmailRequested,
                    onOpenUrlRequested
                )
            }
        }
    }
}


@Composable
fun Authors(onSendEmailRequested: () -> Unit = { },
            onOpenUrlRequested: (Uri) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .offset(y = (-60).dp)
    ) {
        Text(
            text = stringResource(R.string.info_screen_title_text),
            style = MaterialTheme.typography.h6
        )
        MySpacer()
        Developer(name = "António Carvalho", "https://github.com/ACRae", onOpenUrlRequested)
        Developer(name = "Pedro Silva", "https://github.com/psilva20019", onOpenUrlRequested)
        Developer(name = "Miguel Rocha", "https://github.com/MiguelRocha2001", onOpenUrlRequested)
        MySpacer(30.dp)
        Text(
            text = stringResource(R.string.info_screen_email_text),
            style = MaterialTheme.typography.h6
        )
        MySpacer()
        Icon(imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .clickable { onSendEmailRequested() }
                .size(60.dp)
        )
    }
}

@Composable
fun Developer(name : String, link : String = "", onOpenUrlRequested : (Uri) -> Unit){
    val uri = Uri.parse(link)
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .clickable { onOpenUrlRequested(uri) }
            .fillMaxWidth()
            .offset(x = 20.dp)
            .padding(10.dp)
    )
    {
        Image(
            painter = painterResource(id = R.drawable.github_logo),
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        MySpacer()
        Text(text = name, style = MaterialTheme.typography.h6)
    }
}

@Composable
fun MySpacer(size : Dp = 10.dp){
    Spacer(modifier = Modifier.size(size))
}

@Preview
@Composable
fun DeveloperPreview(){
    Developer(name = "Antonio") {}
}


@Preview
@Composable
fun AuthorPreview() {
    Authors{}
}

@Preview
@Composable
fun InfoScreenPreview(){
    InfoScreen()
}