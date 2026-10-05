package com.seuprojeto.pocketcontestmacro

import java.io.Serializable

data class MacroAction(
    val x: Float,
    val y: Float,
    val delayAfter: Long // tempo em milissegundos após o clique
) : Serializable
