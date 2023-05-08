package com.example.bloom_final.ui.camera


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bloom_final.R
import com.example.bloom_final.databinding.FragmentCameraBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var safeContext: Context
    private lateinit var outputDirectory: File

    private val client = OkHttpClient()
    private var apiKey = "gPw5U4bffeCEGa7RZ5fLBrFDKg6Hkpdp1q0XidgGwEPl1yKKdV"

    private var imageCapture: ImageCapture? = null
    private var _binding: FragmentCameraBinding? = null

    private lateinit var cameraExecutor: ExecutorService
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val root: View = binding.root
        return root

        // Set up the listeners for take photo and upload buttons

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = FragmentCameraBinding.inflate(layoutInflater)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.imageCaptureButton.setOnClickListener { takePhoto() }
        binding.imageUploadButton.setOnClickListener { openGallery() }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                sendImageToPlantID(bitmap)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview of camera
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Set up image capture use case
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()

                // Set up configuration for the camera
                val cameraConfig = Camera2Config.defaultConfig()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector,
                    preview, imageCapture
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        // Get a reference to the image capture use case
        val imageCapture = imageCapture ?: return

        // Create timestamped output file to hold the captured image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg")

        // Set up options object to configure the capture session
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up a listener for when image capture is complete
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                Toast.makeText(requireContext(), "Photo capture failed", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                Log.d(TAG, "Photo capture succeeded: $savedUri")
                Toast.makeText(requireContext(), "Photo capture succeeded", Toast.LENGTH_SHORT).show()

                // Load the captured image into the image view
                binding.previewImage.setImageURI(savedUri)

                // Upload the image to Plant.ID API
                val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                sendImageToPlantID(bitmap)
            }
        })
    }
    private fun sendImageToPlantID(bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val requestBody = byteArrayOutputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.plant.id/v2/identify")
            .post(requestBody)
            .header("Content-Type", "application/json")
            .header("Api-Key", apiKey)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error sending image to Plant.ID API", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                var plantName = ""
                var confidence = ""

                try {
                    val jsonObject = JSONObject(responseBody!!)
                    val suggestionsArray = jsonObject.getJSONArray("suggestions")

                    if (suggestionsArray.length() > 0) {
                        val firstSuggestion = suggestionsArray.getJSONObject(0)
                        plantName = firstSuggestion.getString("plant_name")
                        confidence = String.format(
                            "%.2f",
                            firstSuggestion.getDouble("probability") * 100
                        ) + "%"
                    } else {
                        plantName = "Unknown Plant"
                        confidence = "0.00%"
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing JSON response from Plant.ID API", e)
                }

                requireActivity().runOnUiThread {
                    if (isAdded) { // Check the fragment is added before updating UI
                        binding.apply {
                            plantNameTextView.text = plantName
                            confidenceTextView.text = confidence
                            resultLayout.visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        private val REQUEST_CODE_GALLERY = 1
    }
}

