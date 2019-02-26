package biz.sstechnos.employeedashboard.admin

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import biz.sstechnos.employeedashboard.R
import kotlinx.android.synthetic.main.employee_list_item.view.*


class EmployeeAdapter(val items : MutableList<String>) : RecyclerView.Adapter<EmployeeViewHolder>() {

    private lateinit var context : Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        context = parent.context
        return EmployeeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.employee_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder.employee.text = items[position]
        if(position % 2 == 0) {
            holder.employee_container.setBackgroundColor(ContextCompat.getColor(context, R.color.black ))
        } else {
            holder.employee_container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary ))
        }
        holder.itemView.setOnClickListener {
            Log.d("SSTechnos", "name of employee: ${items[position]}")
            var employeeId = items[position].split(" ")[0]
            var accountStatus = items[position].split(" ")[3]
            context.startActivity(Intent(context, ViewEmployeeActivity::class.java)
                .putExtra("EMPLOYEE_ID", employeeId)
                .putExtra("ACCOUNT_STATUS", accountStatus)
                .addFlags(FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}


class EmployeeViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each employee to
    val employee = view.employee_member!!
    val employee_container = view.employee_container!!
}