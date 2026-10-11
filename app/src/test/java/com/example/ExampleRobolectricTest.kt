package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.hardware.SafetyBandManager
import com.example.data.telephony.EmergencyDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AASRA", appName)
    }

    @Test
    fun `verify emergency message format contains required parameters`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dispatcher = EmergencyDispatcher(context)
        val msg = dispatcher.formatEmergencyMessage(
            userName = "Nashra Naaz",
            mapsUrl = "https://maps.google.com/?q=28.6139,77.2090"
        )

        assertTrue("Should contain EMERGENCY ALERT header", msg.contains("EMERGENCY ALERT:"))
        assertTrue("Should contain user's name", msg.contains("Nashra Naaz"))
        assertTrue("Should contain maps location link", msg.contains("https://maps.google.com/?q=28.6139,77.2090"))
        assertTrue("Should contain call to action", msg.contains("Please contact me or provide assistance."))
    }

    @Test
    fun `verify safety band BLE service UUIDs and firmware definitions`() {
        assertEquals("0000A500-0000-1000-8000-00805F9B34FB", SafetyBandManager.BLE_SERVICE_UUID)
        assertTrue(SafetyBandManager.ARDUINO_FIRMWARE_CODE.contains("AASRA SMART SAFETY BAND"))
    }

    @Test
    fun `verify Indian languages catalog support`() {
        val languages = com.example.data.model.IndianLanguage.values()
        assertTrue("Should have multiple Indian regional languages", languages.size >= 10)
        val hindi = languages.first { it == com.example.data.model.IndianLanguage.HINDI }
        assertEquals("हिन्दी", hindi.nativeName)
        assertEquals("आपकी सुरक्षा, हमारी प्राथमिकता", hindi.tagline)
    }
}
