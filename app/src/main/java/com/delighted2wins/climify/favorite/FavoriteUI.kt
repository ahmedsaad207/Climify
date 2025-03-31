package com.delighted2wins.climify.favorite

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.delighted2wins.climify.enums.TempUnit
import com.delighted2wins.climify.home.components.LoadingIndicator
import com.delighted2wins.climify.home.getRepo
import com.delighted2wins.climify.utils.Constants
import com.delighted2wins.climify.utils.convertTemp
import com.delighted2wins.climify.utils.getCountryNameFromCode
import com.delighted2wins.climify.utils.getTempUnitSymbol
import com.delighted2wins.climify.utils.timeStampToHumanDate
import com.delighted2wins.climify.utils.toLocalizedNumber
import kotlinx.coroutines.launch

@Composable
fun FavoriteUI(
    showBottomNabBar: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    showFloatingActionButton: MutableState<Boolean>,
    onNavigateToWeatherDetails: (Int) -> Unit
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val viewModel: FavoriteViewModel =
        viewModel(factory = FavoriteViewModelFactory(getRepo(context)))

    LaunchedEffect(Unit) {
        showBottomNabBar.value = true
        showFloatingActionButton.value = true
    }
    LaunchedEffect(Unit) {
        viewModel.getFavoriteWeathers()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (uiState) {
        is Response.Loading -> LoadingIndicator()

        is Response.Success -> {
            val weathers = (uiState as Response.Success).data
            val weathersState = remember { mutableStateOf(weathers) }
            val appUnit = viewModel.getData<TempUnit>(Constants.KEY_TEMP_UNIT).value
            val animationDuration = 500

            LaunchedEffect(weathers) {
                weathersState.value = weathers
            }

            if (weathersState.value.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    // header
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

                    // list
                    items(
                        items = weathersState.value,
                        key = { it.id }
                    ) { weather ->
                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.deleteWeather(weather)

                                    scope.launch {
                                        snackBarHostState.currentSnackbarData?.dismiss()
                                        val result = snackBarHostState.showSnackbar(
                                            message = "Location deleted from favorite",
                                            actionLabel = "Undo",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            viewModel.insertWeather(weather)
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
                                FavoriteLocationItem(weather, onNavigateToWeatherDetails, appUnit)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(180.dp))
                    }

                }
            } else {
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
fun FavoriteLocationItem(
    weather: CurrentWeather,
    onNavigateToWeatherDetails: (Int) -> Unit,
    appUnit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToWeatherDetails(weather.id) }
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .background(colorResource(R.color.grayish_green), shape = RoundedCornerShape(32.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val unit = LocalContext.current.getTempUnitSymbol(appUnit)
        val temp =
            weather.unit.convertTemp(weather.temp.toDouble(), appUnit).toInt().toLocalizedNumber()

        // country, city and description, time
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = weather.country.getCountryNameFromCode() ?: "",
                fontSize = 16.sp,
                color = colorResource(R.color.neutral_gray),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = weather.city,
                fontSize = 24.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = weather.description,
                fontSize = 18.sp,
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
                fontSize = 13.sp,
                color = colorResource(R.color.neutral_gray),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
            )

        }

        // icon and temp
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .width(96.dp)
                .fillMaxHeight()
        ) {
            GlideImage(
                model = weather.icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )
            Spacer(Modifier.height(15.dp))
            Row {
                Text(
                    text = temp,
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = unit,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(4.dp)
                )
            }
        }
    }
}

