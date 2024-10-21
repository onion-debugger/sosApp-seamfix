package com.example.sosapp.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sosapp.R
import com.example.sosapp.model.Location
import com.example.sosapp.model.SOSRequestBody
import com.example.sosapp.remote.ApiClient
import com.example.sosapp.util.Resource
import com.example.sosapp.util.convertBitMapToBase64String
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _sendSOS = MutableLiveData<Boolean>()
    val sendSOS: LiveData<Boolean> = _sendSOS

    private val _responseMessage = MutableLiveData<Resource<String>>()
    val responseMessage: LiveData<Resource<String>> = _responseMessage

    private val _permissionGranted = MutableLiveData<Boolean>()
    val permissionGranted: LiveData<Boolean> = _permissionGranted

    fun setPermissionStatus(isPermissionGranted: Boolean) {
        _permissionGranted.value = isPermissionGranted
    }


    fun sendSOSAlertToEmergencyContact(
        bitmap: Bitmap,
        location: Location,
        context: Context
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _sendSOS.postValue(true)

                // Convert the Image bitMap to base64 String
                val base64ImageString = bitmap.convertBitMapToBase64String()

                val sosRequestBody = SOSRequestBody(
                    phoneNumbers = listOf("08023073102", "09134578324"),
                    image = base64ImageString,
                    location = Location(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )

                val response = ApiClient.apiService.sendSOSAlert(body = sosRequestBody)

                when(response) {
                    is Resource.Success -> _responseMessage.postValue(
                        Resource.Success(data = response.data)
                    )

                    is Resource.Error -> _responseMessage.postValue(
                        Resource.Error(message = response.message ?: getString(
                            context,
                            R.string.failed_to_send_SOS_alert
                        ))
                    )
                }

            } catch (e: Exception) {
                _responseMessage.postValue(Resource.Error("Error: ${e.localizedMessage}"))
            } finally {
                _sendSOS.postValue(false)
            }
        }
    }


}