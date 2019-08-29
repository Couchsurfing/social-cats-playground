package com.nicolasmilliard.socialcats.search

import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient

@Component(modules = [
    SearchApiModule::class
])
interface SearchApiComponent {
    fun searchService(): SearchService

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance client: OkHttpClient): SearchApiComponent
    }

    companion object : Factory by DaggerSearchApiComponent.factory()
}
