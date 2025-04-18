package com.example.carenectapp

import android.os.Bundle
import android.util.Log

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
    private val elderUid = "my_phone_uid"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().getReference("locations/$elderUid")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Firebase 실시간 위치 수신
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lat = snapshot.child("latitude").getValue(Double::class.java)
                val lng = snapshot.child("longitude").getValue(Double::class.java)

                Log.d("MapDebug", "Firebase에서 읽은 위치: $lat, $lng")

                if (lat != null && lng != null) {
                    val location = LatLng(lat, lng)
                    googleMap.clear()
                    googleMap.addMarker(MarkerOptions().position(location).title("현재 위치"))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MapDebug", "Firebase 읽기 실패: ${error.message}")
            }
        })


    }
}
