package com.seuprojeto.pocketcontestmacro

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var mainLayout: LinearLayout
    private lateinit var params: WindowManager.LayoutParams
    
    private val recordedActions = mutableListOf<MacroAction>()
    private var isRecording = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

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
            x = 50
            y = 150
        }

        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#CC000000"))
            setPadding(20, 20, 20, 20)
        }

        val btnToggleRecord = Button(this).apply {
            text = "🔴 Gravar Cliques"
            setOnClickListener {
                isRecording = !isRecording
                if (isRecording) {
                    recordedActions.clear()
                    text = "⏹ Parar Gravação"
                    Toast.makeText(context, "Modo de Gravação Ativo", Toast.LENGTH_SHORT).show()
                } else {
                    text = "🔴 Gravar Cliques"
                    Toast.makeText(context, "Guardado: ${recordedActions.size} cliques", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnPlay = Button(this).apply {
            text = "▶ Iniciar Macro"
            setOnClickListener {
                if (recordedActions.isNotEmpty()) {
                    Toast.makeText(context, "A executar macro...", Toast.LENGTH_SHORT).show()
                    AutomationAccessibilityService.instance?.playMacro(recordedActions) {
                        Toast.makeText(context, "Macro concluída!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Nenhuma macro gravada!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val btnStop = Button(this).apply {
            text = "⏸ Parar / Pausar"
            setOnClickListener {
                AutomationAccessibilityService.instance?.stopMacro()
                Toast.makeText(context, "Macro parada.", Toast.LENGTH_SHORT).show()
            }
        }

        val btnSave = Button(this).apply {
            text = "💾 Salvar Macro"
            setOnClickListener {
                saveMacroToFile("default_macro.dat")
            }
        }

        val btnLoad = Button(this).apply {
            text = "📂 Carregar Macro"
            setOnClickListener {
                loadMacroFromFile("default_macro.dat")
            }
        }

        mainLayout.addView(btnToggleRecord)
        mainLayout.addView(btnPlay)
        mainLayout.addView(btnStop)
        mainLayout.addView(btnSave)
        mainLayout.addView(btnLoad)

        windowManager.addView(mainLayout, params)
    }

    private fun saveMacroToFile(filename: String) {
        try {
            val file = File(filesDir, filename)
            ObjectOutputStream(file.outputStream()).use { it.writeObject(recordedActions) }
            Toast.makeText(this, "Macro guardada com sucesso!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao guardar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMacroFromFile(filename: String) {
        try {
            val file = File(filesDir, filename)
            if (file.exists()) {
                ObjectInputStream(file.inputStream()).use {
                    val loaded = it.readObject() as? List<MacroAction>
                    if (loaded != null) {
                        recordedActions.clear()
                        recordedActions.addAll(loaded)
                        Toast.makeText(this, "Macro carregada (${recordedActions.size} ações)!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Nenhum ficheiro salvo encontrado.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao carregar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mainLayout.isInitialized) {
            windowManager.removeView(mainLayout)
        }
    }
}
