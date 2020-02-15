/******************************************************************************
 * Copyright (C) 2020 Jan Kubovy (jan@kubovy.eu)                              *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published      *
 * by the Free Software Foundation, either version 3 of the License, or (at   *
 * your option) any later version.                                            *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Lesser General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public           *
 * License along with this program.  If not, see                              *
 * <http://www.gnu.org/licenses/>.                                            *
 ******************************************************************************/
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