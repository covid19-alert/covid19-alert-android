package com.immotef.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.security.crypto.MasterKeys
import com.immotef.authorization.AuthorizationProvider
import com.immotef.db.infected.Infected
import com.immotef.db.infected.InfectedDAO
import com.immotef.db.meet.Meet
import com.immotef.db.meet.MeetDAO
import kotlinx.coroutines.runBlocking
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory


/**
 *
 */


private const val DATABASE_NAME = "corona-db"

@Database(entities = [Meet::class, Infected::class], version = 4, exportSchema = false)
internal abstract class CoronaVirusDatabase : RoomDatabase(), ClearDB {

    abstract fun getMeetDao(): MeetDAO

    abstract fun getInfectedDao(): InfectedDAO

    companion object {
        fun buildDatabase(context: Context, authorizationProvider: AuthorizationProvider): CoronaVirusDatabase {


            val masterKeyAlias = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
                MasterKeys.getOrCreate(keyGenParameterSpec)
            } else {
                runBlocking { authorizationProvider.provideUUID() }
            }
            val passphrase: ByteArray = SQLiteDatabase.getBytes(masterKeyAlias.toCharArray())
            val factory = SupportFactory(passphrase)
            return Room.databaseBuilder(context, CoronaVirusDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().openHelperFactory(factory)
                .build()
        }

    }

    override fun dropAll() {
        this.clearAllTables()
    }
}

interface ClearDB {
    fun dropAll()
}