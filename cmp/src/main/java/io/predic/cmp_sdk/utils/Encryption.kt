package io.predic.cmp_sdk.utils

import android.util.Log
import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

internal class Encryption {

    companion object {
        /**
         * Takes md5 hash from string.
         *
         * @param str String to hash.
         * @return MD5 hashed string.
         */
        fun getMD5(str: String): String? {
            return try {
                val idInBytes = str.toByteArray(charset("UTF-8"))
                val md = MessageDigest.getInstance("MD5")
                val idDigest = md.digest(idInBytes)
                val md5 = BigInteger(1, idDigest).toString(16)
                md5.subSequence(0, 2).toString()
            } catch (e: UnsupportedEncodingException) {
                Log.e(Constants.TAG, "MD5 algorithm is not supported.")
                null
            } catch (e: NoSuchAlgorithmException) {
                Log.e(Constants.TAG, "MD5 algorithm does not exist.")
                null
            }

        }

        fun encryptAAID(AAID: String): String {
            val md5Value = Encryption.getMD5(AAID)
            return if (md5Value != null) md5Value else {
                Log.e(Constants.TAG, "MD5 AAID unwrap failed")
                return ""
            }
        }
    }
}