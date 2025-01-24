package com.github.kr328.clash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.core.bridge.Bridge
import com.github.kr328.clash.design.MainDesign
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.remote.StatusClient
import com.github.kr328.clash.service.model.Profile
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity<MainDesign>() {

    override suspend fun main() {
        val design = MainDesign(this)

        setContentDesign(design)

        design.fetch()
        design.autoImportConfig()

        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))

        while (isActive) {
            select<Unit> {
                events.onReceive {
                    Log.i("onReceive event ${it}")
                    when (it) {
                        Event.ActivityStart,
                        Event.ServiceRecreated,
                        Event.ClashStop, Event.ClashStart,
                        Event.ProfileLoaded, Event.ProfileChanged -> design.fetch()
                        Event.ExpirationExpired -> stopClash()
                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        MainDesign.Request.ToggleStatus -> {
                            if (clashRunning) {
                                stopClashService()
                            } else {
                                design.startClash()
                            }
                        }

                        MainDesign.Request.OpenProxy ->
                            startActivity(ProxyActivity::class.intent)

                        MainDesign.Request.OpenProfiles ->
                            startActivity(ProfilesActivity::class.intent)

                        MainDesign.Request.OpenProviders ->
                            startActivity(ProvidersActivity::class.intent)

                        MainDesign.Request.OpenLogs ->
                            startActivity(LogsActivity::class.intent)

                        MainDesign.Request.OpenSettings ->
                            startActivity(SettingsActivity::class.intent)

                        MainDesign.Request.OpenHelp ->
                            startActivity(HelpActivity::class.intent)

                        MainDesign.Request.OpenAbout ->
                            design.showAbout(queryAppVersionName())

                        MainDesign.Request.JoinQQGroup -> {
                            design.joinQQGroup()
                        }

                        MainDesign.Request.Purchase -> {
                            startActivity(SubscriptionsActivity::class.intent)
                        }

                        MainDesign.Request.Activate -> {
                            startActivity(ActivationCodeInputActivity::class.intent)
                        }
                    }
                }
                if (clashRunning) {
                    ticker.onReceive {
                        design.fetchTraffic()
                    }
                }
            }
        }
    }

    private fun stopClash() {
        if (clashRunning) {
            stopClashService()
        }
    }

    private suspend fun MainDesign.fetch() {
        setClashRunning(clashRunning)

        val state = withClash {
            queryTunnelState()
        }
        val providers = withClash {
            queryProviders()
        }

        setMode(state.mode)
        setHasProviders(providers.isNotEmpty())

        withProfile {
            setProfileName(queryActive()?.name)
        }
    }

    private suspend fun MainDesign.autoImportConfig() {
        try {
            withProfile {
                val providerList = queryAll()
                if (providerList.size == 0) {
                    createDefaultProfile {
                        withContext(Dispatchers.Main) {
                            withProcessing { updateStatus ->
                                coroutineScope {
                                    commit(it.uuid) {
                                        launch {
                                            Log.i(it.toString())
                                            updateStatus(it)
                                        }
                                    }
                                    setActive(it)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.i(e.message!!)
        }
    }

    private suspend fun createDefaultProfile(block: suspend (profile: Profile) -> Unit) {
        withProfile {
            val name = getString(R.string.new_profile)
            val uuid: UUID = create(Profile.Type.Url, name)
            var profile = queryByUUID(uuid)
            if (profile != null) {
                profile = profile.copy(
                    name = getString(R.string.application_name),
                    source = "https://ftq.ink/group",
                    interval = TimeUnit.MINUTES.toMillis(60 * 24)
                )
                Log.i("profile: " + profile)
                patch(profile.uuid, profile.name, profile.source, profile.interval)
                block(profile)
            }
        }
    }

    private suspend fun MainDesign.fetchTraffic() {
        withClash {
            setForwarded(queryTrafficTotal())
        }
    }

    private suspend fun MainDesign.startClash() {
        val active = withProfile { queryActive() }

        if (active == null || !active.imported) {
            showToast(R.string.no_profile_selected, ToastDuration.Long) {
                setAction(R.string.profiles) {
                    startActivity(ProfilesActivity::class.intent)
                }
            }

            return
        }

        val vpnRequest = startClashService()

        try {
            if (vpnRequest != null) {
                val result = startActivityForResult(
                    ActivityResultContracts.StartActivityForResult(),
                    vpnRequest
                )

                if (result.resultCode == RESULT_OK)
                    startClashService()
            }
        } catch (e: Exception) {
            design?.showToast(R.string.unable_to_start_vpn, ToastDuration.Long)
        }
    }

    private suspend fun queryAppVersionName(): String {
        return withContext(Dispatchers.IO) {
            packageManager.getPackageInfo(
                packageName,
                0
            ).versionName + "\n" + Bridge.nativeCoreVersion().replace("_", "-")
        }
    }
}