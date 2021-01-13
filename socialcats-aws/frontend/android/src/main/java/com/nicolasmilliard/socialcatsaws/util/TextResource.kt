package com.nicolasmilliard.socialcatsaws.util

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

sealed class TextResource {
  companion object { // Just needed for static method factory so that we can keep concrete implementations file private
    fun fromText(text: String): TextResource = SimpleTextResource(text)
    fun fromStringId(@StringRes id: Int): TextResource = IdTextResource(id)
    fun fromPlural(@PluralsRes id: Int, pluralValue: Int): TextResource =
      PluralTextResource(id, pluralValue)
  }
}

private data class SimpleTextResource( // We could also use use inline classes in the future
  val text: String
) : TextResource()

private data class IdTextResource(
  @StringRes val id: Int
) : TextResource()

private data class PluralTextResource(
  @PluralsRes val pluralId: Int,
  val quantity: Int
) : TextResource()

// Note: you can get resources with context.getResources()
@Composable
fun TextResource.toText(): String = when (this) {
  is SimpleTextResource -> this.text
  is IdTextResource -> stringResource(id = this.id)
  is PluralTextResource -> quantityStringResource(this.pluralId, this.quantity)
}

@Composable
fun quantityStringResource(@PluralsRes id: Int, quantity: Int): String {
  val resources = resources()
  return resources.getQuantityString(id, quantity)
}

@Composable
private fun resources(): Resources {
  LocalContext.current
  return LocalContext.current.resources
}
