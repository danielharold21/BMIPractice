package com.synac.bmipractice

sealed class BMIActions {
    object WeightClicked: BMIActions()
    object HeightClicked: BMIActions()
    object WeightValueClicked: BMIActions()
    object HeightValueClicked: BMIActions()
    data class SheetItemClicked(val sheetItem: String): BMIActions()
    object CancelSheet: BMIActions()
    data class Number(val number: String): BMIActions()
    object AllClear: BMIActions()
    object Delete: BMIActions()
    object GoButton: BMIActions()
}
