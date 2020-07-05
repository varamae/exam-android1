package com.example.myapplication

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapters.GoalAdapters
import com.example.myapplication.model.GoalModel
import com.github.nkzawa.socketio.androidchat.BackgroundService
import kotlinx.android.synthetic.main.fragment_goal.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.Socket
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket

class MainActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayList: ArrayList<GoalModel>? = null
    private var goalAdapters: GoalAdapters? = null

    var mSocket: Socket? = null
    var b: Button? = null
    var button_id = 0
    var oid = 0
    var prefs: SharedPreferences? = null
    private var selected = false
    var arr = BooleanArray(10)

    val trySocket = try {
        mSocket = IO.socket("https://px-socket-api.herokuapp.com/")
    } catch {
        throw RuntimeException()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_achievement, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //recyclerView = findViewById(R.id.recyclerView1)
        gridLayoutManager = GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        arrayList = ArrayList()
        arrayList = setDataInList()
        goalAdapters = GoalAdapters(applicationContext, arrayList!!)
        recyclerView?.adapter = goalAdapters

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        spinner1.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{_, _, _, _->}, year, month, day)
            datePickerDialog.show()
        }

        val ctx: Context =
            applicationContext.getApplicationContext()
        var prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        var configEditor = prefs.edit()
        configEditor.putBoolean(
            "activityStarted",
            true
        )
        configEditor.commit()
        val status = prefs.getBoolean("serviceStopped", false)
        Log.d("service exists ?", status.toString() + "")
        if (status) {
            Log.d("service", "not running")
            Log.d(
                "activity running",
                prefs.getBoolean("activityStarted", false).toString() + ""
            )
        } else {
            Log.d("service running", "true")
            Log.d(
                "activity running",
                prefs.getBoolean("activityStarted", false).toString() + ""
            )
            stopService(Intent(getBaseContext(), BackgroundService::class.java))
            mSocket?.isClosed()
        }
        setContentView(R.layout.activity_main)
        Log.d("activity", "connecting to server")
        mSocket?.isConnected()
        Log.d("activity", "connected")
        
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        newNotifiction()
        
    }

    val numberInput1: EditText = findViewById(R.id.editTextNumberDecimal1)
    val numberInput2: EditText = findViewById(R.id.editTextNumberDecimal2)

    private fun setDataInList() : ArrayList<GoalModel> {

        var items:ArrayList<GoalModel> = ArrayList()

        items.add(GoalModel(R.drawable.ic_baseline_work_outline_24, "Travel"))
        items.add(GoalModel(R.drawable.ic_baseline_menu_book_24, "Education"))
        items.add(GoalModel(R.drawable.ic_baseline_trending_up_24, "Invest"))
        items.add(GoalModel(R.drawable.ic_baseline_store_24, "Clothing"))
        items.add(GoalModel(R.drawable.ic_baseline_menu_book_24, "Education"))

        return items
    }

    override protected fun onStart() {
        super.onStart()
        Log.d("activity", "inside onstart")
        Log.d("onStart", "getOccupied")
    }
    
    override fun newNotification() {
        
        val num = BackgroundService.onNm()
        val newNotificationTextView: TextView = navView.getView().findViewById(R.id.textView40)
        newNotificationTextView = setText(num)
        
    }

    override fun onPause() {
        super.onPause()
        Log.d("activity", "inside onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("activity", "inside onstop")
    }

    companion object {
        var configEditor: SharedPreferences.Editor? = null
    }
}
