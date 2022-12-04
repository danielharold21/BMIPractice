package com.synac.bmipractice

import android.content.Intent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synac.bmipractice.ui.theme.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BMIScreen(
    viewModel: BMIViewModel
) {

    val state = viewModel.state

    val bottomSheetState = rememberBottomSheetState(
        initialValue = BottomSheetValue.Collapsed
    )
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hey Guy! Checkout my Body Mass Index: ${state.bmi} BMI, which is considered ${state.bmiStage}"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    val context = LocalContext.current

    LaunchedEffect(key1 = state.shouldSheetShow) {
        if (state.shouldSheetShow) bottomSheetState.expand()
        else bottomSheetState.collapse()
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            BottomSheetContent(
                sheetTitle = state.sheetTitle,
                sheetItemsList = state.sheetItemsList,
                onItemClicked = {
                    viewModel.onAction(BMIActions.SheetItemClicked(it))
                },
                onCancelClicked = {
                    viewModel.onAction(BMIActions.CancelSheet)
                }
            )
        },
        sheetBackgroundColor = Color.White,
        sheetPeekHeight = 0.dp,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrayBackground)
                .padding(15.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        viewModel.onAction(BMIActions.CancelSheet)
                    })
                },
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "BMI Calculator",
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UnitItem(
                        onClick = {
                            viewModel.onAction(BMIActions.WeightClicked)
                        },
                        text = "Weight",
                        textColor = if (state.sheetTitle == "Weight") {
                            CustomOrange
                        } else Color.Black
                    )
                    InputUnitItem(
                        inputNo = state.weightValue,
                        inputUnit = state.weightUnit,
                        onInputNoClick = {
                            viewModel.onAction(BMIActions.WeightValueClicked)
                        },
                        inputNoColor =
                        if (state.weightValueStage != WeightValueStage.INACTIVE) {
                            CustomOrange
                        } else Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UnitItem(
                        onClick = {
                            viewModel.onAction(BMIActions.HeightClicked)
                        },
                        text = "Height",
                        textColor = if (state.sheetTitle == "Height") {
                            CustomOrange
                        } else Color.Black
                    )
                    InputUnitItem(
                        inputNo = state.heightValue,
                        inputUnit = state.heightUnit,
                        onInputNoClick = {
                            viewModel.onAction(BMIActions.HeightValueClicked)
                        },
                        inputNoColor =
                        if (state.heightValueStage != HeightValueStage.INACTIVE) {
                            CustomOrange
                        } else Color.Black
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            ) {
                Crossfade(targetState = state.shouldBMICardShow) {
                    if (it) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            BMIResultCard(
                                bmi = state.bmi,
                                bmiStage = state.bmiStage,
                                bmiStageColor = when (state.bmiStage) {
                                    "Underweight" -> CustomBlue
                                    "Normal" -> CustomGreen
                                    else -> CustomRed
                                }
                            )
                            ShareButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    context.startActivity(shareIntent)
                                }
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Divider()
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(0.7f)
                                ) {
                                    val numberButtonList = listOf(
                                        "7", "8", "9", "4", "5", "6",
                                        "1", "2", "3", "", "0", "."
                                    )
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(3)
                                    ) {
                                        items(numberButtonList) { item ->
                                            NumberButton(
                                                modifier = Modifier
                                                    .aspectRatio(1f)
                                                    .weight(1f),
                                                number = item,
                                                onClick = {
                                                    viewModel.onAction(BMIActions.Number(item))
                                                }
                                            )
                                        }
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(0.3f),
                                    verticalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    SymbolButton(
                                        symbol = "AC",
                                        onClick = {
                                            viewModel.onAction(BMIActions.AllClear)
                                        }
                                    )
                                    SymbolButtonWithIcon(
                                        onClick = {
                                            viewModel.onAction(BMIActions.Delete)
                                        }
                                    )
                                    SymbolButton(
                                        symbol = "GO",
                                        onClick = {
                                            viewModel.onAction(BMIActions.GoButton)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun BMIScreenPrev() {
    BMIScreen(viewModel = BMIViewModel())
}