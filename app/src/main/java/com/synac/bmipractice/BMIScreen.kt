package com.synac.bmipractice

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synac.bmipractice.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BMIScreen(
    viewModel: BMIViewModel
) {
    val state = viewModel.state

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

    val coroutineScope = rememberCoroutineScope()
    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    LaunchedEffect(key1 = state.error) {
        if (state.error != null) {
            Toast.makeText(
                context,
                "This BMI does not look good, check again the height and weight value",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetContent = {
            BottomSheetContent(
                sheetTitle = state.sheetTitle,
                sheetItemsList = state.sheetItemsList,
                onItemClicked = {
                    coroutineScope.launch { modalSheetState.hide() }
                    viewModel.onAction(BMIActions.SheetItemClicked(it))
                },
                onCancelClicked = {
                    coroutineScope.launch { modalSheetState.hide() }
                    viewModel.onAction(BMIActions.CancelSheet)
                }
            )
        },
        sheetBackgroundColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        BMIScreenContent(
            state = state,
            weightUnitClicked = {
                coroutineScope.launch { modalSheetState.show() }
                viewModel.onAction(BMIActions.WeightClicked)
            },
            weightInputValueClicked = { viewModel.onAction(BMIActions.WeightValueClicked) },
            heightUnitClicked = {
                coroutineScope.launch { modalSheetState.show() }
                viewModel.onAction(BMIActions.HeightClicked)
            },
            heightInputValueClicked = { viewModel.onAction(BMIActions.HeightValueClicked) },
            onShareButtonClicked = { context.startActivity(shareIntent) },
            numberButtonClicked = { viewModel.onAction(BMIActions.Number(it)) },
            allClearButtonClicked = { viewModel.onAction(BMIActions.AllClear) },
            deleteButtonClicked = { viewModel.onAction(BMIActions.Delete) },
            goButtonClicked = { viewModel.onAction(BMIActions.GoButton) }
        )
    }
}

@Composable
fun BMIScreenContent(
    state: BMIScreenState,
    weightUnitClicked: () -> Unit,
    weightInputValueClicked: (String) -> Unit,
    heightUnitClicked: () -> Unit,
    heightInputValueClicked: (String) -> Unit,
    onShareButtonClicked: () -> Unit,
    numberButtonClicked: (String) -> Unit,
    allClearButtonClicked: () -> Unit,
    deleteButtonClicked: () -> Unit,
    goButtonClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .padding(15.dp),
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
                    onClick = weightUnitClicked,
                    text = "Weight",
                    textColor = if (state.sheetTitle == "Weight") CustomOrange else Color.Black
                )
                InputUnitValue(
                    inputValue = state.weightValue,
                    inputUnit = state.weightUnit,
                    onInputValueClick = weightInputValueClicked,
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
                    onClick = heightUnitClicked,
                    text = "Height",
                    textColor = if (state.sheetTitle == "Height") CustomOrange else Color.Black
                )
                InputUnitValue(
                    inputValue = state.heightValue,
                    inputUnit = state.heightUnit,
                    onInputValueClick = heightInputValueClicked,
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
                            onClick = onShareButtonClicked
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
                            NumberKeyboard(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(7f),
                                onNumberClick = numberButtonClicked
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(3f),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                SymbolButton(
                                    symbol = "AC",
                                    onClick = allClearButtonClicked
                                )
                                SymbolButtonWithIcon(
                                    onClick = deleteButtonClicked
                                )
                                SymbolButton(
                                    symbol = "GO",
                                    onClick = goButtonClicked
                                )
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