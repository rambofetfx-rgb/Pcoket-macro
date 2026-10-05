package com.seuprojeto.pocketcontestmacro

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Criando layout simples programaticamente para evitar XML de activity extra
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 100, 50, 50)
        }

        val title = TextView(this).apply {
            text = "Pocket Contest Macro Pro"
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val btnAccessibility = Button(this).apply {
            text = "1. Ativar Serviço de Acessibilidade"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val btnBattery = Button(this).apply {
            text = "2. Ignorar Otimização de Bateria"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                    }
                }
            }
        }

        layout.addView(title)
        layout.addView(android.widget.Space(this).apply { setLayoutParams(android.widget.LinearLayout.LayoutParams(0, 40)) })
        layout.addView(btnAccessibility)
        layout.addView(android.widget.Space(this).apply { setLayoutParams(android.widget.LinearLayout.LayoutParams(0, 20)) })
        layout.addView(btnBattery)

        setContentView(layout)
    }
}
