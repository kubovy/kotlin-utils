@file:Suppress("unused")
package com.poterion.utils.kotlin

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

private const val DELIMITER = ":"
private var passwordCache: String = ""
private var saltCache: String = "p2KiPkUUqArHHImPK5hvs4GhowWg5zalrRVn7HA8wM81cmVNOkBYD0xKYLc9udc6gXAOhX5xxdnORB1X"

/**
 * Set a passsword to a in-memory cache. This password is than used as a default password for [encrypting][encrypt]
 * and [decrypting][decrypt] messages.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param password Password
 */
fun setPasswordForEncryption(password: String) {
	passwordCache = password
}

/**
 * Set a salt to a in-memory cache. This salt is than used as a default salt for [key generation][createSecretKey].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param salt Salt
 */
fun setSaltForEncryption(salt: String) {
	saltCache = salt
}

/**
 * Creates a password-based secret key.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param password Password
 * @param iterationCount Iterations
 * @param keyLength Key length
 * @return [SecretKeySpec]
 */
fun createSecretKey(password: String, iterationCount: Int = 1_000_000, keyLength: Int = 128): SecretKeySpec =
	createSecretKey(password.toCharArray(), iterationCount, keyLength)

/**
 * Creates a password-based secret key.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param password Password
 * @param iterationCount Iterations
 * @param keyLength Key length
 * @return [SecretKeySpec]
 */
fun createSecretKey(password: CharArray, iterationCount: Int = 1_000_000, keyLength: Int = 128): SecretKeySpec {
	val keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
	val keySpec = PBEKeySpec(password, saltCache.toByteArray(), iterationCount, keyLength)
	val keyTmp = keyFactory.generateSecret(keySpec)
	return SecretKeySpec(keyTmp.encoded, "AES")
}

/**
 * Encrypts a message with given `password`. If no `password` is given, the [default password][setPasswordForEncryption]
 * will be used.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param message Message to encrypt
 * @param password Password
 * @param iterationCount Iterations
 * @param keyLength Key length
 * @return Encrypted message
 */
fun encrypt(message: String, password: String? = null, iterationCount: Int = 1_000_000, keyLength: Int = 128) =
	encrypt(message, createSecretKey(password ?: passwordCache, iterationCount, keyLength))

/**
 * Encrypts this [String] with given `password`. If no `password` is given, the
 * [default password][setPasswordForEncryption] will be used.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param password Password
 * @return Encrypted message
 */
fun String.encrypt(password: String? = null): String =
	encrypt(this, password)

/**
 * Encrypts a message with given [`key`][SecretKeySpec].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param message Message to encrypt
 * @param key Key
 * @return Encrypted message
 */
fun encrypt(message: String, key: SecretKeySpec): String {
	val pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding").apply {
		init(Cipher.ENCRYPT_MODE, key)
	}
	val ivParameterSpec = pbeCipher.parameters.getParameterSpec(IvParameterSpec::class.java)
	val cryptoText = pbeCipher.doFinal(message.toByteArray(Charsets.UTF_8))
	val iv = ivParameterSpec.iv
	return iv.base64Encode() + DELIMITER + cryptoText.base64Encode()
}

/**
 * Decrypts a message with given `password`. If no `passord` is given, the [default password][setPasswordForEncryption]
 * will be used.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param cipherText Cipher text
 * @param password Password
 * @param iterationCount Iterations
 * @param keyLength Key length
 * @return Decrypted message
 */
fun decrypt(cipherText: String, password: String? = null, iterationCount: Int = 1_000_000, keyLength: Int = 128) =
	decrypt(cipherText, createSecretKey(password ?: passwordCache, iterationCount, keyLength))

/**
 * Decrypts this [String] with given `password`. If no `password` is given, the
 * [default password][setPasswordForEncryption] will be used.
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param password Password
 * @return Decrypted message
 */
fun String.decrypt(password: String? = null): String = decrypt(this, password)

/**
 * Decrypts a message with given [`key`][SecretKeySpec].
 *
 * @author Jan Kubovy [jan@kubovy.eu]
 * @param cipherText Cipher text
 * @param key Key
 * @return Decrypted message
 */
fun decrypt(cipherText: String, key: SecretKeySpec): String {
	val (iv, property) = cipherText.split(DELIMITER)
	val pbeCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
	pbeCipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv.base64Decode()))
	return String(pbeCipher.doFinal(property.base64Decode()), Charsets.UTF_8)
}




