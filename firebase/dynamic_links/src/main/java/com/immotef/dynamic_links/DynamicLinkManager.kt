package com.immotef.dynamic_links

import android.content.Intent
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.immotef.core.CoroutineUtils
import kotlinx.coroutines.launch

/**
 *
 */


interface DynamicLinkManager {

    fun handleDeepLink(intent: Intent)
}

internal class DynamicLinkManagerImp(
    private val deepLinkStateTrigger: DeepLinkInternalTrigger,
    private val coroutineUtils: CoroutineUtils
) : DynamicLinkManager {


    override fun handleDeepLink(intent: Intent) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData ->
                if (pendingDynamicLinkData != null) {
                    var id = ""
                    var timestamp = -1L
                    val pair = writeIdAndTimeStamp(pendingDynamicLinkData, id, timestamp)
                    id = pair.first
                    timestamp = pair.second
                    if (id != "") {
                        coroutineUtils.globalScope.launch {
                            deepLinkStateTrigger.setInfectionState(ReportInfectionState(id, timestamp))
                        }
                    }
                } else {
                    coroutineUtils.globalScope.launch {
                        if(deepLinkStateTrigger.isInfectionStateOn()){
                            deepLinkStateTrigger.triggerInternal()
                        }

                    }
                }
            }
            .addOnFailureListener { e -> print(e.message) }
    }

    private fun writeIdAndTimeStamp(pendingDynamicLinkData: PendingDynamicLinkData,
                                    id: String,
                                    timestamp: Long): Pair<String, Long> {
        var id1 = id
        var timestamp1 = timestamp
        pendingDynamicLinkData.link.toString().split("?").last().split("&").map { it.split("=") }.forEach {
            when (it[0]) {
                "infectionValidationKey" -> try {
                    id1 = it[1]
                } catch (e: Exception) {
                }
                "timestamp" -> try {
                    timestamp1 = it[1].toLong()
                } catch (e: Exception) {
                }
            }
        }
        return Pair(id1, timestamp1)
    }
}