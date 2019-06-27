package com.nicolasmilliard.socialcats.search

import android.view.View
import com.nicolasmilliard.presentation.UiBinder
import kotlinx.coroutines.channels.SendChannel
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
class SearchUiBinder(
    view: View,
    private val events: SendChannel<SearchPresenter.Event>
) : UiBinder<SearchPresenter.Model>{


    override fun bind(model: SearchPresenter.Model, oldModel: SearchPresenter.Model?) {
        logger.error { "$model" }
    }
}
