package com.seuprojeto.pocketcontestmacro.utils

import com.seuprojeto.pocketcontestmacro.model.MacroAction
import com.seuprojeto.pocketcontestmacro.model.MacroCondition

object MacroClipboard {
    private var copiedAction: MacroAction? = null
    private var copiedCondition: MacroCondition? = null

    // Copia uma ação clonando seus dados (gerando um novo ID para evitar conflitos)
    fun copyAction(action: MacroAction) {
        copiedAction = action.copy(id = java.util.UUID.randomUUID().toString())
    }

    fun getCopiedAction(): MacroAction? {
        return copiedAction?.copy(id = java.util.UUID.randomUUID().toString())
    }

    // Copia uma condição
    fun copyCondition(condition: MacroCondition) {
        copiedCondition = condition.copy(id = java.util.UUID.randomUUID().toString())
    }

    fun getCopiedCondition(): MacroCondition? {
        return copiedCondition?.copy(id = java.util.UUID.randomUUID().toString())
    }

    fun hasAction(): Boolean = copiedAction != null
    fun hasCondition(): Boolean = copiedCondition != null
}
