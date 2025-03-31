package com.delighted2wins.climify.alarm

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.R
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.domainmodel.Alarm
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.getCountryNameFromCode
import com.delighted2wins.climify.utils.toFormat
import com.delighted2wins.climify.worker.WeatherAlarmWorker
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit


@Composable
fun AlarmUI(snackBarHostState: SnackbarHostState, onSetAlarm: (() -> Unit) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: AlarmViewModel = viewModel(factory = AlarmViewModelFactory(getRepo(context)))

    LaunchedEffect(Unit) {
        viewModel.getAllAlarms()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


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

            onSetAlarm {
                if (startDate <= System.currentTimeMillis() || endDate <= startDate || endDate <= System.currentTimeMillis()) {
                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = "Please Select time in Future",
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
                    scheduleAlarmWork(context, alarm)
                    viewModel.insertAlarm(alarm)

                    scope.launch {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = "Alarm Created successfully",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            LazyColumn {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Alarm",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0B0B0), // A softer gray for readability
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2A2B26), // Lighter grayish-green, adds softness
                                        Color(0xFF20211D)  // Slightly lighter deep gray
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp) // Inner padding for better aesthetics
                            .shadow(
                                4.dp,
                                shape = RoundedCornerShape(12.dp)
                            ) // Subtle shadow for depth
                            .wrapContentSize(Alignment.Center) // Centers text in the background
                    )
                }

                item {
                    Text(
                        text = "Set your Alarm",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Start Duration", color = Color.White)
                            Spacer(Modifier.width(24.dp))
                            Text(
                                "${
                                    startDate.toFormat("EEE, dd MMM")
                                }\n${startDate.toFormat("h:mm a")}", color = Color.White
                            )
                            Spacer(Modifier.width(24.dp))
                            Button(onClick = {
                                getDateTime(context) { startDurationInMillis ->
                                    startDate = startDurationInMillis
                                }
                            }) { Text("Set", color = Color.White) }
                        }
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("End Duration", color = Color.White)
                            Spacer(Modifier.width(24.dp))
                            Text(
                                "${
                                    endDate.toFormat("EEE, dd MMM")
                                }\n${endDate.toFormat("h:mm a")}", color = Color.White
                            )
                            Spacer(Modifier.width(24.dp))
                            Button(onClick = {
                                getDateTime(context) { endDurationInMillis ->


                                    endDate = endDurationInMillis
                                }
                            }) { Text("Set", color = Color.White) }
                        }

                        /*Button(
                            onClick = {
                                if (!Settings.canDrawOverlays(context)) {
                                    requestOverlayPermission(context)
        //                        requestLocationPermissions()
                                } else {
                                    scheduleAlarmWork(context)
                                }
                            }
                        ) {
                            Text("Run Alarm")
                        }*/
//                        Button(onClick = {
//                            if (startDate <= System.currentTimeMillis() || endDate <= startDate || endDate <= System.currentTimeMillis()) {
//                                scope.launch {
//                                    snackBarHostState.currentSnackbarData?.dismiss()
//                                    snackBarHostState.showSnackbar(
//                                        message = "Please Select time in Future",
//                                        duration = SnackbarDuration.Short
//                                    )
//                                }
//                            } else {
//                                val durationInMillis = (endDate - startDate)
//                                val alarm = Alarm(
//                                    System.currentTimeMillis().toString(),
//                                    startDate,
//                                    durationInMillis,
//                                    Constants.TYPE_NOTIFICATION
//                                )
//                                scheduleAlarmWork(context, alarm)
//                                viewModel.insertAlarm(alarm)
//
//                                scope.launch {
//                                    snackBarHostState.currentSnackbarData?.dismiss()
//                                    snackBarHostState.showSnackbar(
//                                        message = "Alarm Created successfully",
//                                        duration = SnackbarDuration.Short
//                                    )
//                                }
//                            }
//                        }) { Text("Add") }
                    }
                }

                item {
                    Text(
                        text = "Alarm",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
                if (alarms.isNotEmpty()) {
//                    items(alarms) {
//                        Text(
//                            it.tag,
//                            color = Color.White,
//                            modifier = Modifier.fillMaxWidth(),
//                            textAlign = TextAlign.Center,
//                            fontSize = 24.sp
//                        )
//                    }
                    // list
                    items(
                        items = alarms,
                        key = { it.tag }
                    ) { alarm ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    WorkManager.getInstance(context).cancelAllWorkByTag(alarm.tag)
                                    viewModel.deleteAlarm(alarm)

                                    scope.launch {
                                        snackBarHostState.currentSnackbarData?.dismiss()
                                        val result = snackBarHostState.showSnackbar(
                                            message = "Alarm deleted successfully",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.insertAlarm(alarm)
                                            scheduleAlarmWork(
                                                context,
                                                alarm
                                            )
                                        }
                                    }
                                }
                                true
                            }
                        )

                        AnimatedVisibility(
                            visible = dismissState.currentValue != SwipeToDismissBoxValue.EndToStart,
                            exit = shrinkVertically(
                                shrinkTowards = Alignment.Top,
                                animationSpec = tween(
                                    durationMillis = animationDuration,
                                    easing = FastOutSlowInEasing
                                )
                            ) + fadeOut(animationSpec = tween(durationMillis = animationDuration)),
                            enter = slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = LinearOutSlowInEasing
                                )
                            ) + fadeIn(animationSpec = tween(durationMillis = 300))
                        ) {
                            SwipeToDismissBox(
                                state = dismissState,
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
                                            contentDescription = "Delete",
                                            tint = Color.White
                                        )
                                    }
                                }
                            ) {
                                Text(
                                    alarm.tag,
                                    color = Color.White,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(180.dp))
                    }
                } else { // TODO add lottie
                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.empty2
                                )
                            )
                            val progress by animateLottieCompositionAsState(
                                composition = composition,
                                iterations = 1
                            )
                            LottieAnimation(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                composition = composition,
                                progress = { progress },
                            )
                            Text(
                                text = stringResource(R.string.no_favorite_locations_found_add_some_to_get_started),
                                fontSize = 18.sp,
                                color = colorResource(R.color.neutral_gray),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }
            }
            /*LazyColumn {
                item {
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = "Alarm",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB0B0B0), // A softer gray for readability
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2A2B26), // Lighter grayish-green, adds softness
                                        Color(0xFF20211D)  // Slightly lighter deep gray
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 12.dp) // Inner padding for better aesthetics
                            .shadow(4.dp, shape = RoundedCornerShape(12.dp)) // Subtle shadow for depth
                            .wrapContentSize(Alignment.Center) // Centers text in the background
                    )
                }

                item {
                    Text(
                        text = "Set your Alarm",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Start Duration", color = Color.White)
                            Spacer(Modifier.width(24.dp))
                            Text("Sun, 30 Mar\n7:02 PM", color = Color.White)
                            Spacer(Modifier.width(24.dp))
                            Button(onClick = {
                                getDateTime(context) {startDurationInMillis ->
                                    startDate = startDurationInMillis
                                }
                            }) { Text("Set", color = Color.White) }
                        }
                        Spacer(Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("End Duration", color = Color.White)
                            Spacer(Modifier.width(24.dp))
                            Text("Sun, 30 Mar\n7:03 PM", color = Color.White)
                            Spacer(Modifier.width(24.dp))
                            Button(onClick = {
                                getDateTime(context) {endDurationInMillis ->
                                    endDate = endDurationInMillis
                                }
                            }) { Text("Set", color = Color.White) }
                        }
                        */
            /*getDateTime { startMillis, endMillis ->
                                val durationInSeconds = (endMillis - startMillis)

                                val alarm = Alarm(
                                    System.currentTimeMillis().toString(),
                                    startMillis,
                                    durationInSeconds,
                                    Constants.TYPE_NOTIFICATION
                                )
                                scheduleAlarmWork(context, alarm)
                                viewModel.insertAlarm(alarm)
                            }*//*
                            *//*Button(
                                onClick = {
                                    if (!Settings.canDrawOverlays(context)) {
                                        requestOverlayPermission(context)
            //                        requestLocationPermissions()
                                    } else {
                                        scheduleAlarmWork(context)
                                    }
                                }
                            ) {
                                Text("Run Alarm")
                            }*/
            /*
                            Button(onClick = {
                                val durationInMillis = (endDate!! - startDate!!)

                                val alarm = Alarm(
                                    System.currentTimeMillis().toString(),
                                    startDate!!,
                                    durationInMillis,
                                    Constants.TYPE_NOTIFICATION
                                )
                                scheduleAlarmWork(context, alarm)
                                viewModel.insertAlarm(alarm)
                            }) { Text("Add") }
                        }
                    }

                    item {
                        Text(
                            text = "Alarm",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp)
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LottieAnimation(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                composition = composition,
                                progress = { progress },
                            )
                            Text(
                                text = stringResource(R.string.no_favorite_locations_found_add_some_to_get_started),
                                fontSize = 18.sp,
                                color = colorResource(R.color.neutral_gray),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    }
                }*/
        }

        is Response.Failure -> { //TODO lottie,
            val error = (uiState as Response.Failure).error
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.cloud_error))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1
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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherOverlay(
    currentWeather: CurrentWeather?,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(Color.Black.copy(alpha = 0f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Reminder", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "" +
                            "country: ${currentWeather?.country?.getCountryNameFromCode() ?: "country is null"}, " +
                            "\ncity: ${currentWeather?.city}" +
                            "\ndescription: ${currentWeather?.description}"
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
                GlideImage(
                    modifier = Modifier.size(48.dp),
                    model = currentWeather?.icon,
                    contentDescription = null
                )
            }
        }
    }
}

fun scheduleAlarmWork(
    context: Context,
    alarm: Alarm
) {
    val initial = calculateInitialDelay(alarm.startDuration)
    val workRequest = OneTimeWorkRequestBuilder<WeatherAlarmWorker>()
        .addTag(alarm.tag)
        .setInputData(
            workDataOf(
                Constants.KEY_ALARM to Gson().toJson(alarm)
            )
        )
        .setInitialDelay(initial, TimeUnit.SECONDS)
        .build()

    WorkManager.getInstance(context).enqueue(workRequest)
}

fun requestOverlayPermission(context: Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )
    context.startActivity(intent)
//    startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION)
}

/*private fun requestLocationPermissions() {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // 34+
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
//                android.Manifest.permission.FOREGROUND_SERVICE_LOCATION
        )

        if (permissions.any {
                checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            requestPermissions(this, permissions, 200)
        }
    } else {
        val permissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )

        if (permissions.any {
                checkSelfPermission(
                    this,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            requestPermissions(this, permissions, 200)
        }
    }
}*/

fun getDateTime(context: Context, onDateTimeSelected: (Long) -> Unit) {

    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            calendar.set(y, m, d)

            TimePickerDialog(
                context,
                { _, hour, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    onDateTimeSelected(calendar.timeInMillis)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE) + 1,
                false
            ).show()

        },
        year,
        month,
        day
    )
    datePicker.datePicker.minDate = System.currentTimeMillis()
    datePicker.show()
}


fun calculateInitialDelay(selectedTimeInMillis: Long): Long {
    val currentTimeMillis = System.currentTimeMillis()
    return if (selectedTimeInMillis > currentTimeMillis) {
        (selectedTimeInMillis - currentTimeMillis) / 1000 // Convert to seconds
    } else {
        0
    }
}








