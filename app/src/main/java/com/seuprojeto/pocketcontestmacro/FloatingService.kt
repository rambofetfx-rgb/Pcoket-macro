package com.seuprojeto.pocketcontestmacro

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: Button
    private lateinit var params: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Configuração dos parâmetros da janela flutuante
        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        // Criar o botão flutuante visualmente
        floatingButton = Button(this).apply {
            text = "🤖 Macro"
            setBackgroundColor(android.graphics.Color.parseColor("#E91E63"))
            setTextColor(android.graphics.Color.WHITE)
            setPadding(20, 10, 20, 10)
        }

        // Adicionar comportamento de arrastar (Drag & Drop) e Clique
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var isMoved = false

        floatingButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    isMoved = false
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = (event.rawX - initialTouchX).toInt()
                    val deltaY = (event.rawY - initialTouchY).toInt()

                    if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
                        isMoved = true
                    }

                    params.x = initialX + deltaX
                    params.y = initialY + deltaY
                    windowManager.updateViewLayout(floatingButton, params)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isMoved) {
                        // Ação ao clicar no botão flutuante
                        Toast.makeText(this@FloatingService, "Painel da Macro Aberto!", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        // Adicionar o botão ao ecrã do sistema
        windowManager.addView(floatingButton, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }
    }
}
