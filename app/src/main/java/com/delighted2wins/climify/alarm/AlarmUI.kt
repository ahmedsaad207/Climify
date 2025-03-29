package com.delighted2wins.climify.alarm

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.getCountryNameFromCode
import com.delighted2wins.climify.worker.WeatherAlarmWorker


@Composable
fun AlarmUI() {
    Log.i("TAG", "AlarmUI")

    val context = LocalContext.current

    LazyColumn {



    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (!Settings.canDrawOverlays(context)) {
                    requestOverlayPermission(context)
//                        requestLocationPermissions()
                } else {
                    scheduleDialog(context)
                }

            }
        ) {
            Text("Run Alarm")
        }
    }


}

/*@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimePickerExample() {
    var time by remember { mutableStateOf(LocalTime.now()) }
    var isDialogOpen by remember { mutableStateOf(false) }

    // Display the time in a formatted way
    Text("Selected Time: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}")

    // Open the dialog when clicked
    Button(onClick = { isDialogOpen = true }) {
        Text("Pick Time")
    }

    // Show time picker dialog
    if (isDialogOpen) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                time = LocalTime.of(hour, minute)
            },
            onDismissRequest = { isDialogOpen = false }
        )
    }
}*/

/*@Composable
fun TimePickerDialog(onTimeSelected: (Int, Int) -> Unit, onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            onTimeSelected(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    DisposableEffect(Unit) {
        timePickerDialog.show()
        onDispose {
            onDismissRequest()
        }
    }
}*/

/*@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateTimePicker() {
    val context = LocalContext.current
    var date by remember { mutableStateOf(LocalDate.now()) }
    var time by remember { mutableStateOf(LocalTime.now()) }
    var isDatePickerOpen by remember { mutableStateOf(false) }
    var isTimePickerOpen by remember { mutableStateOf(false) }

    Text("Selected Date: ${date.format(DateTimeFormatter.ISO_DATE)}")
    Text("Selected Time: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))}")

    Button(onClick = { isDatePickerOpen = true }) {
        Text("Pick Date")
    }

    Button(onClick = { isTimePickerOpen = true }) {
        Text("Pick Time")
    }

//    if (isDatePickerOpen) {
//        DatePickerDialog(
//            onDateSelected = { newDate ->
//                date = newDate
//                isDatePickerOpen = false
//            },
//            onDismissRequest = { isDatePickerOpen = false }
//        )
//    }

    if (isTimePickerOpen) {
        TimePickerDialog(
            onTimeSelected = { hour, minute ->
                time = LocalTime.of(hour, minute)
                isTimePickerOpen = false
            },
            onDismissRequest = { isTimePickerOpen = false }
        )
    }
}*/

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable // good
fun CustomTimePicker() {
    val context = LocalContext.current
    val timeState = rememberTimePickerState(is24Hour = false)
    val showDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { showDialog.value = true }) {
            Text(text = "Set Time")
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog.value = false
                        val hour = timeState.hour
                        val minute = timeState.minute
                        Toast.makeText(context, "Time set: $hour:$minute", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Text("OK")
                    }
                },
                text = {
                    TimePicker(state = timeState)
                }
            )
        }
    }
}*/


// way 3 - wheel picker
/*@Composable
fun WheelTimePicker() {
    val hours = (1..12).toList()
    val minutes = (0..59).toList()
    val amPmList = listOf("AM", "PM")

    var selectedHour by remember { mutableStateOf(1) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedAmPm by remember { mutableStateOf("AM") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NumberPicker( // hours
            value = selectedHour,
            range = hours,
            onValueChange = { selectedHour = it },
            label = "Hour"
        )
        Text(
            text = ":",
            fontSize = 32.sp,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.White
        )
        NumberPicker(
            value = selectedMinute,
            range = minutes,
            onValueChange = { selectedMinute = it },
            label = "Min"
        )
        Spacer(Modifier.width(12.dp))
        NumberPicker(
            value = selectedAmPm,
            range = amPmList,
            onValueChange = { selectedAmPm = it },
            label = ""
        )
    }
}*/

@Composable
fun <T> NumberPicker(value: T, range: List<T>, onValueChange: (T) -> Unit, label: String) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = range.indexOf(value))

    LaunchedEffect(value) {
        listState.scrollToItem(range.indexOf(value))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Box(
            modifier = Modifier
                .height(150.dp)
                .width(70.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .padding(8.dp)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(range) { item ->
                    Text(
                        text = item.toString(),
                        fontSize = if (item == value) 32.sp else 18.sp,
                        color = if (item == value) Color.Black else Color.Gray,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .clickable { onValueChange(item) }
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

fun scheduleDialog(context: Context) {
    val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<WeatherAlarmWorker>()
        .setInputData(
            workDataOf(
                Constants.KEY_LAT to 31.252321,
                Constants.KEY_LON to 29.992283,
                Constants.KEY_TEMP_UNIT to "metric",
                Constants.KEY_LANG to "en",
            )
        )
//        .setInitialDelay(5, TimeUnit.SECONDS)
        .setConstraints(constraints)
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






