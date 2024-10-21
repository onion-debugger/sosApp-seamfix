package com.example.sosapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.sosapp.databinding.ActivityMainBinding
import com.example.sosapp.model.Location
import com.example.sosapp.ui.MainViewModel
import com.example.sosapp.util.Constant.EMPTY_STRING
import com.example.sosapp.util.Constant.PERMISSION_REQUEST_CODE
import com.example.sosapp.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: MainViewModel by viewModels()

    private val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolBar()
        setUpObservers()
        setupCamera()
        setUpSOSButton()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        permissionAccess(isPermissionGranted = hasPermissions())
    }

    private fun setUpSOSButton() {
        binding.fabSOSButton.setOnClickListener {
            if (hasPermissions()) {
                binding.camera.takePicture()
            } else {
                viewModel.setPermissionStatus(false)
            }
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setUpObservers() {
        viewModel.sendSOS.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.fabSOSButton.isEnabled = !loading
        }

        viewModel.responseMessage.observe(this) { response ->
            when(response) {
                is Resource.Success -> {
                    successDialog(response = response.data ?: getString(R.string.success_message))
                }

                is Resource.Error -> {
                    Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                }
            }
        }

        viewModel.permissionGranted.observe(this) { isGranted ->
            binding.fabSOSButton.isEnabled = isGranted
            if (!isGranted) {
                showPermissionRequiredAlert()
            }
        }
    }

    private fun permissionAccess(
        isPermissionGranted: Boolean
    ) {
        if (!isPermissionGranted) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions,
                PERMISSION_REQUEST_CODE
            )
        } else {
            viewModel.setPermissionStatus(isPermissionGranted)
        }
    }

    private fun hasPermissions() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupCamera() {
        binding.camera.setLifecycleOwner(this)
        binding.camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                result.toBitmap { image ->
                    if (image != null) {
                        getCurrentLocationAndSendSOSAlert(image)
                    }
                }
            }
        })
    }

    private fun getCurrentLocationAndSendSOSAlert(image: Bitmap) {
        if (ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        ) {
            lifecycleScope.launch {
                try {
                    val userLocation = fusedLocationClient.lastLocation.result
                    viewModel.sendSOSAlertToEmergencyContact(
                        bitmap = image,
                        location = Location(
                            latitude = userLocation.latitude.toString(),
                            longitude = userLocation.longitude.toString()
                        ),
                        context = this@MainActivity
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            errorMessageDialog(
                response = getString(R.string.location_not_available),
                onPositiveClick = {
                    getCurrentLocationAndSendSOSAlert(image)
                },
                onCancelClick = {
                    viewModel.sendSOSAlertToEmergencyContact(
                        bitmap = image,
                        location = Location(
                            longitude = EMPTY_STRING,
                            latitude = EMPTY_STRING
                        ),
                        context = this@MainActivity
                    )
                }
            )
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            requiredPermissions,
            PERMISSION_REQUEST_CODE
        )
    }

    private fun showPermissionRequiredAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.permission_is_required))
            .setMessage(getString(R.string.permission_needed_msg))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                requestPermission()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun successDialog(response: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.sos_sent))
            .setMessage(response)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.cancel()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun errorMessageDialog(
        response: String,
        onPositiveClick: () -> Unit,
        onCancelClick: (() -> Unit)? = null,
    ) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(response)
            .setMessage(response)
            .setPositiveButton(getString(R.string.retry)) { _, _ ->
                onPositiveClick.invoke()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
                onCancelClick?.invoke()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            viewModel.setPermissionStatus(
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            )
        }

    }

}