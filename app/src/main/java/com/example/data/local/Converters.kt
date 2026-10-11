package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ContactDeliveryReport
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromContactReportsList(reports: List<ContactDeliveryReport>?): String {
        if (reports == null) return "[]"
        val array = JSONArray()
        for (report in reports) {
            val obj = JSONObject().apply {
                put("contactId", report.contactId)
                put("contactName", report.contactName)
                put("phoneNumber", report.phoneNumber)
                put("smsState", report.smsState.name)
                put("callState", report.callState.name)
                put("errorMessage", report.errorMessage ?: "")
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toContactReportsList(jsonStr: String?): List<ContactDeliveryReport> {
        if (jsonStr.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<ContactDeliveryReport>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ContactDeliveryReport(
                        contactId = obj.optString("contactId"),
                        contactName = obj.optString("contactName"),
                        phoneNumber = obj.optString("phoneNumber"),
                        smsState = try {
                            com.example.data.model.DeliveryState.valueOf(obj.optString("smsState"))
                        } catch (e: Exception) {
                            com.example.data.model.DeliveryState.PENDING
                        },
                        callState = try {
                            com.example.data.model.CallState.valueOf(obj.optString("callState"))
                        } catch (e: Exception) {
                            com.example.data.model.CallState.IDLE
                        },
                        errorMessage = obj.optString("errorMessage").takeIf { it.isNotEmpty() }
                    )
                )
            }
        } catch (e: Exception) {
            // return empty
        }
        return list
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return list?.joinToString(";;") ?: ""
    }

    @TypeConverter
    fun toStringList(data: String?): List<String> {
        return data?.split(";;")?.filter { it.isNotEmpty() } ?: emptyList()
    }
}
