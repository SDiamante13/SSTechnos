package biz.sstechnos.employeedashboard.employee

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.admin.upload.DocumentUpload
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.document_item.view.*

class DocumentAdapter(val documentUploads : MutableList<DocumentUpload>) : RecyclerView.Adapter<DocumentViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        context = parent.context
        return DocumentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.document_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val currentDocument = documentUploads[position]
        holder.documentName.text = currentDocument.documentName

        Picasso.with(context)
            .load(currentDocument.imageUrl)
            .fit()
            .centerCrop()
            .into(holder.documentImage)


        holder.itemView.setOnClickListener {
            Log.d("SSTechnos", "name of document: ${documentUploads[position]}")
        }
    }

    override fun getItemCount(): Int {
        return documentUploads.size
    }
}


class DocumentViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each employee to
    val documentName = view.document_name
    val documentImage = view.image_view_document


}