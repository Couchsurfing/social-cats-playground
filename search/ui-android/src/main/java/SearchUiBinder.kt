package com.nicolasmilliard.socialcats.search

import android.view.KeyEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.nicolasmilliard.presentation.UiBinder
import com.nicolasmilliard.socialcats.model.User
import com.nicolasmilliard.socialcats.search.SearchPresenter.Event
import com.nicolasmilliard.socialcats.search.SearchPresenter.Event.ClearSyncStatus
import com.nicolasmilliard.socialcats.search.SearchPresenter.Model.LoadingStatus.*
import com.nicolasmilliard.socialcats.search.ui.R
import com.nicolasmilliard.socialcats.search.util.layoutInflater
import com.nicolasmilliard.socialcats.search.util.onEditorAction
import com.nicolasmilliard.socialcats.search.util.onKey
import com.nicolasmilliard.socialcats.search.util.onScroll
import com.nicolasmilliard.socialcats.search.util.onTextChanged
import kotlinx.coroutines.channels.SendChannel
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}
class SearchUiBinder(
    view: View,
    private val events: SendChannel<Event>,
    private val onClick: UserHandler
) : UiBinder<SearchPresenter.Model> {

    private val context = view.context
    private val resources = view.resources

    private val results: RecyclerView = view.findViewById(R.id.results)
    private val queryInput: EditText = view.findViewById(R.id.query)
    private val queryClear: View = view.findViewById(R.id.clear_query)

    private val resultsAdapter = UserResultAdapter(context.layoutInflater, object : UserResultAdapter.Callback {
        override fun onUserClicked(user: User) = onClick(user)
    })

    private var snackbar: Snackbar? = null
    init {
        results.adapter = resultsAdapter

        val layoutManager = LinearLayoutManager(context)
        results.layoutManager = layoutManager
        results.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        TooltipCompat.setTooltipText(queryClear, queryClear.contentDescription)
        queryClear.setOnClickListener {
            queryInput.setText("")
        }

        queryInput.onTextChanged {
            queryClear.isVisible = it.isNotEmpty()

            events.offer(Event.QueryChanged(it.toString()))
        }

        queryInput.onKey {
            if (it.keyCode == KeyEvent.KEYCODE_ENTER) {
                resultsAdapter.invokeFirstItem()
                true
            } else {
                false
            }
        }
        queryInput.onEditorAction {
            if (it == EditorInfo.IME_ACTION_GO) {
                resultsAdapter.invokeFirstItem()
                true
            } else {
                false
            }
        }

        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        var totalDy = 0
        results.onScroll { _, dy ->
            if (dy > 0) {
                totalDy += dy
                if (totalDy >= touchSlop) {
                    totalDy = 0

                    val inputMethodManager = context.getSystemService<InputMethodManager>()!!
                    inputMethodManager.hideSoftInputFromWindow(queryInput.windowToken, HIDE_NOT_ALWAYS)
                }
            }
        }
    }

    override fun bind(model: SearchPresenter.Model, oldModel: SearchPresenter.Model?) {
        logger.info { "Search UI binder bind: $model" }
        val count = model.count
        queryInput.hint = resources.getQuantityString(R.plurals.search_classes, count.toInt(), count)

        val queryResults = model.queryResults
        val itemResults = queryResults.items.map { UserResult(queryResults.query, it) }
        resultsAdapter.submitList(itemResults) {
            // Always reset the scroll position to the top when the query changes.
            results.scrollToPosition(0)
        }

        if (model.loadingStatus != IDLE) {
            val message = if (model.loadingStatus == LOADING) R.string.updating else R.string.updating_failed

            var snackbar = this.snackbar
            if (snackbar == null) {
                snackbar = Snackbar.make(results, message, LENGTH_INDEFINITE)
                snackbar.show()
                this.snackbar = snackbar
            } else {
                snackbar.setText(message)
            }

            if (model.loadingStatus == FAILED) {
                snackbar.setAction(R.string.dismiss) {
                    events.offer(ClearSyncStatus)
                }
            }
        } else {
            snackbar?.dismiss()
        }
    }
}
