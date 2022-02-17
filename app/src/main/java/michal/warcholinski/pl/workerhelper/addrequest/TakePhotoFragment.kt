package michal.warcholinski.pl.workerhelper.addrequest

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import michal.warcholinski.pl.workerhelper.databinding.FragmentTakePictureBinding
import michal.warcholinski.pl.workerhelper.extension.showELog
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Michał Warcholiński on 2022-01-04.
 */
@AndroidEntryPoint
class TakePhotoFragment : Fragment() {

	private var imageCapture: ImageCapture? = null
	private lateinit var outputDirectory: File
	private lateinit var cameraExecutor: ExecutorService

	private val args: TakePhotoFragmentArgs by navArgs()

	private var _binding: FragmentTakePictureBinding? = null
	private val binding get() = _binding!!

	private val viewModel: TakePhotoViewModel by viewModels()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		_binding = FragmentTakePictureBinding.inflate(inflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		outputDirectory = getOutputDirectory()
		cameraExecutor = Executors.newSingleThreadExecutor()

		startCameraPreview()

		binding.takePhoto.setOnClickListener { takePhoto() }
	}

	private fun getOutputDirectory(): File {
		return viewModel.getLocalAppFilesDir
	}

	private fun startCameraPreview() {
		val cameraPreviewFuture = ProcessCameraProvider.getInstance(requireContext())

		cameraPreviewFuture.addListener({
			val cameraProvider = cameraPreviewFuture.get()

			val preview = Preview.Builder()
				.build()
				.also { preview -> preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider) }

			val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

			try {
				imageCapture = ImageCapture.Builder().build()
				cameraProvider.unbindAll()
				cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
			} catch (e: Exception) {
				e.printStackTrace()
			}

		}, ContextCompat.getMainExecutor(requireContext()))
	}

	private fun takePhoto() {
		val imageCapture = imageCapture ?: return
		val currentDate =
			SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
		val photoFile = File(outputDirectory, "${args.projectName}_${currentDate}.jpg")
		val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

		imageCapture.takePicture(
			outputOptions,
			ContextCompat.getMainExecutor(requireContext()),
			object : ImageCapture.OnImageSavedCallback {
				override fun onError(exc: ImageCaptureException) {

				}

				override fun onImageSaved(output: ImageCapture.OutputFileResults) {
					val savedUri = Uri.fromFile(photoFile)
					findNavController().previousBackStackEntry?.savedStateHandle?.set("file_uri", savedUri)
					findNavController().popBackStack()
				}
			}
		)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		cameraExecutor.shutdown()
		_binding = null
	}
}