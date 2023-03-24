package com.ahmetaksoy.translatorkeyboard


import  android.annotation.SuppressLint
import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.EditText

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

class InputMethod:InputMethodService(),KeyboardView.OnKeyboardActionListener {
    private var keyboardView: KeyboardView? = null
    private var keyboard: Keyboard? = null
    private var caps = false
    private lateinit var textView: EditText
    private var kelimeler : String =""
    lateinit var focusTop: Button

    lateinit var inputConnection: InputConnection
    lateinit var inputConnection2: InputConnection
    lateinit var seciliInputConnection: InputConnection

     lateinit var keyboardLayout:View


    val options = FirebaseTranslatorOptions.Builder().setSourceLanguage(FirebaseTranslateLanguage.TR)
        .setTargetLanguage(FirebaseTranslateLanguage.EN).build()
    val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

    fun translate(kelimeler: String){

        translator.translate(kelimeler)
            .addOnSuccessListener { translatedText ->
                // Çeviri başarılı oldu
                textView.setText(translatedText)
            }
            .addOnFailureListener { exception ->
                // Çeviri başarısız oldu
                println("Çeviri hatası: ${exception.message}")
            }

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)

        textView = keyboardLayout.findViewById(R.id.text_view)
        focusTop = keyboardLayout.findViewById(R.id.focusTop)


        inputConnection = currentInputConnection
        inputConnection2 = textView.onCreateInputConnection(EditorInfo())

        seciliInputConnection= inputConnection



        textView.setOnTouchListener { v, event ->


            textView.requestFocus()
            seciliInputConnection= inputConnection2

            false
        }

        focusTop.setOnTouchListener{v,event->

            seciliInputConnection= inputConnection

            false
        }



    }


    override fun onCreateInputView(): View {



        keyboardLayout = layoutInflater.inflate(R.layout.keyboard2, null)
        keyboardView = keyboardLayout.findViewById(R.id.keyboardd) as KeyboardView

        keyboard = Keyboard(this, R.xml.keys)
        keyboardView!!.keyboard = keyboard
        keyboardView!!.setOnKeyboardActionListener(this)
        keyboardView!!.setBackgroundColor(getResources().getColor(R.color.whiteSmoke))


        return keyboardLayout
    }

    override fun onPress(p0: Int) {
    }

    override fun onRelease(p0: Int) {
    }

    override fun onKey(p0: Int, p1: IntArray?) {


        if (seciliInputConnection != null) {
            when (p0) {
                Keyboard.KEYCODE_DELETE -> {
                    val selectedText = seciliInputConnection.getSelectedText(0)
                    if (TextUtils.isEmpty(selectedText)) {
                        seciliInputConnection.deleteSurroundingText(1, 0)
                    } else {
                        seciliInputConnection.commitText("", 1)
                    }
                    if (kelimeler!=""){
                        kelimeler = kelimeler.substring(0,kelimeler.length-1)
                    }

                    keyboardView!!.invalidateAllKeys()
                }
                Keyboard.KEYCODE_SHIFT -> {
                    caps = !caps
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }
                Keyboard.KEYCODE_DONE -> {
                    //eger klavyedeki editTexte tıklanmışsa oradaki texti enterla
                    if (seciliInputConnection==inputConnection2){
                        //once inputConnectiondaki text temizleniyor ve sonra klavyedeki textView.text'i inputConneciton'a gonderiyoruz
                        inputConnection.deleteSurroundingText(Int.MAX_VALUE, Int.MAX_VALUE)
                        inputConnection.commitText(textView.text.toString(), 1)
                        seciliInputConnection=inputConnection
                    }

                    seciliInputConnection.sendKeyEvent(
                        KeyEvent(
                            KeyEvent.ACTION_DOWN,
                            KeyEvent.KEYCODE_ENTER
                        )
                    )
                    //enter yapıp arayınca eski aramadan text kalmasın diye temizliyoruz.
                    kelimeler=""
                }
                else -> {
                    var code = p0.toChar()
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code)
                    }
                    seciliInputConnection.commitText(code.toString(), 1)

                    kelimeler+=code


                }
            }
            if(seciliInputConnection==inputConnection){

                val extractedText = inputConnection.getExtractedText(ExtractedTextRequest(), 0).text.toString()
                textView.text.clear()
                translate(extractedText)
            }
        }

    }

    override fun onText(p0: CharSequence?) {
    }

    override fun swipeLeft() {
    }

    override fun swipeRight() {
    }

    override fun swipeDown() {
    }

    override fun swipeUp() {
    }



}