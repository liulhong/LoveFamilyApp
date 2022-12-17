package org.ar.call.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.ar.call.R
import org.ar.call.bean.Person
import org.ar.call.ui.EditPersonActivity


class PersonAdapter(val personList: List<Person>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val personImage: ImageButton = view.findViewById(R.id.personImage)
        val personName: TextView = view.findViewById(R.id.personName)
        val personEditImage : ImageButton = view.findViewById(R.id.personEditImage)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.personEditImage.setOnClickListener {
            val intent = Intent(view.context, EditPersonActivity::class.java)
            view.context.startActivity(intent)
        }
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = personList[position]
        holder.personName.text = person.name
        holder.personImage.setImageResource(person.imageId)
        holder.personEditImage.setImageResource(person.editImageId)
    }
    override fun getItemCount() = personList.size
}