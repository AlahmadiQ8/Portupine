package com.nbk.weyay.weyaydesktopclient

import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import java.util.prefs.Preferences

// Based on https://github.com/jameshball/osci-render/blob/5d789ffd287a61d506cd6e8f4f33ea2a0ec25be8/src/main/java/sh/ball/gui/controller/ProjectSelectController.java
object RecentFiles {
    private const val MAX_ITEMS = 5
    private const val RECENT_FILE = "RECENT_FILE_"

    private val userPreferences = Preferences.userRoot()
    val recentItems = FXCollections.observableArrayList<String>()

    init {
        for (recent in 0 until MAX_ITEMS) {
            userPreferences.get("$RECENT_FILE$recent", null)
                ?.let { recentItems.add(it) }
                ?: break
        }
        recentItems.addListener(ListChangeListener { ch ->
            while (ch.next()) {
                resetRecentFiles()
            }
        })
    }

    fun addRecentFile(path: String) {
        recentItems.indexOf(path).let { index ->
            if (index == -1) {
                recentItems.add(0, path)
                if (recentItems.size > MAX_ITEMS) {
                    recentItems.removeAt(recentItems.size - 1)
                }
            } else {
                recentItems.removeAt(index)
                recentItems.add(0, path)
            }
        }
    }

    fun removeRecentFile(path: String) {
        recentItems.remove(path)
    }

    private fun resetRecentFiles() {
        for (i in 0 until MAX_ITEMS)
            userPreferences.remove("$RECENT_FILE$i")
        for (i in 0 until recentItems.size)
            userPreferences.put("$RECENT_FILE$i", recentItems[i])
    }
}