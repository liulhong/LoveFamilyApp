package org.ar.call.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.ar.call.R
import org.ar.call.databinding.ActivityEditPersonBinding
import org.ar.call.view.SeparatedEditText

class EditPersonActivity : BaseActivity() {
    private val binding by lazy { ActivityEditPersonBinding.inflate(layoutInflater) }
    private var isAddPerson = false
    private var isPersonImageEdit = false
    private var isPersonNameEdit = false
    private var isPersonCallIdEdit = false
    private var editFinishedCounter = MutableLiveData<Int>()
    private val fromAlbum = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isAddPerson = intent.getBooleanExtra("isAddPerson", false)
        initView()

    }

    private fun initView() {
        binding.run {
            if (isAddPerson) {
                Log.d("personEditTitle", "add person")
                personDetailTitle.text = "新建联系人"
                editFinishedCounter.value = 0
                btnEditTrue.setImageResource(R.drawable.edit_true_nofinish)
            } else {
                personDetailTitle.text = "编辑联系人"
                isPersonImageEdit = true
                isPersonNameEdit = true
                isPersonCallIdEdit = true
                editFinishedCounter.value = 3
                btnEditTrue.setImageResource(R.drawable.edit_true_finish)
            }
            editFinishedCounter.observe(this@EditPersonActivity, Observer {
                if (editFinishedCounter.value == 2) {
                    btnEditTrue.setImageResource(R.drawable.edit_true_finish)
                } else {
                    btnEditTrue.setImageResource(R.drawable.edit_true_nofinish)
                }
            })
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
                    var nameTextLength = personCallIdEdit.text.length
                    if (!isPersonCallIdEdit && nameTextLength != 0) {
                        isPersonCallIdEdit = true
                        val count = editFinishedCounter.value ?: 0
                        editFinishedCounter.value = count + 1
                    } else if (isPersonCallIdEdit && nameTextLength == 0) {
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
        }
    }

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

}