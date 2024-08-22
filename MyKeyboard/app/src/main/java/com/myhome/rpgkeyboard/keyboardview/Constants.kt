package com.myhome.rpgkeyboard.keyboardview

const val SERVER_IP: String = "192.168.65.10" // 서버 IP 주소
const val SERVER_PORT: Int = 12345 // 서버 포트 번호
const val LOCAL_PORT: Int = 12346 // 로컬 포트 번호
var EMOTION: String = "None" // 감정
var Is_Immoral: Float = 0.8f // 비속어 비율
var llm_answer: String = "너 정말 실망이야"
var Abuse: Float = 0.0f // 비속어 비율

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

