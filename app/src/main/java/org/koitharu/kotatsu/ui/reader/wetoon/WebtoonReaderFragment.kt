package org.koitharu.kotatsu.ui.reader.wetoon

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_reader_webtoon.*
import moxy.ktx.moxyPresenter
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.core.model.MangaPage
import org.koitharu.kotatsu.ui.reader.*
import org.koitharu.kotatsu.utils.ext.callOnScrollListeners
import org.koitharu.kotatsu.utils.ext.doOnCurrentItemChanged
import org.koitharu.kotatsu.utils.ext.firstItem

class WebtoonReaderFragment : BaseReaderFragment(R.layout.fragment_reader_webtoon),
	OnBoundsScrollListener {

	private val presenter by moxyPresenter(factory = ReaderPresenter.Companion::getInstance)

	private var adapter: WebtoonAdapter? = null
	private lateinit var loader: PageLoader

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		loader = PageLoader()
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		adapter = WebtoonAdapter(loader)
		recyclerView.setHasFixedSize(true)
		recyclerView.adapter = adapter
		recyclerView.addOnScrollListener(ListPaginationListener(2, this))
		recyclerView.doOnCurrentItemChanged(::notifyPageChanged)
	}

	override fun onPagesLoaded(chapterId: Long, pages: List<MangaPage>, action: ReaderAction) {
		super.onPagesLoaded(chapterId, pages, action)
		when(action) {
			ReaderAction.REPLACE -> {
				adapter?.let {
					it.replaceData(pages)
					lastState?.let { state ->
						if (chapterId == state.chapterId) {
							recyclerView.firstItem = state.page
						}
					}
				}
			}
			ReaderAction.PREPEND -> adapter?.prependData(pages)
			ReaderAction.APPEND -> adapter?.appendData(pages)
		}
		recyclerView.callOnScrollListeners()
	}

	override fun onScrolledToStart() {
		val prevChapterId = getPrevChapterId()
		if (prevChapterId != 0L) {
			presenter.loadChapter(lastState?.manga ?: return, prevChapterId, ReaderAction.PREPEND)
		}
	}

	override fun onScrolledToEnd() {
		val nextChapterId = getNextChapterId()
		if (nextChapterId != 0L) {
			presenter.loadChapter(lastState?.manga ?: return, nextChapterId, ReaderAction.APPEND)
		}
	}

	override fun onDestroy() {
		loader.dispose()
		super.onDestroy()
	}

	override val hasItems: Boolean
		get() = adapter?.hasItems == true

	override val currentPageIndex: Int
		get() = recyclerView.firstItem

	override val pages: List<MangaPage>
		get() = adapter?.items.orEmpty()

	override fun setCurrentPage(index: Int, smooth: Boolean) {
		if (smooth) {
			recyclerView.smoothScrollToPosition(index)
		} else {
			recyclerView.firstItem = index
		}
	}
}