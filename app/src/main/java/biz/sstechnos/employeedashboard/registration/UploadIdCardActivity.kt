package biz.sstechnos.employeedashboard.registration

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import biz.sstechnos.employeedashboard.R
import kotlinx.android.synthetic.main.activity_upload_id_card.*


class UploadIdCardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_id_card)
        supportActionBar?.title = "Upload Id Card"
    }

    override fun onResume() {
        super.onResume()
        submit_button.setOnClickListener {
            startActivity(Intent(this, AccountCreationActivity::class.java))
            finish()
        }
    }
}
