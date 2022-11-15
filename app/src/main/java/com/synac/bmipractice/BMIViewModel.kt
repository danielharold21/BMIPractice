package com.synac.bmipractice

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

class BMIViewModel : ViewModel() {

    //TODO animation on buttons press

    var state by mutableStateOf(BMIState())

    fun onAction(action: BMIActions) {
        when (action) {
            BMIActions.WeightClicked -> {
                state = state.copy(
                    shouldSheetShow = true,
                    sheetTitle = "Weight",
                    sheetItemsList = listOf("Kilograms", "Pounds")
                )
            }
            BMIActions.HeightClicked -> {
                state = state.copy(
                    shouldSheetShow = true,
                    sheetTitle = "Height",
                    sheetItemsList = listOf("Centimeters", "Meters", "Feet", "Inches")
                )
            }
            is BMIActions.SheetItemClicked -> {
                changeWeightOrHeightUnit(action.sheetItem)
            }
            BMIActions.CancelSheet -> {
                state = state.copy(
                    shouldSheetShow = false,
                    sheetTitle = "",
                    sheetItemsList = emptyList()
                )
            }
            is BMIActions.WeightValueClicked -> {
                state = state.copy(
                    weightValueStage = WeightValueStage.ACTIVE,
                    heightValueStage = HeightValueStage.INACTIVE,
                    shouldBMICardShow = false
                )
            }
            is BMIActions.HeightValueClicked -> {
                state = state.copy(
                    heightValueStage = HeightValueStage.ACTIVE,
                    weightValueStage = WeightValueStage.INACTIVE,
                    shouldBMICardShow = false
                )
            }
            is BMIActions.Number -> {
                enterNumber(action.number)
            }
            BMIActions.AllClear -> {
                allClearToZero()
            }
            BMIActions.Delete -> {
                deleteLastDigit()
            }
            BMIActions.GoButton -> {
                calculateBMI()
            }
        }
    }

    private fun calculateBMI() {
        val weightInKgs: Double = when (state.weightUnit) {
            "Pounds" -> {
                state.weightValue.toDouble().times(0.4536)
            }
            else -> {
                state.weightValue.toDouble()
            }
        }
        val heightInMeters: Double = when (state.heightUnit) {
            "Centimeters" -> {
                state.heightValue.toDouble().times(0.01)
            }
            "Feet" -> {
                state.heightValue.toDouble().times(0.3048)
            }
            "Inches" -> {
                state.heightValue.toDouble().times(0.0254)
            }
            else -> {
                state.heightValue.toDouble()
            }
        }

        val bmiValue = weightInKgs / (heightInMeters * heightInMeters)
        val bmiValueWithOneDecimal = (bmiValue * 10).roundToInt() / 10.0
        val bmiStage = when (bmiValueWithOneDecimal) {
            in 0.0..18.5 -> "Underweight"
            in 18.5..25.0 -> "Normal"
            in 25.0..100.0 -> "Overweight"
            else -> "Invalid"
        }

        state = state.copy(
            shouldBMICardShow = true,
            bmi = if (bmiValueWithOneDecimal > 100) 0.0 else bmiValueWithOneDecimal,
            bmiStage = bmiStage
        )
    }

    private fun deleteLastDigit() {
        if (state.weightValueStage != WeightValueStage.INACTIVE) {
            state = if (state.weightValue.length == 1) {
                state.copy(
                    weightValue = "0",
                    weightValueStage = WeightValueStage.ACTIVE
                )
            } else {
                state.copy(weightValue = state.weightValue.dropLast(1))
            }
            return
        } else if (state.heightValueStage != HeightValueStage.INACTIVE) {
            state = if (state.heightValue.length == 1) {
                state.copy(
                    heightValue = "0",
                    heightValueStage = HeightValueStage.ACTIVE
                )
            } else {
                state.copy(heightValue = state.heightValue.dropLast(1))
            }
        }
    }

    private fun allClearToZero() {
        if (state.weightValueStage != WeightValueStage.INACTIVE) {
            state = state.copy(
                weightValue = "0",
                weightValueStage = WeightValueStage.ACTIVE
            )
        } else if (state.heightValueStage != HeightValueStage.INACTIVE) {
            state = state.copy(
                heightValue = "0",
                heightValueStage = HeightValueStage.ACTIVE
            )
        }
    }

    private fun changeWeightOrHeightUnit(
        sheetItem: String
    ) {
        if (state.sheetTitle == "Weight") {
            state = state.copy(
                weightUnit = sheetItem,
                sheetTitle = "",
                shouldSheetShow = false,
                shouldBMICardShow = false
            )
            return
        } else if (state.sheetTitle == "Height") {
            state = state.copy(
                heightUnit = sheetItem,
                sheetTitle = "",
                shouldSheetShow = false,
                shouldBMICardShow = false
            )
        }
    }

    private fun enterNumber(
        number: String
    ) {
        when {
            state.weightValueStage == WeightValueStage.ACTIVE -> {
                state = if (number == ".") {
                    state.copy(
                        weightValue = "0.",
                        weightValueStage = WeightValueStage.RUNNING
                    )
                } else {
                    state.copy(
                        weightValue = number,
                        weightValueStage = WeightValueStage.RUNNING
                    )
                }
                return
            }
            state.weightValueStage == WeightValueStage.RUNNING -> {
                if (
                    !state.weightValue.contains(".")
                    && state.weightValue.length < 4
                ) {
                    if (state.weightValue.length < 3 && number != ".") {
                        state = state.copy(
                            weightValue = state.weightValue + number,
                            weightValueStage = WeightValueStage.RUNNING
                        )
                    } else if (number == ".") {
                        state = state.copy(
                            weightValue = state.weightValue + number,
                            weightValueStage = WeightValueStage.RUNNING
                        )
                    }
                } else if (
                    state.weightValue.contains(".") &&
                    state.weightValue.reversed().indexOf('.') < 2
                ) {
                    state = state.copy(
                        weightValue = state.weightValue + number,
                        weightValueStage = WeightValueStage.RUNNING
                    )
                }
                return
            }
            state.heightValueStage == HeightValueStage.ACTIVE -> {
                state = if (number == ".") {
                    state.copy(
                        heightValue = "0.",
                        heightValueStage = HeightValueStage.RUNNING
                    )
                } else {
                    state.copy(
                        heightValue = number,
                        heightValueStage = HeightValueStage.RUNNING
                    )
                }
                return
            }
            state.heightValueStage == HeightValueStage.RUNNING -> {
                if (
                    !state.heightValue.contains(".")
                    && state.heightValue.length < 4
                ) {
                    if (state.heightValue.length < 3 && number != ".") {
                        state = state.copy(
                            heightValue = state.heightValue + number,
                            heightValueStage = HeightValueStage.RUNNING
                        )
                    } else if (number == ".") {
                        state = state.copy(
                            heightValue = state.heightValue + number,
                            heightValueStage = HeightValueStage.RUNNING
                        )
                    }
                } else if (
                    state.heightValue.contains(".") &&
                    state.heightValue.reversed().indexOf('.') < 2
                ) {
                    state = state.copy(
                        heightValue = state.heightValue + number,
                        heightValueStage = HeightValueStage.RUNNING
                    )
                }
                return
            }
        }
    }
}
