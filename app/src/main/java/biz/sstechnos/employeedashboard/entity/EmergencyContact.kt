package biz.sstechnos.employeedashboard.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class EmergencyContact(var firstName : String = "",
                            var lastName : String = "",
                            var relationship : Relationship = Relationship.MOTHER,
                            var phoneNumber : String = ""
                            )
