package com.seuprojeto.pocketcontestmacro

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 80, 50, 50)
        }

        val title = TextView(this).apply {
            text = "Pocket Contest Macro Pro"
            textSize = 22f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val subtitle = TextView(this).apply {
            text = "Configure as permissões abaixo para iniciar:"
            textSize = 14f
            setPadding(0, 10, 0, 30)
        }

        val btnAccessibility = Button(this).apply {
            text = "1. Ativar Serviço de Acessibilidade"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        val btnOverlay = Button(this).apply {
            text = "2. Permitir Janela Flutuante (Overlay)"
            setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
                    startActivity(intent)
                }
            }
        }

        val btnBattery = Button(this).apply {
            text = "3. Ignorar Otimização de Bateria"
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

        val btnStartMacro = Button(this).apply {
            text = "▶ Iniciar Macro / Painel"
            setBackgroundColor(android.graphics.Color.parseColor("#4CAF50"))
            setTextColor(android.graphics.Color.WHITE)
            setOnClickListener {
                // Aqui pode iniciar o serviço principal da macro ou mostrar que está pronto
                Toast.makeText(this@MainActivity, "Macro pronta para uso!", Toast.LENGTH_SHORT).show()
            }
        }

        layout.addView(title)
        layout.addView(subtitle)
        layout.addView(btnAccessibility)
        layout.addView(Space(this).apply { layoutParams = LinearLayout.LayoutParams(0, 20) })
        layout.addView(btnOverlay)
        layout.addView(Space(this).apply { layoutParams = LinearLayout.LayoutParams(0, 20) })
        layout.addView(btnBattery)
        layout.addView(Space(this).apply { layoutParams = LinearLayout.LayoutParams(0, 40) })
        layout.addView(btnStartMacro)

        setContentView(layout)
    }
}
