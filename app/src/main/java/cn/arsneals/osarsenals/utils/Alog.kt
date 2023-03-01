package cn.arsneals.osarsenals.utils

import android.util.Log

class Alog {
    companion object {
        private fun convertToAlogTag(tag: String): String {
            return "ARSENALS_$tag"
        }

        fun verbose(tag: String, msg: String) {
            Log.v(convertToAlogTag(tag), msg)
        }

        fun verbose(tag: String, msg: String, tr: Throwable?) {
            Log.v(convertToAlogTag(tag), msg, tr)
        }

        fun debug(tag: String, msg: String) {
            Log.d(convertToAlogTag(tag), msg)
        }

        fun debug(tag: String, msg: String, tr: Throwable) {
            Log.d(convertToAlogTag(tag), msg, tr)
        }

        fun info(tag: String, msg: String) {
            Log.i(convertToAlogTag(tag), msg)
        }

        fun info(tag: String, msg: String, tr: Throwable?) {
            Log.i(convertToAlogTag(tag), msg, tr)
        }

        fun warn(tag: String, msg: String) {
            Log.w(convertToAlogTag(tag), msg)
        }

        fun warn(tag: String, msg: String, tr: Throwable?) {
            Log.w(convertToAlogTag(tag), msg, tr)
        }

        fun error(tag: String, msg: String) {
            Log.e(convertToAlogTag(tag), msg)
        }

        fun error(tag: String, msg: String?, tr: Throwable) {
            Log.e(convertToAlogTag(tag), msg, tr)
        }

        fun wtf(tag: String, msg: String?) {
            Log.wtf(convertToAlogTag(tag), msg)
        }

        fun wtf(tag: String, tr: Throwable) {
            Log.wtf(convertToAlogTag(tag), tr)
        }

        fun wtf(tag: String, msg: String?, tr: Throwable) {
            Log.wtf(convertToAlogTag(tag), msg, tr)
        }
    }
}