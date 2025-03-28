package com.delighted2wins.climify.favorite

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.delighted2wins.climify.R
import com.delighted2wins.climify.Response
import com.delighted2wins.climify.domainmodel.CurrentWeather
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.getCountryNameFromCode
import com.delighted2wins.climify.utils.timeStampToHumanDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FavoriteUI(
    showBottomNabBar: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    showFloatingActionButton: MutableState<Boolean>,
    onNavigateToWeatherDetails: (Int) -> Unit
) {

    LaunchedEffect(Unit) {
        showBottomNabBar.value = true
        showFloatingActionButton.value = true
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: FavoriteViewModel =
        viewModel(factory = FavoriteViewModelFactory(getRepo(context)))
    LaunchedEffect(Unit) {
        viewModel.getFavoriteWeathers()
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val weathers = (uiState as Response.Success).data
            val weathersState = remember { mutableStateOf(weathers) }

            if (weathersState.value.isNotEmpty()) {    // show data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
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
                    }
                    items(
                        items = weathersState.value,
                        key = { it.id }
                    ) { weather ->
                        SwipeToDeleteContainer(
                            context = context,
                            item = weather,
                            onDelete = {
                                scope.launch {
                                    viewModel.deleteWeather(weather)
                                }
                            },
                            snackBarHostState = snackBarHostState
                        ) {
                            FavoriteLocationItem(weather, onNavigateToWeatherDetails)
                        }
                    }

                }
            } else {    // empty list
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty2))
                val progress by animateLottieCompositionAsState(
                    composition = composition,
                    iterations = 1
                )
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
        }

        is Response.Failure -> {
            val error = (uiState as Response.Failure).error
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.error3))
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
                            .height(300.dp),
                        composition = composition,
                        progress = { progress },
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        text = error,
                        fontSize = 18.sp,
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
fun FavoriteLocationItem(weather: CurrentWeather, onNavigateToWeatherDetails: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(colorResource(R.color.grayish_green), shape = RoundedCornerShape(32.dp))
            .padding(16.dp)
            .clickable { onNavigateToWeatherDetails(weather.id) }
    ) {
        // country, city and description, time
        Column {
            Text(
                text = weather.country.getCountryNameFromCode() ?: "",
                fontSize = 18.sp,
                color = colorResource(R.color.neutral_gray),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = weather.city,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = weather.description,
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = stringResource(
                    R.string.last_update_with_date, timeStampToHumanDate(
                        weather.dt.toLong(),
                        stringResource(R.string.day_abbr_month_hour_minute_am_pm)
                    )
                ),
                fontSize = 14.sp,
                color = colorResource(R.color.neutral_gray),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )

        }
        Spacer(Modifier.weight(1f))

        // icon and temp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlideImage(
                model = weather.icon,
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Text(
                text = "${weather.temp.toInt()} °C",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun DeleteBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.White
        )
    }
}

@Composable
fun <T> SwipeToDeleteContainer(
    context: Context,
    item: T,
    onDelete: (T) -> Unit,
    onRestore: (T) -> Unit = {},
    animationDuration: Int = 500,
    snackBarHostState: SnackbarHostState,
    content: @Composable (T) -> Unit
) {

    var isRemoved by remember { mutableStateOf(false) }
    var canSwipe by remember { mutableStateOf(true) }

    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                isRemoved = true
                canSwipe = false
                true
            } else {
                false
            }
        }
    )
    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            val result = snackBarHostState.showSnackbar(
                message = context.getString(R.string.location_deleted_successfully),
                actionLabel = context.getString(R.string.undo),
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                onRestore(item)
                canSwipe = true
                isRemoved = false
            } else {
                delay(animationDuration.toLong())
                onDelete(item)
            }
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            shrinkTowards = Alignment.Top,
            animationSpec = tween(durationMillis = animationDuration)
        ) + fadeOut(),
        enter = expandVertically(
            expandFrom = Alignment.Top,
            animationSpec = tween(durationMillis = animationDuration)
        ) + fadeIn(),
    ) {
        if (canSwipe) {
            SwipeToDismissBox(
                state = state,
                backgroundContent = { DeleteBackground() },
                enableDismissFromStartToEnd = false
            ) {
                content(item)
            }
        } else {
            LaunchedEffect(Unit) {
                state.snapTo(SwipeToDismissBoxValue.Settled)
            }
            content(item)
        }
    }
}