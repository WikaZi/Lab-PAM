package lab06

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lab06.ui.theme.Lab06Theme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import lab06.viewmodel.FormViewModel
import lab06.viewmodel.ListViewModel
import lab06.viewmodel.TodoTaskForm
import lab06.viewmodel.TodoTaskUiState
import java.time.Instant
import java.time.ZoneId
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.isGranted


const val notificationID = 121
const val channelID = "Lab06 channel"
const val titleExtra = "title"
const val messageExtra = "message"

class MainActivity6 : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val container = TodoApplication.container
        scheduleAlarm(2_000)
        setContent {
            Lab06Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(notificationHandler = container.notificationHandler)
                }
            }
        }
    }
    private fun createNotificationChannel() {
        val name = "Lab06 channel"
        val descriptionText = "Lab06 is channel for notifications for approaching tasks."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID , name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    fun scheduleAlarm(time: Long){
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java)
        intent.putExtra(titleExtra, "Deadline")
        intent.putExtra(messageExtra, "Zbliża się termin zakończenia zadania")

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(notificationHandler: NotificationHandler) {
    val navController = rememberNavController()
    //
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(key1 = true) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    //
    NavHost(navController = navController, startDestination = "list") {
        composable(route = "list") { ListScreen(navController = navController, notificationHandler = notificationHandler) }
        composable("form") { FormScreen(navController = navController, notificationHandler = notificationHandler) }
    }
}

@Composable
fun ListScreen(
    navController: NavController,
    notificationHandler: NotificationHandler,
    viewModel: ListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val listUiState by viewModel.listUiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add task",
                        modifier = Modifier.scale(1.5f)
                    )
                },
                onClick = {
                    navController.navigate("form")
                }
            )
        },
        topBar = {
            AppTopBar(
                navController = navController,
                title = "List",
                showBackIcon = false,
                route = "form",
                notificationHandler = notificationHandler
            )
        },
        content = { it ->
            LazyColumn(
                modifier = Modifier.padding(it)
            ) {
                items(items = listUiState.items, key = { it.id })  { task ->
                    ListItem(
                        item = task,
                        onDelete = { viewModel.deleteItem(task) }
                    )
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    navController: NavController,
    notificationHandler: NotificationHandler,
    viewModel: FormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val todoTaskUiState = viewModel.todoTaskUiState

    Scaffold(
        topBar = {
            AppTopBar(
                navController = navController,
                title = "Formularz",
                showBackIcon = true,
                route = "list",
                onSaveClick = {
                    coroutineScope.launch {
                        viewModel.save()
                        navController.navigate("list")
                    }
                },
                notificationHandler = notificationHandler
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.save()
                        navController.navigate("list")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                enabled = todoTaskUiState.isValid
            ) {
                Text("Zapisz zadanie")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextField(
                value = todoTaskUiState.todoTask.title,
                onValueChange = { viewModel.updateUiState(todoTaskUiState.todoTask.copy(title = it)) },
                label = { Text("Tytuł zadania") },
                modifier = Modifier.fillMaxWidth()
            )

            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = todoTaskUiState.todoTask.deadline
            )
            var isDatePickerOpen by remember { mutableStateOf(false) }

            OutlinedButton(onClick = { isDatePickerOpen = true }) {
                Text("Wybierz datę: ${todoTaskUiState.todoTask.deadline?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() } ?: "brak"}")
            }

            if (isDatePickerOpen) {
                DatePickerDialog(
                    onDismissRequest = { isDatePickerOpen = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    viewModel.updateUiState(todoTaskUiState.todoTask.copy(deadline = millis))
                                }
                                isDatePickerOpen = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { isDatePickerOpen = false }) {
                            Text("Anuluj")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Text("Priorytet:")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.values().forEach { p ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = todoTaskUiState.todoTask.priority == p.name,
                            onClick = { viewModel.updateUiState(todoTaskUiState.todoTask.copy(priority = p.name)) }
                        )
                        Text(p.name)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = todoTaskUiState.todoTask.isDone,
                    onCheckedChange = { viewModel.updateUiState(todoTaskUiState.todoTask.copy(isDone = it)) }
                )
                Text("Zadanie zakończone")
            }
        }
    }
}

@Composable
fun TodoTaskInputBody(
    todoUiState: TodoTaskUiState,
    onItemValueChange: (TodoTaskForm) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TodoTaskInputForm(
            item = todoUiState.todoTask,
            onValueChange = onItemValueChange,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoTaskInputForm(
    item: TodoTaskForm,
    modifier: Modifier = Modifier,
    onValueChange: (TodoTaskForm) -> Unit = {},
    enabled: Boolean = true
) {
    Text("Tytuł zadania")
    TextField(
        value = item.title,
        onValueChange = {
            onValueChange(item.copy(title = it))
        })

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        yearRange = IntRange(2000, 2030),
        initialSelectedDateMillis = item.deadline,
    )

    var showDialog by remember {
        mutableStateOf(false)
    }

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { showDialog = true }),
        text = "Date",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.headlineMedium
    )

    if (showDialog) {
        DatePickerDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onValueChange(item.copy(deadline = datePickerState.selectedDateMillis!!))
                }) {
                    Text("Pick")
                }
            }
        ) {
            DatePicker(state = datePickerState, showModeToggle = true)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    navController: NavController,
    title: String,
    showBackIcon: Boolean,
    route: String,
    onSaveClick: () -> Unit = { },
    notificationHandler: NotificationHandler
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        ),
        title = { Text(text = title) },
        navigationIcon = {
            if (showBackIcon) {
                IconButton(onClick = { navController.navigate(route) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (route != "form") {
                OutlinedButton(
                    onClick = onSaveClick
                ) {
                    Text(
                        text = "Zapisz",
                        fontSize = 18.sp
                    )
                }
            } else {
                IconButton(onClick = {
                notificationHandler.showSimpleNotification()
                }){
                    Icon(imageVector = Icons.Default.Notifications, contentDescription = "Notifications")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                }
            }
        }
    )
}



