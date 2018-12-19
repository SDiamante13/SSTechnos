package biz.sstechnos.employeedashboard.admin

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import biz.sstechnos.employeedashboard.R
import biz.sstechnos.employeedashboard.R.color.*
import biz.sstechnos.employeedashboard.entity.Employee
import kotlinx.android.synthetic.main.employee_list_item.*
import kotlinx.android.synthetic.main.employee_list_item.view.*


class EmployeeAdapter(val items : MutableList<String>, val context: Context) : RecyclerView.Adapter<EmployeeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeViewHolder {
        return EmployeeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.employee_list_item, parent, false))

    }

    override fun onBindViewHolder(holder: EmployeeViewHolder, position: Int) {
        holder?.employee.text = items[position]
        if(position % 2 == 0) {
            holder.employee_container.setBackgroundColor(ContextCompat.getColor(context, R.color.black ))
        } else {
            holder.employee_container.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary ))
        }
        holder?.itemView.setOnClickListener {
            Log.d("SSTechnos", "name of employee: ${items[position]}")
            var employeeId = items[position].split(" ")[0]
            context.startActivity(Intent(context, ViewEmployeeActivity::class.java).putExtra("EMPLOYEE_ID", employeeId).addFlags(FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}


class EmployeeViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each employee to
    val employee = view.employee_member
    val employee_container = view.employee_container
}