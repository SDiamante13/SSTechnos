package biz.sstechnos.employeedashboard.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Employee(val employeeId : String = "",
                    val firstName : String = "",
                    val lastName : String = "",
                    val dateOfBirth : String = "",
                    val role : Role = Role.EMPLOYEE,
                    var jobTitle : String = "",
                    var salary : String = "",
                    var username : String? = "")