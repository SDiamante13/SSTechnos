package biz.sstechnos.employeedashboard.admin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import biz.sstechnos.employeedashboard.R
import kotlinx.android.synthetic.main.activity_upload_documents.*


class UploadDocumentsActivity : AppCompatActivity() {

    private lateinit var employeeId : String

    var documentNameList : MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_documents)

        supportActionBar?.title = "Document Listings"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        employeeId = getSharedPreferences("Employee", MODE_PRIVATE)
            .getString("employeeId", " ")

        // Creates a vertical Layout Manager
        document_list_view.layoutManager = LinearLayoutManager(baseContext)

        // Access the RecyclerView Adapter and load the data into it
        document_list_view.adapter = DocumentAdapter(documentNameList)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
