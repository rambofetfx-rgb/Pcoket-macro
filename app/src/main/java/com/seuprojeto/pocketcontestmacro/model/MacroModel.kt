package com.seuprojeto.pocketcontestmacro.model

import java.util.UUID

data class MacroAction(
    val id: String = UUID.randomUUID().toString(),
    val type: ActionType, // CLICK_COORD, CLICK_TEXT, WAIT
    val targetValue: String, // Coordenada "x,y", texto a buscar, ou tempo em ms
    val delayAfter: Long = 1000L
)

data class MacroCondition(
    val id: String = UUID.randomUUID().toString(),
    val type: ConditionType, // TEXT_EXISTS, TIME_ELAPSED
    val value: String
)

data class MacroScript(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val conditions: MutableList<MacroCondition> = mutableListOf(),
    val actions: MutableList<MacroAction> = mutableListOf()
)

enum class ActionType {
    CLICK_COORD,
    CLICK_TEXT,
    WAIT
}

enum class ConditionType {
    TEXT_EXISTS,
    TIME_ELAPSED
}
