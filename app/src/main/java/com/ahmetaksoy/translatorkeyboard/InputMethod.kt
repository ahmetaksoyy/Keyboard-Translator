package com.ahmetaksoy.translatorkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.ImageView
import android.widget.TextView

class InputMethod:InputMethodService(),KeyboardView.OnKeyboardActionListener {
    private var keyboardView: KeyboardView? = null
    private var keyboard: Keyboard? = null
    private var caps = false
    private lateinit var textView: TextView
    private var kelime : String =""
     lateinit var enterImage: ImageView
    lateinit var inputConnection: InputConnection

    //klavyenin ust tarafındaki textviewin enterini kullanabilmek için.
    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        enterImage.setOnClickListener{
            inputConnection.sendKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_ENTER
                )
            )
        }
    }

    // @SuppressLint("ResourceAsColor")
    override fun onCreateInputView(): View {

//        keyboardView = layoutInflater.inflate(R.layout.keyboard, null) as KeyboardView
//        keyboard = Keyboard(this, R.xml.keys)

       val keyboardLayout = layoutInflater.inflate(R.layout.keyboard2, null)
         keyboardView = keyboardLayout.findViewById(R.id.keyboardd) as KeyboardView
       textView = keyboardLayout.findViewById(R.id.text_view)
        enterImage = keyboardLayout.findViewById(R.id.imageView)
       //keyboard= Keyboard(this,R.xml.keys)

//        keyboardView!!.apply {
//            keyboard =this@InputMethod.keyboard
//
//            setOnKeyboardActionListener(this@InputMethod)
//        }
//        keyboardView!!.keyboard = keyboard
//        keyboardView!!.setOnKeyboardActionListener(this)
//
//        keyboardView!!.setBackgroundColor(getResources().getColor(R.color.whiteSmoke))
//        return keyboardView!!

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
         inputConnection = currentInputConnection
        if (inputConnection != null) {
            when (p0) {
                Keyboard.KEYCODE_DELETE -> {
                    val selectedText = inputConnection.getSelectedText(0)
                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0)
                    } else {
                        inputConnection.commitText("", 1)
                    }
                    if (kelime!=""){
                        kelime = kelime.substring(0,kelime.length-1)
                    }
//                    caps = !caps
//                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }
                Keyboard.KEYCODE_SHIFT -> {
                    caps = !caps
                    keyboard!!.isShifted = caps
                    keyboardView!!.invalidateAllKeys()
                }
                Keyboard.KEYCODE_DONE -> inputConnection.sendKeyEvent(
                    KeyEvent(
                        KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_ENTER
                    )
                )


                else -> {
                    var code = p0.toChar()
                    if (Character.isLetter(code) && caps) {
                        code = Character.toUpperCase(code)
                    }
                    inputConnection.commitText(code.toString(), 1)
                    kelime+=code
                   // textView.text=kelime

                }
            }
        }
        textView.text=kelime

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