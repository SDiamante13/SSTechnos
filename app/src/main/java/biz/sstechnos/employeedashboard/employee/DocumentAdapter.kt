package biz.sstechnos.employeedashboard.employee

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import biz.sstechnos.employeedashboard.R
import kotlinx.android.synthetic.main.document_list_item.view.*

class DocumentAdapter(val items : MutableList<String>) : RecyclerView.Adapter<DocumentViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        context = parent.context
        return DocumentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.document_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.documentName.text = items[position]
        if(position % 2 == 0) {
            holder.document_container.setBackgroundColor(ContextCompat.getColor(context, R.color.black ))
        } else {
            holder.document_container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary ))
        }
        holder.itemView.setOnClickListener {
            Log.d("SSTechnos", "name of document: ${items[position]}")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}


class DocumentViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each employee to
    val documentName = view.document_member!!
    val document_container = view.document_container!!
}