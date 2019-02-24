package biz.sstechnos.employeedashboard.registration

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import biz.sstechnos.employeedashboard.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_upload_id_card.*
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream


class UploadIdCardActivity : AppCompatActivity(), OnCompleteListener<UploadTask.TaskSnapshot> {
    private val storageReference : StorageReference by inject()

    private lateinit var downloadUrl : Uri

    private val REQUEST_IMAGE_CAPTURE_ONE = 1
    private val REQUEST_IMAGE_CAPTURE_TWO = 2
    private lateinit var employeeId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_id_card)
        supportActionBar?.title = "Upload Id Card"

        employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
            .getString("employeeId", " ")!!

        submit_button.setOnClickListener {
            startActivity(Intent(this, AccountCreationActivity::class.java))
            finish()
        }

        upload_button1.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    takePictureIntent -> takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_ONE) }
            }
        }

        upload_button2.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                    takePictureIntent -> takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_TWO) }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == REQUEST_IMAGE_CAPTURE_ONE && resultCode == Activity.RESULT_OK && data != null) {
            val data = convertImageToByteArray(data, document1)!!
            storageReference.child("images/$employeeId/govId.jpg").putBytes(data).addOnCompleteListener(this)
        } else if(requestCode == REQUEST_IMAGE_CAPTURE_TWO && resultCode == Activity.RESULT_OK && data != null) {
            val data = convertImageToByteArray(data, document2)!!
            storageReference.child("images/$employeeId/otherId.jpg").putBytes(data).addOnCompleteListener(this)
        }
    }

    private fun convertImageToByteArray(data: Intent, imageView: ImageView): ByteArray? {
        val imageBitmap = data.extras!!.get("data") as Bitmap
       imageView.setImageBitmap(imageBitmap)

        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    override fun onComplete(uploadTask : Task<UploadTask.TaskSnapshot>) {
        if(uploadTask.isSuccessful) {
            downloadUrl = uploadTask.result.downloadUrl!!
        } else {
            Log.d("SSTechnos", "Failure to upload image: " + uploadTask.exception)
        }
    }
}
