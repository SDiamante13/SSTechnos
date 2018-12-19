package biz.sstechnos.employeedashboard.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ContactInfo(var email : String = "",
                       var address : AddressInfo = AddressInfo(),
                       var phoneNumber : String = "",
                       var emergencyContact : EmergencyContact = EmergencyContact())