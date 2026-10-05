package com.seuprojeto.pocketcontestmacro.utils

import com.seuprojeto.pocketcontestmacro.model.MacroAction
import com.seuprojeto.pocketcontestmacro.model.MacroCondition
import java.util.UUID

object MacroClipboard {
    private var copiedAction: MacroAction? = null
    private var copiedCondition: MacroCondition? = null

    fun copyAction(action: MacroAction) {
        copiedAction = action.copy(id = UUID.randomUUID().toString())
    }

    fun getCopiedAction(): MacroAction? {
        return copiedAction?.copy(id = UUID.randomUUID().toString())
    }

    fun copyCondition(condition: MacroCondition) {
        copiedCondition = condition.copy(id = UUID.randomUUID().toString())
    }

    fun getCopiedCondition(): MacroCondition? {
        return copiedCondition?.copy(id = UUID.randomUUID().toString())
    }

    fun hasAction(): Boolean = copiedAction != null
    fun hasCondition(): Boolean = copiedCondition != null
}
