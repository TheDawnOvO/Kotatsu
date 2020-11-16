package org.koitharu.kotatsu.ui.settings.backup

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.dialog_progress.*
import moxy.ktx.moxyPresenter
import org.koin.android.ext.android.get
import org.koitharu.kotatsu.R
import org.koitharu.kotatsu.core.backup.CompositeResult
import org.koitharu.kotatsu.ui.base.AlertDialogFragment
import org.koitharu.kotatsu.utils.ext.getDisplayMessage
import org.koitharu.kotatsu.utils.ext.toUriOrNull
import org.koitharu.kotatsu.utils.ext.withArgs
import org.koitharu.kotatsu.utils.progress.Progress

class RestoreDialogFragment : AlertDialogFragment(R.layout.dialog_progress), RestoreView {

	@Suppress("unused")
	private val presenter by moxyPresenter {
		RestorePresenter(arguments?.getString(ARG_FILE)?.toUriOrNull(), get())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		textView_title.setText(R.string.restore_backup)
		textView_subtitle.setText(R.string.preparing_)
	}

	override fun onBuildDialog(builder: AlertDialog.Builder) {
		builder.setCancelable(false)
	}

	override fun onError(e: Throwable) {
		AlertDialog.Builder(context ?: return)
			.setNegativeButton(R.string.close, null)
			.setTitle(R.string.error)
			.setMessage(e.getDisplayMessage(resources))
			.show()
		dismiss()
	}

	override fun onLoadingStateChanged(isLoading: Boolean) = Unit

	override fun onProgressChanged(progress: Progress?) {
		with(progressBar) {
			isVisible = true
			isIndeterminate = progress == null
			if (progress != null) {
				this.max = progress.total
				this.progress = progress.value
			}
		}
	}

	override fun onRestoreDone(result: CompositeResult) {
		val builder = AlertDialog.Builder(context ?: return)
		when {
			result.isAllSuccess -> builder.setTitle(R.string.data_restored)
				.setMessage(R.string.data_restored_success)
			result.isAllFailed -> builder.setTitle(R.string.error)
				.setMessage(
					result.failures.map {
						it.getDisplayMessage(resources)
					}.distinct().joinToString("\n")
				)
			else -> builder.setTitle(R.string.data_restored)
				.setMessage(R.string.data_restored_with_errors)
		}
		builder.setPositiveButton(android.R.string.ok, null)
			.show()
		dismiss()
	}

	companion object {

		const val ARG_FILE = "file"
		const val TAG = "RestoreDialogFragment"

		fun newInstance(uri: Uri) = RestoreDialogFragment().withArgs(1) {
			putString(ARG_FILE, uri.toString())
		}
	}
}