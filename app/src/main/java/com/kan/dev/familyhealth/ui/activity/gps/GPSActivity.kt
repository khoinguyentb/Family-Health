@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.kan.dev.familyhealth.ui.activity.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.provider.Settings
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.helper.widget.MotionEffect
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.arsy.maps_library.MapRipple
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.kan.dev.familyhealth.R
import com.kan.dev.familyhealth.adapter.DetailListener
import com.kan.dev.familyhealth.adapter.DetailListeners
import com.kan.dev.familyhealth.adapter.FriendAdapter
import com.kan.dev.familyhealth.adapter.FriendDetailAdapter
import com.kan.dev.familyhealth.adapter.OnclickStopListener
import com.kan.dev.familyhealth.adapter.PlaceAdapter
import com.kan.dev.familyhealth.adapter.PlaceListener
import com.kan.dev.familyhealth.adapter.SosAdapter
import com.kan.dev.familyhealth.base.BaseActivity
import com.kan.dev.familyhealth.data.Data.Companion.placeList
import com.kan.dev.familyhealth.data.RealtimeDAO
import com.kan.dev.familyhealth.data.RealtimeDAO.getOnetimeData
import com.kan.dev.familyhealth.data.RealtimeDAO.updateRealtimeData
import com.kan.dev.familyhealth.data.model.FriendModel
import com.kan.dev.familyhealth.databinding.ActivityGpsactivityBinding
import com.kan.dev.familyhealth.ui.activity.main.DetailInformationActivity
import com.kan.dev.familyhealth.ui.activity.main.FriendActivity
import com.kan.dev.familyhealth.ui.activity.PermissionActivity.Companion.LOCATION
import com.kan.dev.familyhealth.ui.activity.interaction.ShareInformationActivity
import com.kan.dev.familyhealth.utils.CODE_LENGTH
import com.kan.dev.familyhealth.utils.ClickDialogListener
import com.kan.dev.familyhealth.utils.DEFAULT_LATLNG
import com.kan.dev.familyhealth.utils.DISABLE
import com.kan.dev.familyhealth.utils.DialogListener
import com.kan.dev.familyhealth.utils.DialogUtils
import com.kan.dev.familyhealth.utils.ENABLE
import com.kan.dev.familyhealth.utils.KEY_MAP
import com.kan.dev.familyhealth.utils.LocationHelper.DEFAULT_ZOOM
import com.kan.dev.familyhealth.utils.LocationHelper.FRIEND_ALL
import com.kan.dev.familyhealth.utils.LocationHelper.FRIEND_ONE
import com.kan.dev.familyhealth.utils.LocationHelper.convertToLatLng
import com.kan.dev.familyhealth.utils.LocationHelper.findLargestLatLngBounds
import com.kan.dev.familyhealth.utils.LocationHelper.getAddressFromLatLng
import com.kan.dev.familyhealth.utils.LocationHelper.getCoordinate
import com.kan.dev.familyhealth.utils.LocationHelper.getCurrentLocation
import com.kan.dev.familyhealth.utils.LocationHelper.iconMarker
import com.kan.dev.familyhealth.utils.LocationHelper.iconMarkerSos
import com.kan.dev.familyhealth.utils.LocationHelper.isLocationEnabled
import com.kan.dev.familyhealth.utils.MY_CODE
import com.kan.dev.familyhealth.utils.checkPerList
import com.kan.dev.familyhealth.utils.checkPermissionLocation
import com.kan.dev.familyhealth.utils.formatDateFromLong
import com.kan.dev.familyhealth.utils.handler
import com.kan.dev.familyhealth.utils.isClick
import com.kan.dev.familyhealth.viewmodel.FriendViewModel
import com.lvt.ads.callback.InterCallback
import com.lvt.ads.util.Admob
import com.lvt.ads.util.AppOpenManager
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Locale

@Suppress("NAME_SHADOWING", "DEPRECATION")
@AndroidEntryPoint
class GPSActivity : BaseActivity<ActivityGpsactivityBinding>(),OnMapReadyCallback,
    OnclickStopListener, DetailListener,PlaceListener,DetailListeners {
    override fun setViewBinding(): ActivityGpsactivityBinding {
        return ActivityGpsactivityBinding.inflate(layoutInflater)
    }
    private val viewModel : FriendViewModel by viewModels()
    private var map: GoogleMap? = null
    private var latLngList: ArrayList<LatLng> = ArrayList()
    private var mapBottomBehavior: BottomSheetBehavior<*>? = null
    private var detailBottomBehavior:BottomSheetBehavior<*>? = null
    private var mapRipple: MapRipple? = null
    private val myCode : String by lazy {
        sharePre.getString(MY_CODE,"")!!
    }
    private val sosList: ArrayList<FriendModel> = ArrayList()
    private var temp = 0
    private var sosStatus: String = DISABLE
    private var runnable: Runnable? = null
    private var userSos = mutableMapOf<String, Any>()
    private var markerMap: Marker? = null
    private var mediaPlayer: MediaPlayer? = null
    private var animation: Animation? = null
    private var isTracking = false
    private var isSos = false
    private var checkPut = 0
    private var perDialog: Dialog? = null
    private val sosAdapter :SosAdapter by lazy {
        SosAdapter(this,this)
    }
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var friendAdapter: FriendAdapter
    private lateinit var friendDetailAdapter: FriendDetailAdapter
    private var mapTypeDialog: Dialog? = null
    private val friendDefault : FriendModel by lazy {
        FriendModel("00000000",0,0,"name",0f,0f,"","","","",
            checkCm = true,
            checkSt = true,
            checkKg = true,
            checkLb = true,
            latLng = "",
            visible = true,
            isSos = true,
            statusSos = true,
            isTracking = true,
            lastActive = 0L,
            online = true
        )
    }
    override fun initData() {
        RealtimeDAO.initRealtimeData()
    }

    override fun initView() {
        Admob.getInstance().loadInterAll(this, getString(R.string.inter_all))
        Admob.getInstance().loadCollapsibleBanner(this, getString(R.string.banner_collap), "bottom")
        initDialog()
        getFriend()
        getPlace()
        initDialog()
        actionSearchFriend()
        initAdapter()
    }

    override fun initListener() {

        binding.apply {
            btnChooseMapType.setOnClickListener {
                mapTypeDialog!!.show()
                binding.includeBanner.visibility = View.INVISIBLE
                DialogUtils.initView(this@GPSActivity)
            }
            btnSos.setOnClickListener { actionOpenSos() }
            btnScanMap.setOnClickListener { actionShowFriendAll(true) }
            viewSOS.btnActive.setOnClickListener {actionClickSos() }
            viewSos.tvTurnOffNotification.setOnClickListener {
                try {
                    for (fri in sosList) {
                        val user: MutableMap<String, Any> =
                            java.util.HashMap()
                        user["statusSos"] = false
                        updateRealtimeData(
                            myCode + "/friends/" + fri.code,
                            user
                        ) { _ ->
                            val marker = map!!.addMarker(
                                MarkerOptions().position(convertToLatLng(fri.latLng))
                                    .icon(iconMarker(this@GPSActivity, fri.avt))
                            )
                            marker!!.tag = fri.code
                        }
                    }
                    sosList.clear()
                    stopSOS()
                } catch (e: java.lang.Exception) {
                    sosList.clear()
                    e.printStackTrace()
                }
            }
            mapBottomSheet.btnSettings.setOnClickListener {
                if (isClick) {
                    isClick = false
                    startActivity(Intent(applicationContext, SettingGPSActivity::class.java))
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
            mapBottomSheet.btnShareCode.setOnClickListener {
                if (isClick) {
                    isClick = false
                    startActivity(
                        Intent(
                            applicationContext,
                            ShareInformationActivity::class.java
                        ).putExtra("settings", true)
                    )
                    Handler().postDelayed({ isClick = true }, 500)
                }
            }
            mapBottomSheet.btnViewAddFriend.setOnClickListener {
                if (isClick) {
                    isClick = false
                    Admob.getInstance().showInterAll(this@GPSActivity, object : InterCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            startActivity(
                                Intent(
                                    applicationContext,
                                    FriendActivity::class.java
                                )
                            )
                        }
                    })
                    Handler().postDelayed({ isClick = true }, 500)
                }
            }
            mapBottomSheet.btnViewPlace.setOnClickListener {
                if (isClick) {
                    isClick = false
                    Admob.getInstance().showInterAll(this@GPSActivity, object : InterCallback() {
                        override fun onNextAction() {
                            super.onNextAction()
                            startActivity(Intent(applicationContext, PlaceActivity::class.java).putExtra("main", true))
                        }
                    })
                    handler.postDelayed({ isClick = true }, 500)
                }
            }
        }

    }

    private fun check() {
        if (checkPerList(LOCATION)) {
            initMap()
            perDialog!!.dismiss()
        } else {
            perDialog!!.show()
        }
    }
    override fun onResume() {
        super.onResume()
        check()
        AppOpenManager.getInstance().enableAppResumeWithActivity(GPSActivity::class.java)
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
        }
        if (runnable != null) {
            handler.removeCallbacks(runnable!!)
            handler.postDelayed(runnable!!, 10000)
        }
        if (!isLocationEnabled(this@GPSActivity)) {
            val builder = AlertDialog.Builder(this)
            val buttonText = getString(R.string.Ok)
            val spannableButtonString = SpannableString(buttonText)
            spannableButtonString.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this,
                        R.color.blue_049DEF
                    )
                ), 0, buttonText.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            builder.setMessage(getString(R.string.dialog_location))
                .setCancelable(false)
                .setPositiveButton(
                    spannableButtonString
                ) { dialog, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        getFriend()
        initAdapter()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        checkSos()
    }
    override fun onDestroy() {
        val user: MutableMap<String, Any> = java.util.HashMap()
        user["isSos"] = false
        updateRealtimeData(myCode, user) { _ -> }
        handler.removeCallbacks(runnable!!)
        super.onDestroy()
    }
    private fun initMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_Fragment) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }
    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        mapRipple = MapRipple(map, DEFAULT_LATLNG, this)
        map!!.mapType = sharePre.getInt(KEY_MAP, 1)
        map!!.uiSettings.isZoomControlsEnabled = false
        map!!.uiSettings.isMyLocationButtonEnabled = false
        map!!.uiSettings.isCompassEnabled = false
        map!!.uiSettings.isMapToolbarEnabled = false

        runnable = object : Runnable {
            override fun run() {
                Log.e("Check", checkPut.toString())
                actionShowFriendAll(false)
                checkPut++
                checkOnline()
                if (ActivityCompat.checkSelfPermission(
                        this@GPSActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@GPSActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                getCurrentLocation(applicationContext) { latLng ->
                    val oldLatLng = convertToLatLng(latLng.toString())
                    getOnetimeData(myCode) { snapshot ->
                        val avt = snapshot!!.child("avt").getValue(Int::class.java) ?: 0
                        val marker = map!!.addMarker(
                            MarkerOptions().position(oldLatLng).icon(iconMarker(this@GPSActivity, avt))
                        )
                        marker?.tag = myCode
                    }
                }
                handler.postDelayed(this, 15000)
            }
        }
        actionShowFriendAll(true)
        getCurrentLocation(applicationContext) { latLng ->
            val oldLatLng = convertToLatLng(latLng.toString())
            getOnetimeData(myCode) { snapshot ->
                val avt = snapshot!!.child("avt").getValue(Int::class.java)!!
                val marker = map!!.addMarker(
                    MarkerOptions().position(oldLatLng).icon(iconMarker(this@GPSActivity, avt))
                )
                marker!!.tag = myCode
                moveCamera(FRIEND_ONE, latLng as LatLng)
            }
        }
        handler.postDelayed(runnable!!, 15000)
        if (intent.getStringExtra("place") != null) {
            val place = intent.getStringExtra("place")
            Log.d("getIntent", "getIntent$place")
            actionShowNearbyPlace(place!!)
        }

        map!!.setOnMarkerClickListener { marker: Marker ->
            val tag = marker.tag.toString()
            actionShowDetail(tag)
            false
        }
    }
    private fun initDialog(){
        perDialog = DialogUtils.initPerDialog(this, object : DialogListener {
            override fun onGrant() {
                var check = false
                if (checkPermissionLocation()){
                    check = true
                }
                if (check) {
                    AppOpenManager.getInstance()
                        .disableAppResumeWithActivity(GPSActivity::class.java)
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.setData(uri)
                    startActivity(intent)
                } else {
                    initMap()
                    perDialog!!.dismiss()
                    AppOpenManager.getInstance()
                        .enableAppResumeWithActivity(GPSActivity::class.java)
                }
            }
            override fun onDismiss() {}
        })

        mapTypeDialog = DialogUtils.initMapTypeDialog(this, object : ClickDialogListener {
            override fun onClick(type: Int) {
                mapTypeDialog!!.dismiss()
                DialogUtils.initView(this@GPSActivity)
                sharePre.putInt(KEY_MAP, type)
                map!!.mapType = type
                binding.includeBanner.visibility = View.VISIBLE
            }

            override fun onClose() {
                binding.includeBanner.visibility = View.VISIBLE
                DialogUtils.initView(this@GPSActivity)
            }
        })
    }
    private fun initAdapter() {
        viewModel.getAll.observe(this){
            binding.mapBottomSheet.recyclerOnlineFriend.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL, false
            )
            binding.mapBottomSheet.recyclerOnlineFriend.setHasFixedSize(true)

            friendAdapter = FriendAdapter(this,"online",this)
            friendAdapter.setItems(it)
            friendAdapter.addItem(friendDefault,0)
            friendAdapter.addItem(friendDefault,0)
            binding.mapBottomSheet.recyclerOnlineFriend.adapter = friendAdapter
            binding.mapBottomSheet.recyclerOnlineFriend.itemAnimator = null
            binding.mapBottomSheet.recyclerFriend.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            binding.mapBottomSheet.recyclerFriend.setHasFixedSize(true)

            friendAdapter = FriendAdapter(this,"main",this)
            friendAdapter.setItems(it)
            friendAdapter.addItem(friendDefault,0)
            friendAdapter.addItem(friendDefault,0)
            binding.mapBottomSheet.recyclerFriend.adapter = friendAdapter
            binding.mapBottomSheet.recyclerFriend.itemAnimator = null
        }

        binding.mapBottomSheet.recyclerPlace.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.mapBottomSheet.recyclerPlace.setHasFixedSize(true)
        placeAdapter = PlaceAdapter(this,this)
        placeAdapter.setItems(placeList)
        binding.mapBottomSheet.recyclerPlace.adapter = placeAdapter
        binding.mapBottomSheet.recyclerPlace.itemAnimator = null
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun actionSearchFriend() {
        binding.mapBottomSheet.edtSearch.setOnClickListener {
            if (mapBottomBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED || mapBottomBehavior!!.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                mapBottomBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
                    @SuppressLint("SwitchIntDef")
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        when (newState) {
                            BottomSheetBehavior.STATE_EXPANDED -> {
                                binding.mapBottomSheet.online.visibility = View.INVISIBLE
                                binding.mapBottomSheet.layoutPlace.visibility = View.VISIBLE
                                binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                            }
                            BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                                binding.mapBottomSheet.online.visibility = View.INVISIBLE
                                binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                                binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                            }
                            BottomSheetBehavior.STATE_COLLAPSED -> {
                                binding.mapBottomSheet.online.visibility = View.VISIBLE
                                binding.mapBottomSheet.layoutFriend.visibility = View.INVISIBLE
                                binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                                val imm =
                                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(
                                    binding.mapBottomSheet.edtSearch.windowToken,
                                    0
                                )
                            }
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        if (slideOffset > 0.5) {
                            mapBottomBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                        } else {
                            mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                            mapBottomBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                        }
                    }
                })
                mapBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        binding.mapBottomSheet.edtSearch.onFocusChangeListener =
            OnFocusChangeListener { _, hasFocus ->
                Log.e("EdtSearch", hasFocus.toString())
                if (hasFocus) {
                    if (mapBottomBehavior!!.state == BottomSheetBehavior.STATE_COLLAPSED || mapBottomBehavior!!.state == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                        mapBottomBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
                            @SuppressLint("SwitchIntDef")
                            override fun onStateChanged(bottomSheet: View, newState: Int) {
                                when (newState) {
                                    BottomSheetBehavior.STATE_EXPANDED -> {
                                        binding.mapBottomSheet.online.visibility = View.INVISIBLE
                                        binding.mapBottomSheet.layoutPlace.visibility = View.VISIBLE
                                        binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                                    }
                                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                                        binding.mapBottomSheet.online.visibility = View.INVISIBLE
                                        binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                                        binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                                    }
                                    BottomSheetBehavior.STATE_COLLAPSED -> {
                                        binding.mapBottomSheet.online.visibility = View.VISIBLE
                                        binding.mapBottomSheet.layoutFriend.visibility = View.INVISIBLE
                                        binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                                        val imm =
                                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                                        imm.hideSoftInputFromWindow(
                                            binding.mapBottomSheet.edtSearch.windowToken,
                                            0
                                        )
                                    }
                                }
                            }

                            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                                if (slideOffset > 0.5) {
                                    mapBottomBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                                } else {
                                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                                    mapBottomBehavior!!.setState(BottomSheetBehavior.STATE_EXPANDED)
                                }
                            }
                        })
                        mapBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            }
        binding.mapBottomSheet.edtSearch.setOnEditorActionListener { textView, i, _ ->
            if (i === EditorInfo.IME_ACTION_DONE) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                val imm =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textView.windowToken, 0)
                getPlace()
                binding.mapBottomSheet.edtSearch.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }
        binding.mapBottomSheet.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val text = charSequence.toString().trim { it <= ' ' }
                if (text != "") {
                    viewModel.getAll.observe(this@GPSActivity){it ->
                        val tempList: MutableList<FriendModel> = ArrayList()
                        for (u in it) {
                            getOnetimeData(myCode + "/friends/" + u.code) { snapshot ->
                                val name =
                                    snapshot!!.child("name").getValue(String::class.java)
                                val nickname =
                                    snapshot.child("nickname").getValue(String::class.java)
                                val phoneNumber =
                                    snapshot.child("phoneNumber").getValue(String::class.java)
                                if (nickname!!.trim { it <= ' ' } == "") {
                                    if (phoneNumber!!.contains(text) || name!!.lowercase(Locale.getDefault())
                                            .contains(text.lowercase(Locale.getDefault()))
                                    ) {
                                        tempList.add(u)
                                    }
                                } else {
                                    if (phoneNumber!!.contains(text) || name!!.lowercase(Locale.getDefault())
                                            .contains(text.lowercase(Locale.getDefault())) || nickname.lowercase(
                                            Locale.getDefault()
                                        ).contains(text.lowercase(Locale.getDefault()))
                                    ) {
                                        tempList.add(u)
                                    }
                                }
                                if (tempList.size == 0) {
                                    binding.mapBottomSheet.tvNull.visibility = View.VISIBLE
                                } else {
                                    binding.mapBottomSheet.tvNull.visibility = View.GONE
                                }
                            }
                        }
                        binding.mapBottomSheet.rcvSearch.layoutManager = LinearLayoutManager(
                            this@GPSActivity,
                            LinearLayoutManager.VERTICAL, false
                        )
                        binding.mapBottomSheet.rcvSearch.setHasFixedSize(true)
                        if (tempList.size == 0) {
                            binding.mapBottomSheet.tvNull.visibility = View.VISIBLE
                        } else {
                            binding.mapBottomSheet.tvNull.visibility = View.GONE
                        }
                        friendDetailAdapter = FriendDetailAdapter(this@GPSActivity,this@GPSActivity)
                        friendDetailAdapter.setItems(tempList)
                        binding.mapBottomSheet.rcvSearch.itemAnimator = null
                        binding.mapBottomSheet.rcvSearch.visibility = View.VISIBLE
                        binding.mapBottomSheet.recyclerFriend.visibility = View.INVISIBLE
                    }

                } else {
                    binding.mapBottomSheet.tvNull.visibility = View.GONE
                    binding.mapBottomSheet.rcvSearch.visibility = View.INVISIBLE
                    binding.mapBottomSheet.recyclerFriend.visibility = View.VISIBLE
                    initAdapter()
                }
            }
            override fun afterTextChanged(editable: Editable) {}
        })
    }
    private fun actionShowFriendAll(show: Boolean) {
        map!!.clear()
        latLngList.clear()
        getOnetimeData(myCode) { snapshot ->
            val avt = snapshot!!.child("avt").getValue(Int::class.java)!!
            val latLng = snapshot.child("latLng").getValue(String::class.java)
            latLngList.add(convertToLatLng(latLng!!))
            val marker = map!!.addMarker(
                MarkerOptions().position(
                    convertToLatLng(
                        latLng
                    )
                ).icon(iconMarker(this@GPSActivity, avt))
            )
            marker!!.tag = myCode
        }

        viewModel.getAll.observe(this){
            temp = 0
            if (it.isNotEmpty()) {
                for (friend in it) {
                    getOnetimeData(myCode + "/friends/" + friend.code) { snapshot ->
                        try {
                            val friendLatLng =
                                snapshot!!.child("latLng").getValue(String::class.java)
                            val isTracking =
                                snapshot.child("isTracking").getValue(Boolean::class.java)!!
                            val visible =
                                snapshot.child("visible").getValue(Boolean::class.java)!!
                            val isSos =
                                snapshot.child("isSos").getValue(Boolean::class.java)!!
                            val statusSos =
                                snapshot.child("statusSos").getValue(Boolean::class.java)!!
                            if (isTracking) {
                                val friendAvt =
                                    snapshot.child("avt").getValue(Int::class.java)!!
                                val latLng = convertToLatLng(friendLatLng!!)
                                latLngList.add(latLng)
                                if (visible) {
                                    if (isSos) {
                                        if (statusSos) {
                                            temp++
                                            var isAddUser = true
                                            if (sosList.isNotEmpty()) {
                                                for (it in sosList) {
                                                    if (it.code == friend.code) {
                                                        isAddUser = false
                                                        break
                                                    }
                                                }
                                            }
                                            if (isAddUser) {
                                                sosList.add(friend)
                                            }
                                            binding.viewSos.root.visibility = View.VISIBLE
                                            binding.viewSos.root.startAnimation(animation)
                                            mapRipple!!.withLatLng(latLng)
                                            mapRipple!!.withNumberOfRipples(3)
                                            mapRipple!!.withFillColor(Color.RED)
                                            mapRipple!!.withStrokeColor(Color.BLACK)
                                            mapRipple!!.withStrokewidth(10)
                                            mapRipple!!.withDistance(100.0)
                                            mapRipple!!.withRippleDuration(2000)
                                            mapRipple!!.withTransparency(0.5f)
                                            mapRipple!!.stopRippleMapAnimation()
                                            mapRipple!!.startRippleMapAnimation()

                                            binding.viewSos.recyclerView.layoutManager =
                                                LinearLayoutManager(
                                                    this,
                                                    LinearLayoutManager.VERTICAL,
                                                    false
                                                )
                                            binding.viewSos.recyclerView.setHasFixedSize(true)
                                            binding.viewSos.recyclerView.adapter = sosAdapter
                                            binding.viewSos.recyclerView.itemAnimator = null
                                            markerMap = map!!.addMarker(
                                                MarkerOptions().position(latLng)
                                                    .icon(
                                                        iconMarkerSos(
                                                            this@GPSActivity,
                                                            friendAvt
                                                        )
                                                    )
                                            )
                                            markerMap!!.tag = friend.code
                                        } else {
                                            markerMap = map!!.addMarker(
                                                MarkerOptions().position(latLng)
                                                    .icon(iconMarker(this@GPSActivity, friendAvt))
                                            )
                                            markerMap!!.tag = friend.code
                                        }
                                    } else {
                                        binding.viewSos.root.visibility = View.GONE
                                        userSos["statusSos"] = true
                                        updateRealtimeData(
                                            myCode + "/friends/" + friend.code,
                                            userSos
                                        ) { _ -> }
                                        markerMap = map!!.addMarker(
                                            MarkerOptions().position(latLng)
                                                .icon(iconMarker(this@GPSActivity, friendAvt))
                                        )
                                        markerMap!!.tag = friend.code
                                    }
                                }
                                if (show) moveCamera(FRIEND_ALL, null)
                            } else {
                                userSos["isSos"] = false
                                updateRealtimeData(
                                    myCode + "/friends/" + friend.code,
                                    userSos
                                ) { _ -> }
                            }
                            if (temp == 0) {
                                sosList.clear()
                            }
                            stopSOS()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } else {
                binding.viewSos.root.visibility = View.GONE
                sosList.clear()
                stopSOS()
            }
        }
    }
    private fun checkOnline() {

        viewModel.getAll.observe(this){
            for (item in it) {
                getOnetimeData(item.code) { snapshot ->
                    val lastActive =
                        snapshot!!.child("lastActive").getValue(Long::class.java)
                    val friend: MutableMap<String, Any> =
                        java.util.HashMap()
                    if (lastActive == item.lastActive) {
                        friend["online"] = false
                        updateRealtimeData(
                            myCode + "/friends/" + item.code,
                            friend
                        ) { _ -> }
                    } else {
                        friend["online"] = true
                        updateRealtimeData(
                            myCode + "/friends/" + item.code,
                            friend
                        ) { _ -> }
                    }
                }
            }
        }
    }
    private fun actionShowDetail(code: String) {
        if (code.length > CODE_LENGTH) {
            try {
                binding.detailBottom.root.visibility = View.VISIBLE
                binding.mapBottomSheet.root.visibility = View.INVISIBLE
                binding.detailBottom.txtActiveTime.visibility = View.GONE
                binding.detailBottom.textView2.visibility = View.GONE
                detailBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                val latLng = convertToLatLng(code)
                binding.detailBottom.txtAddress.text = getAddressFromLatLng(
                    this.applicationContext,
                    latLng
                )
                binding.detailBottom.txtCoordinate.text = getCoordinate(latLng)
                binding.detailBottom.btnClose.setOnClickListener {
                    detailBottomBehavior!!.setState(
                        BottomSheetBehavior.STATE_HIDDEN
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        } else {
            try {
                binding.detailBottom.root.visibility = View.VISIBLE
                binding.mapBottomSheet.root.visibility = View.INVISIBLE
                detailBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                binding.detailBottom.txtActiveTime.visibility = View.VISIBLE
                binding.detailBottom.textView2.visibility = View.VISIBLE
                binding.detailBottom.textView2.text =
                    if (code == myCode) getString(R.string.your_position) else getString(R.string.friend_location)
                if (code == myCode) {
                    getOnetimeData(code) { snapshot ->
                        val latLong =
                            snapshot!!.child("latLng").getValue(String::class.java)
                        val latLng = convertToLatLng(latLong!!)
                        val lastActive =
                            snapshot.child("lastActive").getValue(Long::class.java)
                        binding.detailBottom.txtAddress.text = getAddressFromLatLng(
                            this.applicationContext,
                            latLng
                        )
                        Log.e(MotionEffect.TAG, "actionShowDetail: 1")
                        binding.detailBottom.txtActiveTime.text = formatDateFromLong(lastActive!!)
                        binding.detailBottom.txtCoordinate.text = getCoordinate(latLng)
                    }
                }

                viewModel.getAll.observe(this){
                    it.forEach { friendModel ->
                        if (code == friendModel.code){
                            getOnetimeData(code) { snapshot ->
                                val latLong = snapshot!!.child("latLng").getValue(
                                    String::class.java
                                )
                                val latLng = convertToLatLng(latLong!!)
                                val lastActive =
                                    snapshot.child("lastActive").getValue(Long::class.java)
                                binding.detailBottom.txtAddress.text = getAddressFromLatLng(
                                    this.applicationContext,
                                    latLng
                                )
                                binding.detailBottom.txtActiveTime.text = formatDateFromLong(lastActive!!)
                                binding.detailBottom.txtCoordinate.text = getCoordinate(latLng)
                            }
                            return@observe
                        }
                    }
                }

                binding.detailBottom.btnClose.setOnClickListener {
                    Log.e(MotionEffect.TAG, "actionShowDetail: 2")
                    detailBottomBehavior!!.setState(BottomSheetBehavior.STATE_HIDDEN)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun moveCamera(type: String, latLng: LatLng?) {

        handler.removeCallbacks(runnable!!)
        handler.postDelayed(runnable!!, 10000)
        getPlace()
        when (type) {
            FRIEND_ALL -> {
                val bounds: LatLngBounds = findLargestLatLngBounds(latLngList)
                map!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }

            FRIEND_ONE -> {
                map!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng!!, DEFAULT_ZOOM))
            }
        }
    }
    private fun actionShowNearbyPlace(type: String) {
        Log.e("actionShowNearbyPlace", type)
        map!!.clear()
        mapBottomBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.mapBottomSheet.online.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutPlace.visibility = View.VISIBLE
                        binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.mapBottomSheet.online.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.mapBottomSheet.online.visibility = View.VISIBLE
                        binding.mapBottomSheet.layoutFriend.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.mapBottomSheet.edtSearch.windowToken, 0)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0.5) {
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        })
        getOnetimeData(myCode) { snapshot ->
            handler.removeCallbacks(runnable!!)
            mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
            val latLong = snapshot!!.child("latLng").getValue(String::class.java)
            val latLng = convertToLatLng(latLong!!)
            val requestUrl =
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                        "location=${latLng.latitude},${latLng.longitude}" +
                        "&radius=2000&types=$type" +
                        "&key=AIzaSyAQ4hUxrjJYAtEH_ZlBWug628Of44MRusg"
            try {
                val request = Request.Builder()
                    .url(requestUrl)
                    .build()
                val client = OkHttpClient()
                client.newCall(request).enqueue(object : Callback{
                    override fun onFailure(request: Request?, e: IOException?) {

                    }
                    override fun onResponse(response: Response?) {
                        if (response!!.isSuccessful) {
                            val responseBody = response.body().string()
                            try {
                                val json = JSONObject(responseBody)
                                val results = json.getJSONArray("results")
                                Log.e(MotionEffect.TAG, "actionShowNearby: $results")
                                runOnUiThread {
                                    map!!.clear()
                                    val list: MutableList<LatLng> = java.util.ArrayList()
                                    for (i in 0..4) {
                                        try {
                                            val place = results.getJSONObject(i)
                                            val location =
                                                place.getJSONObject("geometry")
                                                    .getJSONObject("location")
                                            val latitude = location.getDouble("lat")
                                            val longitude = location.getDouble("lng")
                                            markerMap =
                                                map!!.addMarker(
                                                    MarkerOptions().position(
                                                        LatLng(
                                                            latitude,
                                                            longitude
                                                        )
                                                    )
                                                )
                                            markerMap!!.tag = LatLng(latitude, longitude).toString()
                                            list.add(LatLng(latitude, longitude))
                                            Log.e(
                                                MotionEffect.TAG,
                                                "onResponse: " + LatLng(
                                                    latitude,
                                                    longitude
                                                ).toString()
                                            )
                                        } catch (e: JSONException) {
                                            e.printStackTrace()
                                        }
                                    }
                                    moveCameraPlace(list)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        } else {
                            val errorMessage = response.message()
                            Log.e(MotionEffect.TAG, errorMessage)
                        }
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun moveCameraPlace(list: List<LatLng>) {
        handler.removeCallbacks(runnable!!)

        val user: MutableMap<String, Any> = java.util.HashMap()
        user["isSos"] = false
        updateRealtimeData(myCode, user) { _ ->
            sosStatus = ENABLE
            actionClickSos()
            if (binding.viewSOS.root.visibility === View.VISIBLE) {
                val animation =
                    AnimationUtils.loadAnimation(this, R.anim.slide_up)
                binding.viewSOS.root.startAnimation(animation)
                binding.viewSOS.root.visibility = View.GONE
            }
            try {
                val bounds = findLargestLatLngBounds(list)
                map!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
        if (sosList.isNotEmpty()) {
            binding.viewSos.root.visibility = View.INVISIBLE
            for (fri in sosList) {
                val user1: MutableMap<String, Any> = HashMap()
                user1["statusSos"] = false
                updateRealtimeData(
                    myCode + "/friends/" + fri.code,
                    user1
                ) { _ -> mapRipple!!.stopRippleMapAnimation() }
            }
        }
    }
    private fun actionOpenSos() {
        if (binding.viewSOS.root.visibility === View.GONE) {
            binding.viewSOS.root.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_down)
            binding.viewSOS.root.startAnimation(animation)
        } else if (binding.viewSOS.root.visibility === View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)
            binding.viewSOS.root.startAnimation(animation)
            binding.viewSOS.root.visibility = View.GONE
        }
    }
    private fun checkSos() {
        getOnetimeData(myCode) { snapshot ->
            isTracking = snapshot!!.child("isTracking").getValue(Boolean::class.java)!!
            isSos = snapshot.child("isSos").getValue(Boolean::class.java)!!
            if (!isTracking) {
                binding.viewSOS.root.visibility = View.GONE
                sosStatus = DISABLE
                binding.viewSOS.txtSosDesc.text = getString(R.string.disable_sos)
                binding.viewSOS.btnActive.text = getString(R.string.start)
                binding.viewSOS.btnActive.backgroundTintList =
                    ColorStateList.valueOf(getColor(R.color.red_E02E07))
            } else {
                if (isSos) {
                    sosStatus = ENABLE
                    binding.viewSOS.txtSosDesc.text = getString(R.string.enable_sos)
                    binding.viewSOS.btnActive.text = getString(R.string.turn_off)
                    binding.viewSOS.btnActive.backgroundTintList =
                        ColorStateList.valueOf(getColor(R.color.blue_4380F6))
                    binding.viewSOS.root.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun stopSOS() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.sound_alert)
        Log.w("Sos", sosList.size.toString())
        if (sosList.size == 0) {
            mediaPlayer!!.stop()
            binding.viewSos.root.visibility = View.GONE
            mapRipple!!.stopRippleMapAnimation()
        } else {
            mediaPlayer!!.start()

        }
    }
    private fun actionClickSos() {
        when (sosStatus) {
            DISABLE -> {
                getOnetimeData(myCode) { snapshot ->
                    val isTracking = snapshot!!.child("isTracking").getValue(
                        Boolean::class.java
                    )!!
                    if (!isTracking) {
                        Toast.makeText(this, getString(R.string.toast_sos), Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        sosStatus = ENABLE
                        binding.viewSOS.txtSosDesc.text = getString(R.string.enable_sos)
                        binding.viewSOS.btnActive.text = getString(R.string.turn_off)
                        binding.viewSOS.btnActive.backgroundTintList = ColorStateList.valueOf(
                            getColor(
                                R.color.blue_4380F6
                            )
                        )
                        val user: MutableMap<String, Any> =
                            java.util.HashMap()
                        user["isSos"] = true
                        updateRealtimeData(myCode, user) { _ -> }
                    }
                }
            }

            ENABLE -> {
                sosStatus = DISABLE
                binding.viewSOS.txtSosDesc.text = getString(R.string.disable_sos)
                binding.viewSOS.btnActive.text = getString(R.string.start)
                binding.viewSOS.btnActive.backgroundTintList =
                    ColorStateList.valueOf(getColor(R.color.red_E02E07))
                val user: MutableMap<String, Any> = java.util.HashMap()
                user["isSos"] = false
                updateRealtimeData(myCode, user) { _ -> }
            }
        }
    }
    private fun getPlace() {
        mapBottomBehavior = BottomSheetBehavior.from(binding.mapBottomSheet.root)
        detailBottomBehavior = BottomSheetBehavior.from(binding.detailBottom.root)
        detailBottomBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.detailBottom.root.visibility = View.VISIBLE
                    binding.mapBottomSheet.root.visibility = View.INVISIBLE
                    mapBottomBehavior!!.setState(BottomSheetBehavior.STATE_HIDDEN)
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.mapBottomSheet.root.visibility = View.VISIBLE
                    binding.detailBottom.root.visibility = View.INVISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
        mapBottomBehavior!!.addBottomSheetCallback(object : BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.mapBottomSheet.online.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutPlace.visibility = View.VISIBLE
                        binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.mapBottomSheet.online.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutFriend.visibility = View.VISIBLE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.mapBottomSheet.online.visibility = View.VISIBLE
                        binding.mapBottomSheet.layoutFriend.visibility = View.INVISIBLE
                        binding.mapBottomSheet.layoutPlace.visibility = View.INVISIBLE
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(binding.mapBottomSheet.edtSearch.windowToken, 0)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (slideOffset > 0.5) {
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED

                } else if (slideOffset > 0.2) {
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                } else {
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
                    mapBottomBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })
    }
    override fun onStop(friendModel: FriendModel) {
        try {
            userSos["statusSos"] = false
            updateRealtimeData(
                myCode + "/friends/" + friendModel.code,
                userSos
            ) { _ -> }
            sosList.remove(friendModel)
            stopSOS()
            mapRipple!!.stopRippleMapAnimation()
            Log.w("Sos", sosList.size.toString())
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    private fun getFriend() {
        viewModel.getAll.observe(this){
            for (i in it) {
                getOnetimeData(
                    myCode + "/friends/" + i.code
                ) { snapshot1 ->
                    try {
                        val visible =
                            snapshot1!!.child("visible").getValue(Boolean::class.java)!!
                        if (visible) {
                            getOnetimeData(i.code) { snapshot ->
                                val avtId =
                                    snapshot!!.child("avt").getValue(Int::class.java)!!
                                val battery =
                                    snapshot.child("battery").getValue(Int::class.java)!!
                                val name =
                                    snapshot.child("name").getValue(String::class.java)
                                val gender =
                                    snapshot.child("gender").getValue(String::class.java)
                                val phoneNumber =
                                    snapshot.child("phoneNumber").getValue(String::class.java)
                                val latLng =
                                    snapshot.child("latLng").getValue(String::class.java)
                                val lastActive =
                                    snapshot.child("lastActive").getValue(Long::class.java)
                                val isTracking =
                                    snapshot.child("isTracking").getValue(Boolean::class.java)!!
                                val isSos =
                                    snapshot.child("isSos").getValue(Boolean::class.java)!!


                                if (isTracking) {
                                    i.avt = avtId
                                    i.battery = battery
                                    i.name = name!!
                                    i.gender = gender!!
                                    i.phoneNumber = phoneNumber!!
                                    i.latLng = latLng!!
                                    i.lastActive = lastActive!!
                                    i.isSos = isSos

                                    viewModel.update(i)

                                    val friend: MutableMap<String, Any> =
                                        java.util.HashMap()
                                    friend["battery"] = battery
                                    friend["name"] = name
                                    friend["avt"] = avtId
                                    friend["phoneNumber"] = phoneNumber
                                    friend["gender"] = gender
                                    friend["isTracking"] = isTracking
                                    friend["isSos"] = isSos
                                    friend["lastActive"] = lastActive
                                    friend["latLng"] = latLng

                                    updateRealtimeData(
                                        myCode + "/friends/" + snapshot.key,
                                        friend
                                    ) { _ -> }
                                } else {
                                    val friend: MutableMap<String, Any> =
                                        java.util.HashMap()
                                    friend["isTracking"] = isTracking
                                    updateRealtimeData(
                                        myCode + "/friends/" + snapshot.key,
                                        friend
                                    ) { _ -> }
                                }
                            }
                        }
                    } catch (_: java.lang.Exception) {
                    }
                }
            }
        }
    }
    override fun onDetail(code: String, type: String) {
        when(type){
            "online"-> {
                getOnetimeData(code) { snapshot ->
                    val latLng =
                        snapshot!!.child("latLng").getValue(String::class.java)
                    map!!.clear()
                    actionShowFriendAll(false)
                    moveCamera(FRIEND_ONE, convertToLatLng(latLng!!))
                }
            }
            "main"->{
                val intent = Intent(this, DetailInformationActivity::class.java)
            intent.putExtra("receiveCode", code)
            Log.e("Kan", code)
            startActivity(intent)
            }
        }
    }
    override fun onFind(type: String) {
        actionShowNearbyPlace(type)
    }
    override fun onDelete(item: FriendModel) {
        viewModel.delete(item)
    }
}
