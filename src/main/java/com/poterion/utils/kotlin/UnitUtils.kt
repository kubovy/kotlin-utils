package com.poterion.utils.kotlin

private val KI = arrayOf("", "k", "M", "G", "T", "P")

fun Number.toKI(suffix: String = "B", digitsAfterComma: Int = 2): String {
	var index = 0
	var value = toDouble()
	while (value > 1024 && KI.size > (index + 1)) {
		value /= 1024
		index++
	}
	return "%.${digitsAfterComma}f%s%s".format(value, KI[index], suffix)
}