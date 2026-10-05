package com.seuprojeto.pocketcontestmacro

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Path
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Button
import android.widget.Toast
import kotlinx.coroutines.*

class AutomationAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var floatingView: View? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())

    companion object {
        var instance: AutomationAccessibilityService? = null
            private set
        var isServiceRunning = false
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isServiceRunning = true
        startForegroundServiceWithNotification()
        showFloatingOverlay()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Tenta manter o serviço ativo contra otimizações de bateria
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Monitoramento global de eventos de tela se necessário
    }

    override fun onInterrupt() {
        isServiceRunning = false
        removeFloatingOverlay()
        instance = null
    }

    private fun startForegroundServiceWithNotification() {
        val channelId = "pocket_macro_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pocket Macro Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Pocket Macro Protegido")
                .setContentText("Serviço rodando em primeiro plano para evitar encerramento.")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Pocket Macro Protegido")
                .setContentText("Serviço rodando em primeiro plano.")
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .build()
        }

        startForeground(999, notification)
    }

    private fun showFloatingOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        
        val button = Button(this).apply {
            text = "▶ Executar"
            setBackgroundColor(0xAA00AA00.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            setOnClickListener {
                Toast.makeText(this@AutomationAccessibilityService, "Macro acionado!", Toast.LENGTH_SHORT).show()
                // Exemplo de teste de clique nas coordenadas X: 500, Y: 1000
                clickAt(500f, 1000f)
            }
        }

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 150
        }

        floatingView = button
        try {
            windowManager.addView(floatingView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Clique por Coordenada X e Y
    fun clickAt(x: Float, y: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val path = Path().apply { moveTo(x, y) }
            val stroke = GestureDescription.StrokeDescription(path, 0, 50)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            dispatchGesture(gesture, null, null)
        }
    }

    // Reconhecimento e Clique por Texto na Tela (OCR via Accessibility Nodes)
    fun clickByText(textToFind: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        return findAndClickNodeRecursive(rootNode, textToFind)
    }

    private fun findAndClickNodeRecursive(node: AccessibilityNodeInfo, text: String): Boolean {
        if (node.text != null && node.text.toString().contains(text, ignoreCase = true)) {
            var target: AccessibilityNodeInfo? = node
            while (target != null && !target.isClickable) {
                target = target.parent
            }
            if (target != null) {
                return target.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findAndClickNodeRecursive(child, text)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        return false
    }

    private fun removeFloatingOverlay() {
        if (floatingView != null) {
            try {
                windowManager.removeView(floatingView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            floatingView = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        removeFloatingOverlay()
        serviceScope.cancel()
        instance = null
    }
}
