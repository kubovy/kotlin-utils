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