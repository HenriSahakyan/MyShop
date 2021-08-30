package com.example.myshop.utils

import androidx.appcompat.widget.AppCompatRadioButton
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet


class MSRadioButton(context: Context,attributeSet: AttributeSet): AppCompatRadioButton(context,attributeSet) {

    init{
        applyFont()
    }
    private fun applyFont(){
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets,"Montserrat-Bold.ttf")
        setTypeface(typeface)
    }


}