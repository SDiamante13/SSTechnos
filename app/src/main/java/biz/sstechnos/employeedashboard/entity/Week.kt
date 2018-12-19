package biz.sstechnos.employeedashboard.entity

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Week (var totalHrs : Double = 0.0,
                 var mondayHrs : Double = 0.0,
                 var tuesdayHrs : Double = 0.0,
                 var wednesdayHrs : Double = 0.0,
                 var thursdayHrs : Double = 0.0,
                 var fridayHrs : Double = 0.0,
                 var saturdayHrs : Double = 0.0,
                 var sundayHrs : Double = 0.0)