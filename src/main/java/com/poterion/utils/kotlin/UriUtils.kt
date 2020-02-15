package com.poterion.utils.kotlin

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun String.uriEncode() = URLEncoder.encode(this, StandardCharsets.UTF_8.name()).replace("+", "%20")

fun String.uriDecode() = URLDecoder.decode(this, StandardCharsets.UTF_8.name())