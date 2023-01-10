package org.ar.call.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.AbstractWindowedCursor
import android.database.CursorWindow
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.kongzue.dialogx.dialogs.MessageDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.ar.call.R
import org.ar.call.bean.Person
import org.ar.call.database.PersonDatabaseHelper
import org.ar.call.databinding.ActivityEditPersonBinding
import org.ar.call.utils.*
import org.ar.call.utils.PicFunc.blobToBitmap
import java.io.*
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
    private var needCrop = false
    private val TAKE_PHOTO_PERMISSION_REQUEST_CODE = 0 // 拍照的权限处理返回码
    private val WRITE_SDCARD_PERMISSION_REQUEST_CODE = 1 // 读储存卡内容的权限处理返回码
    private val TAKE_PHOTO_REQUEST_CODE = 3 // 拍照返回的 requestCode
    private val CHOICE_FROM_ALBUM_REQUEST_CODE = 4 // 相册选取返回的 requestCode
    private val CROP_PHOTO_REQUEST_CODE = 5 // 裁剪图片返回的 requestCode
    private val photoUri: Uri? = null
    private var photoOutputUri: Uri? = null // 图片最终的输出文件的 Uri

    private var person : Person = Person()
    private lateinit var dbHelper : PersonDatabaseHelper


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isAddPerson = intent.getBooleanExtra("isAddPerson", false)
        person.id = intent.getIntExtra("id", 0)
        initView()
        dbHelper = PersonDatabaseHelper(this, "personList.db", 2)
        /*
         * 先判断用户以前有没有对我们的应用程序允许过读写内存卡内容的权限，
         * 用户处理的结果在 onRequestPermissionResult 中进行处理
         */
        if(ContextCompat.checkSelfPermission(this@EditPersonActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // 申请读写内存卡内容的权限
            ActivityCompat.requestPermissions(this@EditPersonActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_SDCARD_PERMISSION_REQUEST_CODE);
        }
        Log.d("printData", "onCreateEditPersonActivity: " + person.name)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
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
                btDeletePerson.show()

                val db = PersonDatabaseHelper(this@EditPersonActivity, "personList.db", 2).writableDatabase
//                val selectQuery = "SELECT  * FROM Person WHERE id = ?"
//                db.rawQuery(selectQuery, arrayOf(person.id.toString())).use { // .use requires API 16
                // 查询Book表中所有的数据
                val cursor = db.query("Person",null, "id=?", arrayOf(person.id.toString()), null, null, null)
                val cw = CursorWindow("test1", 30000000)
                val ac = cursor as AbstractWindowedCursor
                ac.window = cw
                if (cursor.moveToFirst()) {
                    do {
                        person.name = cursor.getString(cursor.getColumnIndex("name"))
                        person.callId = cursor.getString(cursor.getColumnIndex("callId"))
                        Log.d("printData", "onSearchName: " + person.name)
                        person.image = blobToBitmap(cursor.getBlob(cursor.getColumnIndex("image")))
                        person.callFree = cursor.getInt(cursor.getColumnIndex("callFree")) > 0
                    } while (cursor.moveToNext())
                }
                cursor.close()

                Log.d("printData", "onSearchName: " + person.name)
                personImageEdit.setImageBitmap(person.image)
                personNameEdit.text = Editable.Factory.getInstance().newEditable(person.name)
                personCallIdEdit.text = Editable.Factory.getInstance().newEditable(person.callId)
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
                toast(this@EditPersonActivity, "编辑成功")
                finish()
            }
            // 定义图片选择及裁剪
            val cropPhoto = registerForActivityResult(CropPhotoContract()) { p: Pair<Uri?, String> ->
                if (p.first != null) {
                    personImageEdit.setImageURI(p.first)
                    var tempFile = File(Environment.getExternalStorageDirectory().toString()
                            + File.separator + Environment.DIRECTORY_DCIM+File.separator + p.second)
                    tempFile.delete()
                    Log.d("printData", "cropPhoto: $tempFile")
                }
            }
            val selectPhoto = registerForActivityResult(SelectPhotoContract()) { uri: Uri? ->
                if (uri != null) {
                    if (needCrop) {
                        Log.d("printData", "initView: $uri")
                        lifecycleScope.launch {
                            val googlePrefix =
                                "content://com.google.android.apps.photos.contentprovider"
                            val newUri = if (uri.toString().startsWith(googlePrefix, true)) {
                                // 处理谷歌相册返回的图片
                                uri
//                                saveImageToCache(this@EditPersonActivity, uri)
                            } else uri

                            // 剪裁图片
                            kotlin.runCatching {
                                cropPhoto.launch(CropParams(newUri))
                            }.onFailure {
                                Log.e("myError", "crop failed: $it" )
                            }
                        }
                    } else {
                        personImageEdit.setImageURI(uri)
                    }
                } else {
                    toast("您没有选择任何图片")
                }
            }

            personImageEdit.setOnClickListener {
                needCrop = true
                selectPhoto.launch(null)
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
                            person.callId = it.toString()
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
            }
            btDeletePerson.setOnClickListener {
                MessageDialog.show("提示", "确定删除此人吗？", "确定","取消")
                    .setOkButtonClickListener { _, _ ->
                        val db = dbHelper.writableDatabase
                        db.delete("Person", "id=?", arrayOf(person.id.toString()))
                        finish()
                        true
                    }.isCancelable = false
            }
        }
    }
    /**
     * 在这里进行用户权限授予结果处理
     * @param requestCode 权限要求码，即我们申请权限时传入的常量
     * @param permissions 保存权限名称的 String 数组，可以同时申请一个以上的权限
     * @param grantResults 每一个申请的权限的用户处理结果数组(是否授权)
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_SDCARD_PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "读写内存卡内容权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getNow(): String {
        return if (android.os.Build.VERSION.SDK_INT >= 24){
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Date())
        }else{
            val tms = Calendar.getInstance()
            tms.get(Calendar.YEAR).toString() + "-" +
                    tms.get(Calendar.MONTH).toString() + "-" +
                    tms.get(Calendar.DAY_OF_MONTH).toString() + " " +
                    tms.get(Calendar.HOUR_OF_DAY).toString() + ":" +
                    tms.get(Calendar.MINUTE).toString() +":" +
                    tms.get(Calendar.SECOND).toString() +"." +
                    tms.get(Calendar.MILLISECOND).toString()
        }
    }


}