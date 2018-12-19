package biz.sstechnos.employeedashboard.utils

import android.app.Activity
import biz.sstechnos.employeedashboard.R
import org.aviran.cookiebar2.CookieBar

class CookieBarUtil {
    companion object {
        fun makeCookie(activity : Activity, title: String, message : String): CookieBar.Builder {  return CookieBar.build(activity)
            .setTitle(title)
            .setTitleColor(R.color.colorPrimary)
            .setBackgroundColor(R.color.black)
            .setMessage(message)
            .setDuration(7000)
            .setCookiePosition(CookieBar.BOTTOM) }
    }
}