package per.nullist.targetedcleaner.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import per.nullist.targetedcleaner.R

import per.nullist.targetedcleaner.view_model.AppListViewModel
import per.nullist.targetedcleaner.view_model.data.AppInfo
import per.nullist.targetedcleaner.view_model.event_handler.SafeAppsEventHandler

@ExperimentalMaterialApi
@Composable
fun AppInfoItem(
    app: AppInfo,
    isSafe: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        icon = {
            Image(
                bitmap = app.icon,
                contentDescription = "icon of ${app.name}",
                Modifier.size(50.dp)
            )
        },
        trailing = {
            Checkbox(isSafe, { onCheckedChange(!it) })
        },
        modifier = Modifier.clickable { onCheckedChange(!isSafe) }
    ) {
        Text(app.name)
    }
}

@ExperimentalMaterialApi
@Composable
fun AppList(
    apps: List<AppInfo>,
    safeApps: Set<AppInfo>,
    onChangeItemChecked: (Boolean, AppInfo) -> Unit
) {
    LazyColumn {
        items(apps) { app ->
            AppInfoItem(
                app,
                safeApps.contains(app)
            ) { isSafe ->
                onChangeItemChecked(isSafe, app)
            }
        }
    }
}

@Composable
fun Loading() {
    Box(
        Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            Modifier.align(Alignment.Center)
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun AppListActivityView(
    model: AppListViewModel,
    eventHandler: SafeAppsEventHandler
) {
    val apps by model.allInstalledApps.observeAsState()
    val safeApps by model.safeApps.observeAsState()

    MaterialTheme(
        typography = Typography(
            defaultFontFamily = FontFamily(
                Font(R.font.ibm_plex_sans_kr_light, FontWeight.Light)
            )
        )
    ) {
        Scaffold(
            topBar = {
                TopAppBar {
                    Text("제외할 앱 설정",
                        Modifier.padding(start = 20.dp),
                        fontSize = 20.sp
                    )
                }
            }
        ) {
            if((apps ?: listOf()).isEmpty()) {
                Loading()
            }
            else {
                AppList(
                    apps ?: listOf(),
                    safeApps ?: setOf()
                ) { isSafe, app ->
                    when {
                        isSafe -> eventHandler.add(app)
                        else -> eventHandler.remove(app)
                    }
                }
            }
        }
    }
}
