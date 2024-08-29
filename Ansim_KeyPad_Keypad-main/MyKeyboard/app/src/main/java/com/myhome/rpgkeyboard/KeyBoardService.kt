package com.myhome.rpgkeyboard

import android.inputmethodservice.InputMethodService
import android.os.AsyncTask.execute
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.myhome.rpgkeyboard.keyboardview.*
import kotlinx.android.synthetic.main.emoji_left.view.emoji_text
import kotlinx.android.synthetic.main.llm_explain.view.key_button
import org.json.JSONObject


class KeyBoardService : InputMethodService(){
    lateinit var keyboardView:LinearLayout
    lateinit var keyboardFrame:FrameLayout
    lateinit var keyboardKorean:KeyboardKorean
    lateinit var keyboardEnglish:KeyboardEnglish
    lateinit var keyboardSimbols:KeyboardSimbols
    var isQwerty = 0 // shared preference에 데이터를 저장하고 불러오는 기능 필요

    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 300L // 5 seconds
    private var connection : InputConnection? = null

    val keyboardInterationListener = object:KeyboardInterationListener{


        //inputconnection이 null일경우 재요청하는 부분 필요함
        override fun modechange(mode: Int) {
            currentInputConnection.finishComposingText()
            Log.d("currentInputConnection", currentInputConnection.toString())
            if (currentInputConnection == null) {
                // inputConnection이 null일 경우 한글 키보드로 재설정
                modechange(1)  // 한글 키보드 모드로 강제 변경
                return
            }

            when(mode){
                0 ->{
                    currentMode = 0
                    keyboardFrame.removeAllViews()
                    keyboardEnglish.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardEnglish.getLayout())
                }
                1 -> {
                    currentMode = 1
                    if(isQwerty == 0){
                        keyboardFrame.removeAllViews()
                        keyboardKorean.inputConnection = currentInputConnection
                        keyboardFrame.addView(keyboardKorean.getLayout())
                    }
                    else{
                        keyboardFrame.removeAllViews()
                        keyboardFrame.addView(KeyboardChunjiin.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                    }
                }
                2 -> {
                    currentMode = 2
                    keyboardFrame.removeAllViews()
                    keyboardSimbols.inputConnection = currentInputConnection
                    keyboardFrame.addView(keyboardSimbols.getLayout())
                }
                3 -> {
                    currentMode = 3
                    keyboardFrame.removeAllViews()
                    keyboardFrame.addView(KeyboardEmoji.newInstance(applicationContext, layoutInflater, currentInputConnection, this))
                }

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        keyboardView = layoutInflater.inflate(R.layout.keyboard_view, null) as LinearLayout
        keyboardFrame = keyboardView.findViewById(R.id.keyboard_frame)

        handler.post(updateEmojiRunnable)

        val llmExplainView = keyboardView.findViewById<View>(R.id.key_button)
        val keyButton = llmExplainView.findViewById<Button>(R.id.key_button)
        val LLMTextView = keyboardView.findViewById<TextView>(R.id.key_button)
        val originalText = keyButton.text

        keyButton.setOnClickListener {
            if (getIsImmoral() >= 0.8f) {
                if (keyButton.text != originalText) {
                    keyButton.text = originalText
                } else {
                    Log.d("KeyBoardService", "Send LLM request")
                    keyButton.text = "Loading..." // 응답을 기다리는 동안 표시할 텍스트
                    val jsonMessage = JSONObject().apply {
                        put("type", "llm")
                    }

                    // 비동기 소켓 요청
                    val reMessage: JSONObject? = SocketClient(SERVER_IP, SERVER_PORT, jsonMessage).execute().get()
                    LLMTextView.text = reMessage?.getString("llm_answer") ?: "답변 생성 실패"
                }
            } else {
                Log.d("KeyBoardService", "Button press not allowed, getIsImmoral() < 0.8")
            }
        }
    }

    private val updateEmojiRunnable = object : Runnable {
        override fun run() {

            if (currentInputConnection != connection){
                connection = currentInputConnection
                updateInputConnection()
            }

            try {
                Log.d("inputConnection", currentInputConnection.toString())

                val emojiView = keyboardView.findViewById<TextView>(R.id.emoji_text)
                emojiView.text = getIsImmoralEmoji()
                Log.d("EmojiView", emojiView.text.toString())
//                val llm_text = keyboardView.findViewById<TextView>(R.id.key_button)
//                llm_text.text = llm_answer

                val LLMTextView = keyboardView.findViewById<TextView>(R.id.key_button)
                if (getIsImmoral() < 0.8 && LLMTextView.text != "+") {
                    LLMTextView.text = "+"
                }


            } catch (e: Exception) {
                Log.d("KeyBoardService", "Error: $e")
            }
            handler.postDelayed(this, updateInterval)
        }
    }


    override fun onCreateInputView(): View {
//

        keyboardKorean = KeyboardKorean(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardEnglish = KeyboardEnglish(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardSimbols = KeyboardSimbols(applicationContext, layoutInflater, keyboardInterationListener)
        keyboardKorean.inputConnection = currentInputConnection
        keyboardKorean.init()
        keyboardEnglish.inputConnection = currentInputConnection
        keyboardEnglish.init()
        keyboardSimbols.inputConnection = currentInputConnection
        keyboardSimbols.init()

        return keyboardView

    }

    override fun updateInputViewShown() {
        super.updateInputViewShown()
        currentInputConnection.finishComposingText()
        if(currentInputEditorInfo.inputType == EditorInfo.TYPE_CLASS_NUMBER){
            keyboardFrame.removeAllViews()
            keyboardFrame.addView(KeyboardNumpad.newInstance(applicationContext, layoutInflater, currentInputConnection, keyboardInterationListener))
        }
        else{
            keyboardInterationListener.modechange(1)
        }
    }


    fun updateInputConnection() {
        // keboard 재실행
        keyboardFrame.removeAllViews()
        keyboardInterationListener.modechange(currentMode)
    }

}
