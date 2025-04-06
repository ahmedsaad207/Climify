package com.delighted2wins.climify.features.alarm

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.delighted2wins.climify.R
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.domainmodel.Response
import com.delighted2wins.climify.features.favorite.FavoriteLocationItem
import com.delighted2wins.climify.features.home.components.LoadingIndicator
import com.delighted2wins.climify.features.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.checkAndRequestPostNotificationPermission
import com.delighted2wins.climify.utils.formatDuration
import com.delighted2wins.climify.utils.requestOverlayPermission
import com.delighted2wins.climify.utils.toFormat
import com.delighted2wins.climify.workers.WeatherAlarmWorker
import com.google.gson.Gson
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit


@Composable
fun AlarmUI(snackBarHostState: SnackbarHostState, onSetAlarm: (() -> Unit) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: AlarmViewModel = viewModel(factory = AlarmViewModelFactory(getRepo(context)))
    val message = remember { MutableSharedFlow<String>(replay = 1) }
    val selectedOption = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getAllAlarms()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(message) {
        message.collect {
            if (it.isNotBlank()) {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message = it, duration = SnackbarDuration.Short
                )
            }
        }
    }

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MINUTE, 1)
            var startDate by remember { mutableLongStateOf(calendar.timeInMillis) }
            calendar.add(Calendar.MINUTE, 2)
            var endDate by remember { mutableLongStateOf(calendar.timeInMillis) }
            val alarms = (uiState as Response.Success).data
            val animationDuration = 500
            val redColor = Color(0xFFFF7F7F)
            val greenColor = Color(0xFFA5D6A7)
            val startDurationLabelColorState = remember { mutableStateOf(greenColor) }
            val endDurationLabelColorState = remember { mutableStateOf(greenColor) }

            onSetAlarm {
                if (startDate <= System.currentTimeMillis() || endDate <= startDate || endDate <= System.currentTimeMillis()) {
                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.please_select_time_in_future),
                            duration = SnackbarDuration.Short
                        )
                    }

                    if (startDate <= System.currentTimeMillis()) {
                        startDurationLabelColorState.value = redColor
                    } else {
                        startDurationLabelColorState.value = greenColor
                    }

                    if (endDate <= System.currentTimeMillis() || endDate <= startDate) {
                        endDurationLabelColorState.value = redColor
                    } else {
                        endDurationLabelColorState.value = greenColor
                    }
                } else if (selectedOption.value.isBlank()) {
                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.please_select_type_of_alarm),
                            duration = SnackbarDuration.Short
                        )
                    }
                } else {

                    val durationInMillis = (endDate - startDate)
                    val alarm = Alarm(
                        System.currentTimeMillis().toString(),
                        startDate,
                        durationInMillis,
                        Constants.TYPE_NOTIFICATION
                    )
                    alarm.type = selectedOption.value
                    scheduleAlarmWork(context, alarm)
                    viewModel.insertAlarm(alarm)

                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = context.getString(R.string.alarm_created_successfully),
                            duration = SnackbarDuration.Short
                        )
                    }

                }
            }

            LazyColumn {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.alarm),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0B0B0),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2A2B26),
                                        Color(0xFF20211D)
                                    )
                                ), shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp)
                            .shadow(
                                4.dp, shape = RoundedCornerShape(12.dp)
                            )
                            .wrapContentSize(Alignment.Center)
                    )
                }

                item {
                    Text(
                        text = stringResource(R.string.set_your_alarm),
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                item {
                    val onStartDurationClicked = remember {
                        object : MutableInteractionSource {
                            override val interactions = MutableSharedFlow<Interaction>(
                                extraBufferCapacity = 16,
                                onBufferOverflow = BufferOverflow.DROP_OLDEST,
                            )

                            override suspend fun emit(interaction: Interaction) {
                                if (interaction is PressInteraction.Release) {
                                    getDateTime(context) { startDurationInMillis ->
                                        startDate = startDurationInMillis
                                    }
                                }
                                interactions.emit(interaction)
                            }

                            override fun tryEmit(interaction: Interaction): Boolean {
                                return interactions.tryEmit(interaction)
                            }
                        }
                    }
                    val onEndDurationClicked = remember {
                        object : MutableInteractionSource {
                            override val interactions = MutableSharedFlow<Interaction>(
                                extraBufferCapacity = 16,
                                onBufferOverflow = BufferOverflow.DROP_OLDEST,
                            )

                            override suspend fun emit(interaction: Interaction) {
                                if (interaction is PressInteraction.Release) {
                                    getDateTime(context) { endDurationInMillis ->
                                        endDate = endDurationInMillis
                                    }
                                }
                                interactions.emit(interaction)
                            }

                            override fun tryEmit(interaction: Interaction): Boolean {
                                return interactions.tryEmit(interaction)
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(Color(0xFF2A2B26), Color(0xFF1E1F1C))
                                    ), shape = RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = "${
                                            startDate.toFormat("EEE, dd MMM")
                                        }\n${startDate.toFormat("h:mm a")}",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.start_duration)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AlarmOn,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        interactionSource = onStartDurationClicked,
                                        colors = TextFieldDefaults.colors(
                                            unfocusedTextColor = Color.White,
                                            focusedTextColor = Color.White,
                                            unfocusedIndicatorColor = Color.DarkGray,
                                            focusedIndicatorColor = Color.Gray,
                                            cursorColor = Color.White,
                                            unfocusedLabelColor = startDurationLabelColorState.value,
                                            focusedLabelColor = startDurationLabelColorState.value,
                                            focusedContainerColor = colorResource(R.color.deep_gray),
                                            unfocusedContainerColor = colorResource(R.color.deep_gray)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = "${
                                            endDate.toFormat("EEE, dd MMM")
                                        }\n${endDate.toFormat("h:mm a")}",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(stringResource(R.string.end_duration)) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.AlarmOff,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.padding(end = 8.dp)
                                            )
                                        },
                                        interactionSource = onEndDurationClicked,
                                        colors = TextFieldDefaults.colors(
                                            unfocusedTextColor = Color.White,
                                            focusedTextColor = Color.White,
                                            unfocusedIndicatorColor = Color.DarkGray,
                                            focusedIndicatorColor = Color.Gray,
                                            cursorColor = Color.White,
                                            unfocusedLabelColor = endDurationLabelColorState.value,
                                            focusedLabelColor = endDurationLabelColorState.value,
                                            focusedContainerColor = colorResource(R.color.deep_gray),
                                            unfocusedContainerColor = colorResource(R.color.deep_gray)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp)
                                    )
                                }
                                RadioButtonGroup(selectedOption)
                            }
                        }

                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.alarm),
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
                if (alarms.isNotEmpty()) {

                    items(items = alarms, key = { it.tag }) { alarm ->

                        if (alarm.startDuration < System.currentTimeMillis()) {
                            alarm.isChecked = false
                            viewModel.insertAlarm(alarm)
                        }

                        val dismissState =
                            rememberSwipeToDismissBoxState(confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    WorkManager.getInstance(context).cancelAllWorkByTag(alarm.tag)
                                    viewModel.deleteAlarm(alarm)
                                    if (alarm.startDuration > System.currentTimeMillis()) {

                                        scope.launch {
                                            snackBarHostState.currentSnackbarData?.dismiss()
                                            val result = snackBarHostState.showSnackbar(
                                                message = context.getString(R.string.alarm_deleted_successfully),
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                viewModel.insertAlarm(alarm)
                                                scheduleAlarmWork(
                                                    context, alarm
                                                )
                                            }
                                        }
                                    }
                                }
                                true
                            })

                        AnimatedVisibility(
                            visible = dismissState.currentValue != SwipeToDismissBoxValue.EndToStart,
                            exit = shrinkVertically(
                                shrinkTowards = Alignment.Top, animationSpec = tween(
                                    durationMillis = animationDuration, easing = FastOutSlowInEasing
                                )
                            ) + fadeOut(animationSpec = tween(durationMillis = animationDuration)),
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 }, animationSpec = tween(
                                    durationMillis = 500, easing = LinearOutSlowInEasing
                                )
                            ) + fadeIn(animationSpec = tween(durationMillis = 300))
                        ) {
                            SwipeToDismissBox(state = dismissState,
                                enableDismissFromStartToEnd = false,
                                backgroundContent = {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(horizontal = 24.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.delete),
                                            tint = Color.White
                                        )
                                    }
                                }) {

                                ReminderItem(
                                    message, alarm
                                ) {
                                    alarm.isChecked = it
                                    viewModel.insertAlarm(alarm)

                                    if (it) {
                                        scheduleAlarmWork(context, alarm)
                                    } else {
                                        WorkManager.getInstance(context)
                                            .cancelAllWorkByTag(alarm.tag)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(200.dp))
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.sleeping
                                )
                            )
                            val progress by animateLottieCompositionAsState(
                                composition = composition,
                                iterations = LottieConstants.IterateForever
                            )
                            LottieAnimation(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                composition = composition,
                                progress = { progress },
                            )
                            Text(
                                text = stringResource(R.string.no_alarms_found),
                                fontSize = 18.sp,
                                color = colorResource(R.color.neutral_gray),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
        }

        is Response.Failure -> {
            val error = (uiState as Response.Failure).error
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cloud_error))
            val progress by animateLottieCompositionAsState(
                composition = composition, iterations = 1
            )
            Column {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.favorite_locations),
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(R.color.deep_gray)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        composition = composition,
                        progress = { progress },
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = error,
                        fontSize = 24.sp,
                        color = colorResource(R.color.neutral_gray),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherOverlay(
    currentWeather: CurrentWeather?, onDismiss: () -> Unit, onOpen: () -> Unit
) {
    currentWeather?.let {
        Column {
            FavoriteLocationItem(currentWeather, {}, currentWeather.unit)

            Row(
                modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onDismiss() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFB71C1C)),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = { onOpen() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF388E3C)),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = stringResource(R.string.open)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.open))
                }
            }
        }
    }
}

fun scheduleAlarmWork(
    context: Context, alarm: Alarm
) {
    val initial = calculateInitialDelay(alarm.startDuration)
    val workRequest =
        OneTimeWorkRequestBuilder<WeatherAlarmWorker>().addTag(alarm.tag).setInputData(
            workDataOf(
                Constants.KEY_ALARM to Gson().toJson(alarm)
            )
        ).setInitialDelay(initial, TimeUnit.SECONDS).build()

    WorkManager.getInstance(context).enqueue(workRequest)
}

fun getDateTime(context: Context, onDateTimeSelected: (Long) -> Unit) {

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(
        context, { _, y, m, d ->
            calendar.set(y, m, d)

            TimePickerDialog(
                context, { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    onDateTimeSelected(calendar.timeInMillis)
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE) + 1, false
            ).show()

        }, year, month, day
    )
    datePicker.datePicker.minDate = System.currentTimeMillis()
    datePicker.show()
}

fun calculateInitialDelay(selectedTimeInMillis: Long): Long {
    val currentTimeMillis = System.currentTimeMillis()
    return if (selectedTimeInMillis > currentTimeMillis) {
        (selectedTimeInMillis - currentTimeMillis) / 1000
    } else {
        0
    }
}

@Composable
fun ReminderItem(
    message: MutableSharedFlow<String>, alarm: Alarm, onToggle: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val date = alarm.startDuration.toFormat("EEEE, MMMM d")
    val time = alarm.startDuration.toFormat("HH:mm")
    val pmAm = alarm.startDuration.toFormat("a")
    val isChecked = alarm.isChecked
    val duration = "${alarm.type} last for ${(alarm.endDuration / 1000).formatDuration()}"
    Log.i("TAG", "ReminderItem: duration = $duration")
    var isCheckedState by remember { mutableStateOf(isChecked) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2A2B26), Color(0xFF1E1F1C))
                    ), shape = RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = date,
                        fontSize = 14.sp,
                        color = Color(0xFF808080),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = time,
                            fontSize = 28.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pmAm,
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (pmAm.equals(
                                    "PM", ignoreCase = true
                                )
                            ) Icons.Outlined.WbSunny
                            else Icons.Outlined.DarkMode,
                            tint = if (pmAm.equals("PM", ignoreCase = true)) Color(0xFFFFC107)
                            else Color(0xFF2196F3),
                            contentDescription = null
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = duration,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }

                Switch(checked = isCheckedState, onCheckedChange = {
                    if (alarm.startDuration > System.currentTimeMillis()) {
                        onToggle(it)
                        isCheckedState = it
                    } else {
                        message.tryEmit(context.getString(R.string.you_can_t_enable_past_alarm))
                    }
                }, thumbContent = if (isChecked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }, enabled = true)
            }
        }
    }
}


@Composable
fun RadioButtonGroup(selectedOption: MutableState<String>) {
    val context = LocalContext.current
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedOption.value == Constants.TYPE_OVERLAY, onClick = {
                if (!Settings.canDrawOverlays(context)) {
                    context.requestOverlayPermission()
                } else {
                    selectedOption.value = Constants.TYPE_OVERLAY
                }
            })
            Text(stringResource(R.string.alarm_popup), color = Color.White)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selectedOption.value == Constants.TYPE_NOTIFICATION, onClick = {
                checkAndRequestPostNotificationPermission(context) {
                    selectedOption.value = Constants.TYPE_NOTIFICATION
                }
            })
            Text(stringResource(R.string.notification), color = Color.White)
        }
    }
}


