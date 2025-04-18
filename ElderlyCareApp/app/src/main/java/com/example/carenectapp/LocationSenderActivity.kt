package com.example.carenectapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase

class LocationSenderActivity : AppCompatActivity() {

    private val uid = "my_phone_uid"
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ 레이아웃 설정 (activity_location_sender.xml 있어야 함)
        setContentView(R.layout.activity_location_sender)

        // 위치 클라이언트 초기화
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                sendLocationToFirebase()
                handler.postDelayed(this, 30000)
            }
        })
    }


    private fun sendLocationToFirebase() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }

        locationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val data = mapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude
                )

                // ✅ Firebase에 전송
                FirebaseDatabase.getInstance()
                    .getReference("locations/my_phone_uid")
                    .setValue(data)

                // ✅ Toast 메시지로 알림 표시
                Toast.makeText(
                    this,
                    "위치 전송됨\n위도: ${location.latitude}, 경도: ${location.longitude}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // 위치가 null일 경우도 확인
                Toast.makeText(this, "위치를 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
