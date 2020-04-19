package com.immotef.btmanager

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.immotef.core.CoroutineUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module


/**
 *
 */
val btManagerModule = module{
    single<BtManager> { BtManagerImp(BluetoothAdapter.getDefaultAdapter(),androidApplication(),get()) }
}

interface BtManager {
    fun turnBtOn(): Boolean
    fun registerReceiver()
    fun unregisterReceiver()
    fun btOnStream(): Flow<Boolean>
}


@ExperimentalCoroutinesApi
internal class BtManagerImp(
    private val btAdapter: BluetoothAdapter?,
    private val context: Context,
    private val coroutineUtils: CoroutineUtils) : BtManager {

    private val broadcastChannel = ConflatedBroadcastChannel<Boolean>()
    override fun turnBtOn(): Boolean {
        if (btAdapter != null && !btAdapter.isEnabled) {
            return btAdapter.enable()
        }
        return true
    }

    override fun registerReceiver() {
        context.registerReceiver(mReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun unregisterReceiver() = context.unregisterReceiver(mReceiver)

    override fun btOnStream(): Flow<Boolean> = broadcastChannel.asFlow()


    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        turnBtOn()
                        coroutineUtils.globalScope.launch {
                            broadcastChannel.send(false)
                        }
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                    }
                    BluetoothAdapter.STATE_ON -> {
                        coroutineUtils.globalScope.launch {
                            broadcastChannel.send(true)
                        }
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                    }
                }
            }
        }
    }
}