package com.github.nkzawa.socketio.androidchat

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.net.sip.SipSession
import android.os.IBinder
import android.preference.PreferenceManager
import android.system.Os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import org.json.JSONException
import org.json.JSONObject
import java.net.Socket
import java.net.SocketAddress
import java.net.URISyntaxException
import java.util.*
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket


class BackgroundService : Service() {
    var mSocket: Socket? = null
    var that = this
    var prefs: SharedPreferences? = null
    var ctx: Context? = null
    override fun onCreate() {
        ctx = applicationContext
        var prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        val status = prefs.getBoolean("activityStarted", true)
        Log.d("inside onCreate", status.toString() + "")
        if (!status) {
            run {
                try {
                    Log.d("inside if", "oncreate")
                    var configEditor = prefs!!.edit()
                    configEditor.putBoolean("serviceStopped", false)
                    configEditor.commit()
                    Log.d("connecting to server", "inside oncreate")
                    mSocket = IO.Socket("https://px-socket-api.herokuapp.com/")
                    mSocket?.isConnected()
                } catch (e: URISyntaxException) {
                    throw RuntimeException(e)
                }
            }
        } else {
            Log.d("inside else", "oncreate")
            onDestroy()
        }
    }

    fun createNotification(title: String?, text: String?) {
        val mBuilder = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.stat_notify_sync)
            .setContentTitle(title)
            .setContentText(text)
        // Creates an explicit intent for an Activity in your app
        val resultIntent = Intent(this, MainActivity::class.java)

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        val stackBuilder = TaskStackBuilder.create(this)
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity::class.java)
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
            0,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        mBuilder.setContentIntent(resultPendingIntent)
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build())
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        var ctx = applicationContext
        var prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
        val status = prefs.getBoolean("activityStarted", true)
        Log.d("inside onStartCommand", status.toString() + "")
        if (!status) {
            run {
                Log.d("inside if", "onstartCommand")
                Log.d("service started", status.toString() + "")
            }
        } else {
            Log.d("inside else", "onstartcommand")
            onDestroy()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val onNm: SipSession.Listener = object : SipSession.Listener() {
        fun call(vararg args: Any) {
            val data = args[0] as JSONObject
            Log.d("Inside onOccupied", "got num")
            val num: Int
            num = try {
                data.getInt("num")
            } catch (e: JSONException) {
                return
            }
            val occupied =
                prefs!!.getStringSet("occupied", HashSet())
            occupied!!.add(num.toString() + "")
            var configEditor = prefs!!.edit()
            configEditor.putStringSet("occupied", occupied)
            configEditor.commit()
            Log.d("value of num", num.toString() + "")
            that.createNotification("Button Pressed", "$num Pressed")
        }
    }
    private val onAv: SipSession.Listener = object : SipSession.Listener() {
        fun call(vararg args: Any) {
            val data = args[0] as JSONObject
            Log.d("onAv", "recieved something")
            val num: Int
            try {
                num = data.getInt("av")
                Log.d("onAvailable", num.toString() + "")
            } catch (e: JSONException) {
                return
            }
            val occupied =
                prefs!!.getStringSet("occupied", HashSet())
            if (occupied!!.contains(num.toString() + "")) {
                Log.d("inside if", num.toString() + "")
                occupied.remove(num.toString() + "")
            }
            var configEditor = prefs!!.edit()
            configEditor.putStringSet("occupied", occupied)
            configEditor.commit()
            Log.d(
                "inside onAv",
                prefs!!.getStringSet("occupied", HashSet()).toString()
            )
            that.createNotification("Button Available", "$num available")
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        Log.d("inside service destroy", "destroy service")
        Log.d("service", "disconnecting")
        Log.d("service", "disconnected")
        mSocket?.isClosed()
        mSocket?.isClosed()
        var configEditor = prefs!!.edit()
        configEditor.putBoolean("serviceStopped", true)
        configEditor.commit()
    }

    companion object {
        var configEditor: Editor? = null
    }
}