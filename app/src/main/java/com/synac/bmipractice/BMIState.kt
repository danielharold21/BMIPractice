package com.synac.bmipractice


data class BMIState(
    val weightValue: String = "60",
    val weightValueStage: WeightValueStage = WeightValueStage.ACTIVE,
    val weightUnit: String = "Kilograms",
    val heightValue: String = "170",
    val heightValueStage: HeightValueStage = HeightValueStage.INACTIVE,
    val heightUnit: String = "Centimeters",
    val shouldSheetShow: Boolean = false,
    val sheetTitle: String = "",
    val sheetItemsList: List<String> = emptyList(),
    val shouldBMICardShow: Boolean = false,
    val bmi: Double = 0.0,
    val bmiStage: String = "",
)

enum class WeightValueStage {
    INACTIVE,
    ACTIVE,
    RUNNING
}

enum class HeightValueStage {
    INACTIVE,
    ACTIVE,
    RUNNING
}