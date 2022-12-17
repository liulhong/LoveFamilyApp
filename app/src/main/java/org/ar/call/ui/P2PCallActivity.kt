package org.ar.call.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import org.ar.call.R
import org.ar.call.bean.Person
import org.ar.call.database.PersonDatabaseHelper
import org.ar.call.ui.adapter.PersonAdapter
import org.ar.call.utils.Constans
import org.ar.call.utils.showError
import org.ar.call.view.SeparatedEditText


class P2PCallActivity : AppCompatActivity() {
    private val personList = ArrayList<Person>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p2_pcall)
        initPersons()
        initView()
        val layoutManager = LinearLayoutManager(this)
        val mLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = mLayoutManager
        val adapter = PersonAdapter(personList)
        recyclerView.adapter = adapter

        val dbHelper = PersonDatabaseHelper(this, "personList.db", 1)
        dbHelper.writableDatabase

    }

    private fun initPersons() {
        repeat(2) {
            personList.add(Person("Apple", R.drawable.img_person, R.drawable.person_edit))
            personList.add(Person("Banana", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Orange", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Watermelon", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Pear", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Grape", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Pineapple", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Strawberry", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Cherry", R.drawable.apple, R.drawable.person_edit))
            personList.add(Person("Mango", R.drawable.apple, R.drawable.person_edit))
        }
    }

    fun initView(){
        var btnSetting : ImageButton = findViewById(R.id.btn_setting)
        var ivBack : ImageButton = findViewById(R.id.iv_back)
        btnSetting.setOnClickListener { Intent().apply {
            setClass(this@P2PCallActivity, SettingActivity::class.java)
            putExtra(Constans.KEY_SINGLE_CALL,true)
            startActivity(this)
        } }

        ivBack.setOnClickListener { finish() }
    }

}