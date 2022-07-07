package org.koitharu.kotatsu.explore.ui.adapter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import coil.ImageLoader
import coil.request.Disposable
import coil.request.ImageRequest
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.base.ui.list.AdapterDelegateClickListenerAdapter
import org.koitharu.kotatsu.base.ui.list.OnListItemClickListener
import org.koitharu.kotatsu.databinding.ItemExploreButtonsBinding
import org.koitharu.kotatsu.databinding.ItemExploreHeaderBinding
import org.koitharu.kotatsu.databinding.ItemExploreSourceBinding
import org.koitharu.kotatsu.explore.ui.model.ExploreItem
import org.koitharu.kotatsu.utils.ext.enqueueWith
import org.koitharu.kotatsu.utils.image.FaviconFallbackDrawable

fun exploreButtonsAD(
	clickListener: View.OnClickListener,
) = adapterDelegateViewBinding<ExploreItem.Buttons, ExploreItem, ItemExploreButtonsBinding>(
	{ layoutInflater, parent -> ItemExploreButtonsBinding.inflate(layoutInflater, parent, false) }
) {

	binding.buttonBookmarks.setOnClickListener(clickListener)
	binding.buttonHistory.setOnClickListener(clickListener)
	binding.buttonLocal.setOnClickListener(clickListener)
	binding.buttonSuggestions.setOnClickListener(clickListener)
}

fun exploreSourcesHeaderAD(
	listener: ExploreListEventListener,
) = adapterDelegateViewBinding<ExploreItem.Header, ExploreItem, ItemExploreHeaderBinding>(
	{ layoutInflater, parent -> ItemExploreHeaderBinding.inflate(layoutInflater, parent, false) }
) {

	val listenerAdapter = View.OnClickListener {
		listener.onManageClick(itemView)
	}

	binding.buttonMore.setOnClickListener(listenerAdapter)

	bind {
		binding.textViewTitle.setText(R.string.remote_sources)
	}
}

fun exploreSourceItemAD(
	coil: ImageLoader,
	listener: OnListItemClickListener<ExploreItem.Source>,
	lifecycleOwner: LifecycleOwner,
) = adapterDelegateViewBinding<ExploreItem.Source, ExploreItem, ItemExploreSourceBinding>(
	{ layoutInflater, parent -> ItemExploreSourceBinding.inflate(layoutInflater, parent, false) },
	on = { item, _, _ -> item is ExploreItem.Source }
) {

	var imageRequest: Disposable? = null
	val eventListener = AdapterDelegateClickListenerAdapter(this, listener)

	binding.root.setOnClickListener(eventListener)
	binding.root.setOnLongClickListener(eventListener)

	bind {
		binding.textViewTitle.text = item.source.title
		val fallbackIcon = FaviconFallbackDrawable(context, item.source.name)
		imageRequest = ImageRequest.Builder(context)
			.data(item.faviconUrl)
			.fallback(fallbackIcon)
			.placeholder(fallbackIcon)
			.error(fallbackIcon)
			.target(binding.imageViewCover)
			.lifecycle(lifecycleOwner)
			.enqueueWith(coil)
	}

	onViewRecycled {
		imageRequest?.dispose()
		imageRequest = null
	}
}