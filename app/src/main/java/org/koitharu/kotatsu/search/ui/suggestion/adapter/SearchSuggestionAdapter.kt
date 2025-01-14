package org.koitharu.kotatsu.search.ui.suggestion.adapter

import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import coil.ImageLoader
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import org.koitharu.kotatsu.search.ui.suggestion.SearchSuggestionListener
import org.koitharu.kotatsu.search.ui.suggestion.model.SearchSuggestionItem
import kotlin.jvm.internal.Intrinsics

const val SEARCH_SUGGESTION_ITEM_TYPE_QUERY = 0

class SearchSuggestionAdapter(
	coil: ImageLoader,
	lifecycleOwner: LifecycleOwner,
	listener: SearchSuggestionListener,
) : AsyncListDifferDelegationAdapter<SearchSuggestionItem>(DiffCallback()) {

	init {
		delegatesManager
			.addDelegate(SEARCH_SUGGESTION_ITEM_TYPE_QUERY, searchSuggestionQueryAD(listener))
			.addDelegate(searchSuggestionHeaderAD(listener))
			.addDelegate(searchSuggestionTagsAD(listener))
			.addDelegate(searchSuggestionMangaListAD(coil, lifecycleOwner, listener))
	}

	private class DiffCallback : DiffUtil.ItemCallback<SearchSuggestionItem>() {

		override fun areItemsTheSame(
			oldItem: SearchSuggestionItem,
			newItem: SearchSuggestionItem,
		): Boolean = when {
			oldItem is SearchSuggestionItem.RecentQuery && newItem is SearchSuggestionItem.RecentQuery -> {
				oldItem.query == newItem.query
			}
			else -> oldItem.javaClass == newItem.javaClass
		}

		override fun areContentsTheSame(
			oldItem: SearchSuggestionItem,
			newItem: SearchSuggestionItem,
		): Boolean = Intrinsics.areEqual(oldItem, newItem)
	}
}