package com.immotef.db

import com.immotef.db.infected.InfectedDAO
import com.immotef.db.meet.MeetDAO
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 *
 */


val dbModule = module {
    single<CoronaVirusDatabase> { CoronaVirusDatabase.buildDatabase(androidApplication(), get()) }
    factory<ClearDB> { get<CoronaVirusDatabase>() }
    single<MeetDAO> { get<CoronaVirusDatabase>().getMeetDao() }
    single<InfectedDAO> { get<CoronaVirusDatabase>().getInfectedDao() }
}