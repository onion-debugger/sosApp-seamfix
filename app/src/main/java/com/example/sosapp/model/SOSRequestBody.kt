package com.example.sosapp.model

data class SOSRequestBody(
    var phoneNumbers: List<String>,
    var image: String,
    var location: Location,
)
