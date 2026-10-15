package com.mymwn.klash

import android.content.Context

object Prefs {
    private const val FILE = "mymwn_klash"
    private const val LIKES = "likes"

    fun getLikes(context: Context): Set<String> =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getStringSet(LIKES, emptySet()) ?: emptySet()

    fun setLiked(context: Context, uri: String, liked: Boolean) {
        val p = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        val set = p.getStringSet(LIKES, emptySet())?.toMutableSet() ?: mutableSetOf()
        if (liked) set.add(uri) else set.remove(uri)
        p.edit().putStringSet(LIKES, set).apply()
    }

    fun isLiked(context: Context, uri: String) = getLikes(context).contains(uri)
}
