package com.synac.bmipractice


data class BMIScreenState(
    val weightValue: String = "60",
    val weightValueStage: WeightValueStage = WeightValueStage.ACTIVE,
    val weightUnit: String = "Kilograms",
    val heightValue: String = "170",
    val heightValueStage: HeightValueStage = HeightValueStage.INACTIVE,
    val heightUnit: String = "Centimeters",
    val sheetTitle: String = "",
    val sheetItemsList: List<String> = emptyList(),
    val shouldBMICardShow: Boolean = false,
    val bmi: Double = 0.0,
    val bmiStage: String = "",
    val error: String? = null,
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