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

import org.slf4j.LoggerFactory

private val LOGGER = LoggerFactory.getLogger("com.poterion.footprint.manager.utils.DebugUtils")

/**
 * Executes given `function` and logs measured time passed during execution.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param message Message to the log
 * @param function Function to execute
 * @return Result of the function
 */
fun <R> measureTime(message: String, function: () -> R): R {
	val start = System.currentTimeMillis()
	val result = function()
	LOGGER.info("${message} in ${(System.currentTimeMillis() - start).toFormattedDuration()}"
						.replace("%s", result.toString()))
	return result
}