package com.poterion.utils.kotlin

import java.util.*

/**
 * Encodes this [ByteArray] as [Base64] [String]
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @return [Base64] encoded string
 */
fun ByteArray.base64Encode(): String = Base64.getEncoder().encodeToString(this)