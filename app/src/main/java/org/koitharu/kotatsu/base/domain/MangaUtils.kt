package org.koitharu.kotatsu.base.domain

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import okhttp3.OkHttpClient
import okhttp3.Request
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koitharu.kotatsu.core.network.CommonHeaders
import org.koitharu.kotatsu.core.parser.MangaRepository
import org.koitharu.kotatsu.parsers.model.MangaPage
import org.koitharu.kotatsu.parsers.util.await
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile
import kotlin.math.roundToInt

object MangaUtils : KoinComponent {

	private const val MIN_WEBTOON_RATIO = 2

	/**
	 * Automatic determine type of manga by page size
	 * @return ReaderMode.WEBTOON if page is wide
	 */
	suspend fun determineMangaIsWebtoon(pages: List<MangaPage>): Boolean {
		val pageIndex = (pages.size * 0.3).roundToInt()
		val page = requireNotNull(pages.getOrNull(pageIndex)) { "No pages" }
		val url = MangaRepository(page.source).getPageUrl(page)
		val uri = Uri.parse(url)
		val size = if (uri.scheme == "cbz") {
			runInterruptible(Dispatchers.IO) {
				val zip = ZipFile(uri.schemeSpecificPart)
				val entry = zip.getEntry(uri.fragment)
				zip.getInputStream(entry).use {
					getBitmapSize(it)
				}
			}
		} else {
			val request = Request.Builder()
				.url(url)
				.get()
				.header(CommonHeaders.REFERER, page.referer)
				.cacheControl(CommonHeaders.CACHE_CONTROL_DISABLED)
				.build()
			get<OkHttpClient>().newCall(request).await().use {
				runInterruptible(Dispatchers.IO) {
					getBitmapSize(it.body?.byteStream())
				}
			}
		}
		return size.width * MIN_WEBTOON_RATIO < size.height
	}

	suspend fun getImageMimeType(file: File): String? = runInterruptible(Dispatchers.IO) {
		val options = BitmapFactory.Options().apply {
			inJustDecodeBounds = true
		}
		BitmapFactory.decodeFile(file.path, options)?.recycle()
		options.outMimeType
	}

	private fun getBitmapSize(input: InputStream?): Size {
		val options = BitmapFactory.Options().apply {
			inJustDecodeBounds = true
		}
		BitmapFactory.decodeStream(input, null, options)?.recycle()
		val imageHeight: Int = options.outHeight
		val imageWidth: Int = options.outWidth
		check(imageHeight > 0 && imageWidth > 0)
		return Size(imageWidth, imageHeight)
	}
}