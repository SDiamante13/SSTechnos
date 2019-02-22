package biz.sstechnos.employeedashboard.employee

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.widget.Toast
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.upload.DocumentUpload
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_upload_documents.*
import kotlinx.android.synthetic.main.activity_view_documents.*
import org.koin.android.ext.android.inject


class ViewDocumentsActivity : AppCompatActivity(), ValueEventListener {

    private val databaseReference : DatabaseReference by inject()

    private lateinit var recyclerView: RecyclerView

    private lateinit var documentAdapter: DocumentAdapter

    private lateinit var employeeId : String

    private var documentList : MutableList<DocumentUpload> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_documents)

        supportActionBar?.title = "Document Listings"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = recycler_view_documents
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(baseContext)

        databaseReference.child("documents").addValueEventListener(this)
    }

    override fun onDataChange(documents: DataSnapshot) {

        for (singleSnapshot in documents.children) {
            var documentUploadSnapshot = singleSnapshot.getValue(DocumentUpload::class.java)
            if (documentUploadSnapshot != null) documentList.add(documentUploadSnapshot)
        }

        documentAdapter = DocumentAdapter(documentList)
        recyclerView.adapter = documentAdapter

    }

    override fun onCancelled(databaseError: DatabaseError?) {
        Toast.makeText(this@ViewDocumentsActivity, databaseError?.message, Toast.LENGTH_SHORT).show()
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }
}
