package com.kan.dev.familyhealth.ui.activity.gps

import android.media.MediaPlayer
import android.view.animation.Animation
import com.arsy.maps_library.MapRipple
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ActivityGpsactivityBinding
import com.lvt.ads.util.Admob
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GPSActivity : BaseActivity<ActivityGpsactivityBinding>(),OnMapReadyCallback {
    override fun setViewBinding(): ActivityGpsactivityBinding {
        return ActivityGpsactivityBinding.inflate(layoutInflater)
    }

    private var map: GoogleMap? = null
    private var latLngList: List<LatLng> = ArrayList()
    private var mapBottomBehavior: BottomSheetBehavior<*>? = null
    private var detailBottomBehavior:BottomSheetBehavior<*>? = null
    private lateinit var friendModel: FriendModel
    private val mapRipple: MapRipple? = null

    var userSos: Map<String, Any> = HashMap()
    var markerMap: Marker? = null
    var mediaPlayer: MediaPlayer? = null
    var animation: Animation? = null
    private val isTracking = false
    private val isSos = false
    override fun initData() {
        
    }

    override fun initView() {
        Admob.getInstance().loadInterAll(this, getString(R.string.inter_all))
        Admob.getInstance().loadCollapsibleBanner(this, getString(R.string.banner_collap), "bottom")
    }

    override fun initListener() {
        
    }
    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }
    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

}