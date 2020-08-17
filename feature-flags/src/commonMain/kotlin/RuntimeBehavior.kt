package com.nicolasmilliard.socialcats.featureflags

import co.touchlab.stately.collections.IsoMutableList

/**
 * Check whether a feature should be enabled or not. Based on the priority of the different providers and if said
 * provider explicitly defines a value for that feature, the value of the flag is returned.
 */
object RuntimeBehavior {

    internal val providers = IsoMutableList<FeatureFlagProvider>()

    fun isFeatureEnabled(feature: Feature): Boolean {
        return providers
            .filter { it.hasFeature(feature) }
            .minByOrNull(FeatureFlagProvider::priority)
            ?.isFeatureEnabled(feature)
            ?: feature.defaultValue
    }

    suspend fun fetchFeatureFlags(forceRefresh: Boolean) {
        val remoteProviders =
            providers
                .filter { it is RemoteFeatureFlagProvider }.toList()

        remoteProviders
            .forEach { (it as RemoteFeatureFlagProvider).fetch(forceRefresh) }
    }

    suspend fun activateFeatureFlags() {
        val remoteProviders =
            providers
                .filter { it is RemoteFeatureFlagProvider }.toList()

        remoteProviders
            .filter { it is RemoteFeatureFlagProvider }
            .forEach { (it as RemoteFeatureFlagProvider).activate() }
    }

    fun addProvider(provider: FeatureFlagProvider) {
        providers.add(provider)
    }

    fun clearFeatureFlagProviders() = providers.clear()

    fun removeAllFeatureFlagProviders(priority: Int) = providers.removeAll(providers.filter { it.priority == priority })
}
