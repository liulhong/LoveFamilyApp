package org.ar.call.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import org.ar.call.R
import org.ar.call.bean.Person
import org.ar.call.ui.BaseActivity
import org.ar.call.ui.EditPersonActivity
import org.ar.call.ui.P2PCallActivity
import org.ar.call.ui.P2PVideoActivity
import org.ar.call.utils.showError
import org.ar.call.vm.GlobalVM


class PersonAdapter(private val personList: List<Person>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {
    companion object {
        // 获取viewModel需要用到这个，必须先在P2PCallActivity的onCreate方法中传过来
        var viewModel: GlobalVM? = null
    }
//    private lateinit var viewModel: GlobalVM

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val personImage: ImageButton = view.findViewById(R.id.personImage)
        val personName: TextView = view.findViewById(R.id.personName)
        val personEditIcon : ImageButton = view.findViewById(R.id.personEditIcon)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_item, parent, false)
        val viewHolder = ViewHolder(view)
        // 获取viewModel
//        viewModel = ViewModelProvider(viewModelStoreOwner!!).get(GlobalVM::class.java)
//        viewModel = viewModelStoreOwner?.callViewModel!!

        viewHolder.personImage.setOnClickListener {
            val position = viewHolder.adapterPosition
            val person = personList[position]
            Log.d("personImageClick", "onCreateViewHolder: " + person.callId.toString())
            if (person.callId.toString().isEmpty()) {
                TipDialog.show(view.context as AppCompatActivity,"请输入需要呼叫的ID", WaitDialog.TYPE.ERROR)
                return@setOnClickListener
            }
            if (person.callId.toString().length < 4) {
                TipDialog.show(view.context as AppCompatActivity,"请输入4位呼叫ID", WaitDialog.TYPE.ERROR)
                return@setOnClickListener
            }

            if (person.callId.toString().equals(viewModel?.userId)) {
                TipDialog.show(view.context as AppCompatActivity,"不能呼叫自己", WaitDialog.TYPE.ERROR)
                return@setOnClickListener
            }
            viewModel?.queryOnline(person.callId.toString()){
                if (it) {
                    viewModel!!.createLocalInvitation(
                        person.callId.toString(),
                        0
                    ) {
                        Log.d("printData", "adapter: person.callFree = ${person.callFree}")
                        view.context.startActivity(Intent().apply {
                            setClass(view.context, P2PVideoActivity::class.java)
                            putExtra("isCalled", false)//是否是收到呼叫 no
                            putExtra("isCallFree", person.callFree)
                        })
                    }
                }else{
                    TipDialog.show(view.context as AppCompatActivity,"对方不在线", WaitDialog.TYPE.ERROR)
                }
            }



        }
        viewHolder.personEditIcon.setOnClickListener {
            val position = viewHolder.adapterPosition
            val person = personList[position]
            val intent = Intent(view.context, EditPersonActivity::class.java)
            intent.putExtra("id", person.id)

            view.context.startActivity(intent)
        }
        return viewHolder
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = personList[position]
        holder.personName.text = person.name
        holder.personImage.setImageBitmap(person.image)
        holder.personEditIcon.setImageResource(person.editIcon)
    }
    override fun getItemCount() = personList.size
}