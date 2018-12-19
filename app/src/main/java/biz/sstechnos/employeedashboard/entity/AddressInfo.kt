package biz.sstechnos.employeedashboard.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class AddressInfo(var country : String = "",
                       var city : String = "",
                       var state : String = "",
                       var streetAddress : String = "",
                       var zipCode : String = "")