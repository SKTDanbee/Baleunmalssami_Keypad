package com.myhome.rpgkeyboard.keyboardview

import android.util.Log

const val SERVER_IP: String = "10.0.2.2" // 서버 IP 주소
const val SERVER_PORT: Int = 12345 // 서버 포트 번호
const val LOCAL_PORT: Int = 12346 // 로컬 포트 번호
var EMOTION: String = "None" // 감정
var Is_Immoral: Float = 0.0f // 비속어 비율
var llm_answer: String = ""
var Abuse: Float = 0.0f // 비속어 비율
var currentMode = 1 // 현재 모드

fun getEmotion(): String {
    return EMOTION
}
fun setEmotion(emotion: String) {
    EMOTION = emotion
}


var IsTextEmpty: Boolean = false // 텍스트가 비어있는지 여부
fun setTextEmpty(isEmpty: Boolean) {
    IsTextEmpty = isEmpty
}
fun getTextEmpty(): Boolean{
    return IsTextEmpty
}
fun getIsImmoralEmoji(): String {
    Log.d("ABUSE_score", Abuse.toString())
    if (Is_Immoral > 0.75) {
        return "🔴"
    }
    else if(0.75 >= Is_Immoral  && Is_Immoral> 0.2 ){
        return "🟠"
    }
    else{
        return "🟢"
    }
}
fun setIsImmoral(isImmoral: Float) {
    Is_Immoral = isImmoral
}
fun getIsImmoral(): Float {
    return Is_Immoral
}

