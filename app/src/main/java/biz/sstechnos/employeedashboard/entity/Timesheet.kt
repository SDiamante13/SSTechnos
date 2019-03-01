package biz.sstechnos.employeedashboard.entity

data class Timesheet (var totalHrs: Double = 0.0,
                      var weeks: List<Week> = emptyList(),
                      var approved: Boolean = false)