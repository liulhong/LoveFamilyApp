package org.ar.call.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.database.AbstractWindowedCursor
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.ar.call.R
import org.ar.call.bean.Person
import org.ar.call.database.PersonDatabaseHelper
import org.ar.call.databinding.ActivityEditPersonBinding
import org.ar.call.utils.PicFunc.blobToBitmap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class EditPersonActivity : BaseActivity() {
    private val binding by lazy { ActivityEditPersonBinding.inflate(layoutInflater) }
    private var isAddPerson = false
    private var isPersonImageEdit = false
    private var isPersonNameEdit = false
    private var isPersonCallIdEdit = false
    private var editFinishedCounter = MutableLiveData<Int>()
    private val fromAlbum = 2
    private var person : Person = Person()
    private lateinit var dbHelper : PersonDatabaseHelper


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isAddPerson = intent.getBooleanExtra("isAddPerson", false)
        person.id = intent.getIntExtra("id", 0)
        initView()
        dbHelper = PersonDatabaseHelper(this, "personList.db", 2)
        Log.d("printData", "onCreateEditPersonActivity: " + person.name)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initView() {
        binding.run {
            if (isAddPerson) {
                Log.d("personEditTitle", "add person")
                personDetailTitle.text = "新建联系人"
                editFinishedCounter.value = 0
                btnEditTrue.setImageResource(R.drawable.edit_true_nofinish)
                btnEditTrue.isEnabled = false
                personCallFreeEdit.isChecked = false
            } else {
                personDetailTitle.text = "编辑联系人"
                isPersonImageEdit = true
                isPersonNameEdit = true
                isPersonCallIdEdit = true
                editFinishedCounter.value = 2
                btnEditTrue.setImageResource(R.drawable.edit_true_finish)
                btnEditTrue.isEnabled = true

                val db = PersonDatabaseHelper(this@EditPersonActivity, "personList.db", 2).writableDatabase
//                val selectQuery = "SELECT  * FROM Person WHERE name = ?"
//                db.rawQuery(selectQuery, arrayOf(person.name.toString())).use { // .use requires API 16
//                    if (it.moveToFirst()) {
//
//                        person.callId = it.getInt(it.getColumnIndex("callId"))
//                        Log.d("printData", "onSearchName: " + person.name)
//                        person.image = blobToBitmap(it.getBlob(it.getColumnIndex("image")))
//                        person.callFree = it.getInt(it.getColumnIndex("callFree")) > 0
//                    }
//                }
                // 查询Book表中所有的数据
                val cursor = db.query("Person",null, "id=?", arrayOf(person.id.toString()), null, null, null)
                val cw = CursorWindow("test1", 50000000)
                val ac = cursor as AbstractWindowedCursor
                ac.window = cw
                if (cursor.moveToFirst()) {
                    do {
                        person.name = cursor.getString(cursor.getColumnIndex("name"))
                        person.callId = cursor.getInt(cursor.getColumnIndex("callId"))
                        Log.d("printData", "onSearchName: " + person.name)
                        person.image = blobToBitmap(cursor.getBlob(cursor.getColumnIndex("image")))
                        person.callFree = cursor.getInt(cursor.getColumnIndex("callFree")) > 0
                    } while (cursor.moveToNext())
                }
                cursor.close()
                Log.d("printData", "onSearchName: " + person.name)
                personImageEdit.setImageBitmap(person.image)
                personNameEdit.text = Editable.Factory.getInstance().newEditable(person.name)
                personCallIdEdit.text = Editable.Factory.getInstance().newEditable(person.callId.toString())
                personCallFreeEdit.isChecked = person.callFree
            }

            editFinishedCounter.observe(this@EditPersonActivity, Observer {
                if (editFinishedCounter.value == 2) {
                    btnEditTrue.setImageResource(R.drawable.edit_true_finish)
                    btnEditTrue.isEnabled = true
                } else {
                    btnEditTrue.setImageResource(R.drawable.edit_true_nofinish)
                    btnEditTrue.isEnabled = false
                }
            })
            btnEditFalse.setOnClickListener {
                finish()
            }
            btnEditTrue.setOnClickListener{
                val db = dbHelper.writableDatabase
                var picBitmap : Bitmap = (binding.personImageEdit.drawable as BitmapDrawable).bitmap
                val os = ByteArrayOutputStream()
                picBitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                val values = ContentValues().apply {
                    put("callId", person.callId)
                    put("name", person.name)
                    put("image", os.toByteArray())
                    put("callFree", person.callFree)
                }
                if (isAddPerson) {
                    values.put("insertTime", getNow())
                    db.insert("Person", null, values)
                } else {
                    db.update("Person", values, "id = ?", arrayOf(person.id.toString()))
                }


                finish()
            }
            personImageEdit.setOnClickListener {
                // 打开文件选择器
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                // 指定只显示图片
                intent.type = "image/*"
                startActivityForResult(intent, fromAlbum)
            }
            personNameEdit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    personNameEdit.text?.let {
//                        Log.v("printData", it.toString())
                        person?.name = personNameEdit.text.toString()
                    }
                    var nameTextLength = personNameEdit.text.length
                    if (!isPersonNameEdit && nameTextLength != 0) {
                        isPersonNameEdit = true
                        val count = editFinishedCounter.value ?: 0
                        editFinishedCounter.value = count + 1
                    } else if (isPersonNameEdit && nameTextLength == 0) {
                        isPersonNameEdit = false
                        val count = editFinishedCounter.value ?: 0
                        editFinishedCounter.value = count - 1
                    }
                    Log.d("personEditStatus", "afterTextChanged: ")
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    Log.d("personNameEditStatus", "beforeTextChanged: ")
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    Log.d("personNameEditStatus", "onTextChanged: ")
                }
            })
            personCallIdEdit.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    personCallIdEdit.text?.let {

                        if (it.toString() != "") {
                            Log.v("printData", it.toString())
                            person.callId = Integer.parseInt(it.toString())
                        }
                    }
                    var callIdLength = personCallIdEdit.text.length
                    if (!isPersonCallIdEdit && callIdLength != 0) {
                        isPersonCallIdEdit = true
                        val count = editFinishedCounter.value ?: 0
                        editFinishedCounter.value = count + 1
                    } else if (isPersonCallIdEdit && callIdLength == 0) {
                        isPersonCallIdEdit = false
                        val count = editFinishedCounter.value ?: 0
                        editFinishedCounter.value = count - 1
                    }
                    Log.d("personEditStatus", "afterTextChanged: ")
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                    Log.d("personNameEditStatus", "beforeTextChanged: ")
                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    Log.d("personNameEditStatus", "onTextChanged: ")
                }
            })
            personCallFreeEdit.setOnCheckedChangeListener { _, isChecked ->
                    person?.callFree = isChecked
//                    Log.v("printData",isChecked.toString() )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
                fromAlbum -> {
            if (resultCode == Activity.RESULT_OK && data != null) {
                data.data?.let { uri ->
                    // 将选择的图片显示
                    val bitmap = getBitmapFromUri(uri)
                    var personImage : ImageButton = findViewById(R.id.personImageEdit)
                    personImage.setImageBitmap(bitmap)

                }
            }
        }
        }
    }
    private fun getBitmapFromUri(uri: Uri) = contentResolver
    .openFileDescriptor(uri, "r")?.use {
        BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
    }

    fun getNow(): String {
        if (android.os.Build.VERSION.SDK_INT >= 24){
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        }else{
            var tms = Calendar.getInstance()
            return tms.get(Calendar.YEAR).toString() + "-" + tms.get(Calendar.MONTH).toString() + "-" + tms.get(Calendar.DAY_OF_MONTH).toString() + " " + tms.get(Calendar.HOUR_OF_DAY).toString() + ":" + tms.get(Calendar.MINUTE).toString() +":" + tms.get(Calendar.SECOND).toString() +"." + tms.get(Calendar.MILLISECOND).toString()
        }

    }


}