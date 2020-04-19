package com.immotef.preferences

import android.app.backup.BackupManager
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 *
 */


interface PreferencesFacade {
    suspend fun saveString(text: String, key: String)
    suspend fun retrieveString(key: String): String?
    suspend fun saveInt(number: Int, key: String)
    suspend fun saveLong(value: Long, key: String)
    suspend fun retrieveLong(key: String, defValue: Long = -1): Long
    suspend fun retrieveInt(key: String): Int
    suspend fun saveBoolean(value: Boolean, key: String)
    suspend fun retrieveBoolean(key: String): Boolean
    suspend fun putObject(key: String, any: Any)
    suspend fun <T> getObject(key: String, type: Class<T>): T?
    suspend fun contains(key: String): Boolean
    suspend fun remove(key: String)
}


internal class PreferencesFacadeImp(private val sharedPreferences: SharedPreferences, private val backupManager: BackupManager) : PreferencesFacade {
    override suspend fun saveString(text: String, key: String) {
        sharedPreferences.edit().putString(key, text).apply()
        backupManager.dataChanged()
    }

    override suspend fun retrieveString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override suspend fun saveInt(number: Int, key: String) {
        sharedPreferences.edit().putInt(key, number).apply()
        backupManager.dataChanged()
    }

    override suspend fun retrieveInt(key: String): Int {
        return sharedPreferences.getInt(key, -1000)
    }

    override suspend fun saveBoolean(value: Boolean, key: String) {
        sharedPreferences.edit().putBoolean(key, value).apply()
        backupManager.dataChanged()
    }

    override suspend fun retrieveBoolean(key: String): Boolean {
        return try {
            sharedPreferences.getBoolean(key, false)
        } catch (ex: ClassCastException) {
            sharedPreferences.edit().remove(key).apply()
            false
        }
    }

    override suspend fun saveLong(value: Long, key: String) {
        sharedPreferences.edit().putLong(key, value).apply()
        backupManager.dataChanged()
    }

    override suspend fun retrieveLong(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    override suspend fun putObject(key: String, any: Any) {
        val gson = Gson()
        saveString(gson.toJson(any), key)
        backupManager.dataChanged()
    }

    override suspend fun <T> getObject(key: String, type: Class<T>): T? {
        val gson = Gson()
        return gson.fromJson(retrieveString(key), type)
    }

    override suspend fun contains(key: String): Boolean = sharedPreferences.contains(key)

    override suspend fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}