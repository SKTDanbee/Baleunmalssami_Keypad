package com.myhome.rpgkeyboard.keyboardview

const val SERVER_IP: String = "192.168.0.78" // 서버 IP 주소
const val SERVER_PORT: Int = 12345 // 서버 포트 번호
const val LOCAL_PORT: Int = 12346 // 로컬 포트 번호
var EMOTION: String = "" // 감정

fun getEmotion(): String {
    return EMOTION
}
fun setEmotion(emotion: String) {
    EMOTION = emotion
}
