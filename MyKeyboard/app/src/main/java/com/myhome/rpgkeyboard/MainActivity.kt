package com.myhome.rpgkeyboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.myhome.rpgkeyboard.database.DatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingHeader = findViewById<ConstraintLayout>(R.id.setting_header)
        val submitButton = settingHeader.findViewById<TextView>(R.id.submit_text)
        submitButton.visibility = View.GONE

        // DatabaseHelper 초기화
        dbHelper = DatabaseHelper(this)

        // CSV 파일에서 데이터 로드
        dbHelper.loadCSVData(this)
    }
}
