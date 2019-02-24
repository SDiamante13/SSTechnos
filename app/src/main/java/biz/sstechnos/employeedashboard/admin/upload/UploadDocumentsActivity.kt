package biz.sstechnos.employeedashboard.admin.upload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.webkit.MimeTypeMap
import android.widget.Toast
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.employee.ViewDocumentsActivity
import biz.sstechnos.employeedashboard.utils.CookieBarUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_upload_documents.*
import org.koin.android.ext.android.inject


class UploadDocumentsActivity : AppCompatActivity(), OnCompleteListener<UploadTask.TaskSnapshot> {

    private val storageReference : StorageReference by inject()
    private val databaseReference : DatabaseReference by inject()

    private var imageUri : Uri = Uri.EMPTY
    private val PICK_IMAGE_REQUEST = 1
    private var uploadTask: Task<UploadTask.TaskSnapshot>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_documents)

        supportActionBar?.title = "Document Upload"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        button_choose_image.setOnClickListener {
            openFileChooser()

        }

        text_show_uploads.setOnClickListener {
            startActivity(Intent(this@UploadDocumentsActivity, ViewDocumentsActivity::class.java))
        }

        button_upload.setOnClickListener {
            if(uploadTask != null && !uploadTask!!.isComplete)
                Toast.makeText(this@UploadDocumentsActivity, "Upload in progress", Toast.LENGTH_SHORT).show()
            else
                uploadFile()
        }
    }

    private fun uploadFile() {
        if (imageUri != Uri.EMPTY && edit_text_file.toString().trim() != "") {
            uploadTask = storageReference.child("documents")
                .child("" + System.currentTimeMillis() + "." + getFileExtension(imageUri))
                .putFile(imageUri)
                .addOnCompleteListener(this)
                .addOnProgressListener { taskSnapshot ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        upload_progress_bar.setProgress(
                            ((100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()),
                            true
                        )
                    }
                }
        } else {
            Toast.makeText(this@UploadDocumentsActivity, "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onComplete(uploadTask: Task<UploadTask.TaskSnapshot>) {
        if(uploadTask.isSuccessful) {
            Handler().postDelayed({ upload_progress_bar.progress = 0 }, 500)
            CookieBarUtil.makeCookie(this@UploadDocumentsActivity,
                "Success!",
                "Your document has been uploaded and can be viewed by your employees").show()
            val documentUpload = DocumentUpload(
                documentName = edit_text_file.text.toString().trim(),
                imageUrl = uploadTask.result.downloadUrl.toString())
            val documentId = databaseReference.push().key
            databaseReference.child("documents").child(documentId).setValue(documentUpload)
        } else {
            CookieBarUtil.makeCookie(this@UploadDocumentsActivity,
                "Failed!",
                "Something went wrong in the uploading of your document").show()
        }
    }

    private fun getFileExtension(uri: Uri) : String {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(contentResolver.getType(uri))!!
    }


    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            Picasso.with(this@UploadDocumentsActivity).load(imageUri).into(image_view_preview)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
