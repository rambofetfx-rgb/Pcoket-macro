package com.seuprojeto.pocketcontestmacro

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityNodeInfo

class AutomationAccessibilityService : AccessibilityService() {

    private var isRunning = false
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        var instance: AutomationAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // Monitoriza eventos do sistema se necessário
    }

    override fun onInterrupt() {
        isRunning = false
    }

    fun playMacro(actions: List<MacroAction>, onFinished: () -> Unit) {
        if (actions.isEmpty() || isRunning) return
        isRunning = true

        var index = 0
        fun executeNext() {
            if (!isRunning || index >= actions.size) {
                isRunning = false
                onFinished()
                return
            }

            val action = actions[index]
            clickAt(action.x, action.y)

            handler.postDelayed({
                index++
                executeNext()
            }, action.delayAfter)
        }

        executeNext()
    }

    fun stopMacro() {
        isRunning = false
    }

    private fun clickAt(x: Float, y: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val path = Path().apply {
                moveTo(x, y)
            }
            val stroke = GestureDescription.StrokeDescription(path, 0, 50)
            val gestureBuilder = GestureDescription.Builder().addStroke(stroke)
            dispatchGesture(gestureBuilder.build(), null, null)
        }
    }

    // Função de OCR nativo por Acessibilidade para detetar texto no ecrã
    fun findTextOnScreen(targetText: String): Boolean {
        val rootNode: AccessibilityNodeInfo = rootInActiveWindow ?: return false
        return searchNodeForText(rootNode, targetText)
    }

    private fun searchNodeForText(node: AccessibilityNodeInfo, target: String): Boolean {
        if (node.text != null && node.text.toString().contains(target, ignoreCase = true)) {
            return true
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (searchNodeForText(child, target)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        return false
    }
}
