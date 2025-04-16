package com.example.carenectapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.android.gms.maps.CameraUpdateFactory


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var database: DatabaseReference
    private val elderUid = "elder_busan_001" // 새 노인의 UID
    // Firebase에 등록된 노인 UID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Firebase Database 경로 설정
        database = FirebaseDatabase.getInstance().getReference("locations/$elderUid")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as? SupportMapFragment

        mapFragment?.getMapAsync(this)


    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Firebase 실시간 위치 수신
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val lat = snapshot.child("latitude").getValue(Double::class.java)
                 val lng = snapshot.child("longitude").getValue(Double::class.java)

                 if (lat != null && lng != null) {
                    val location = LatLng(lat, lng)
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(location).title("노인의 위치"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
