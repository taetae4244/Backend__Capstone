package com.example.carenectapp

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.carenectapp.ui.theme.CarenectAppTheme
import com.google.firebase.database.*
import net.daum.mf.map.api.*

class MainActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private val elderUid = "elder123" // 예시 UID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapView = MapView(this)

        // Compose로 지도 띄우기
        setContent {
            CarenectAppTheme {
                AndroidView(
                    factory = {
                        FrameLayout(it).apply {
                            addView(mapView)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Firebase 위치 수신 시작
        startListeningToElderLocation()
    }

    private fun startListeningToElderLocation() {
        val ref = FirebaseDatabase.getInstance().getReference("locations/$elderUid")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("lat").getValue(Double::class.java)
                val lng = snapshot.child("lng").getValue(Double::class.java)

                if (lat != null && lng != null) {
                    updateMapMarker(lat, lng)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateMapMarker(lat: Double, lng: Double) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(lat, lng)

        val marker = MapPOIItem().apply {
            itemName = "노인 위치"
            this.mapPoint = mapPoint
            markerType = MapPOIItem.MarkerType.BluePin
        }

        mapView.removeAllPOIItems()
        mapView.addPOIItem(marker)
        mapView.setMapCenterPoint(mapPoint, true)
    }
}
