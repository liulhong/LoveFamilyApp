package org.ar.call.ui

import android.content.Intent
import android.database.AbstractWindowedCursor
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.ar.call.R
import org.ar.call.bean.Person
import org.ar.call.database.PersonDatabaseHelper
import org.ar.call.ui.adapter.PersonAdapter
import org.ar.call.utils.Constans
import org.ar.call.utils.PicFunc
import org.ar.call.utils.PicFunc.blobToBitmap


class P2PCallActivity : BaseActivity() {
    private val personList = ArrayList<Person>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_p2_pcall)
        // 将this传递给Adapter，用于获取viewModel，必须写在adapter创建之前
//        PersonAdapter.viewModelStoreOwner = this
        PersonAdapter.viewModel = this.callViewModel
        initView()

    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        initPersons()
        val mLayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = mLayoutManager
        val adapter = PersonAdapter(personList)
        recyclerView.adapter = adapter
        Log.d("MyLifeCycle", "onResume: P2PCallActivity")
    }
    @RequiresApi(Build.VERSION_CODES.P)
    private fun initPersons() {
        personList.clear()
        repeat(1) {
            val db = PersonDatabaseHelper(this, "personList.db", 2).writableDatabase
            // 查询Book表中所有的数据
            val cursor = db.query("Person", null, null, null, null, null, "insertTime asc")
            val cw = CursorWindow("test", 50000000)
            val ac = cursor as AbstractWindowedCursor
            ac.window = cw
            if (cursor.moveToFirst()) {
                do {
                    // 遍历Cursor对象，取出数据并打印
                    var person : Person = Person()
                    person.id = cursor.getInt(cursor.getColumnIndex("id"))
                    person.callId = cursor.getInt(cursor.getColumnIndex("callId"))
                    person.name = cursor.getString(cursor.getColumnIndex("name"))
                    person.image = blobToBitmap(cursor.getBlob(cursor.getColumnIndex("image")))
                    person.callFree = cursor.getInt(cursor.getColumnIndex("callFree")) > 0
                    person.editIcon = R.drawable.person_edit
                    personList.add(person)
                } while (cursor.moveToNext())
            }
            cursor.close()
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