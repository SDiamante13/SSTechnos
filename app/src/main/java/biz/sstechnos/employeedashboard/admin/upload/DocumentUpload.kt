package biz.sstechnos.employeedashboard.admin.upload

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class DocumentUpload(val documentName : String = "N/A",
                          val imageUrl : String = "")