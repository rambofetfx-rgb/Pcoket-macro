package com.seuprojeto.pocketcontestmacro.storage

import android.content.Context
import com.seuprojeto.pocketcontestmacro.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MacroRepository(private val context: Context) {

    private val file = File(context.filesDir, "macros_saved.json")

    fun saveMacros(macros: List<MacroScript>) {
        val jsonArray = JSONArray()
        for (macro in macros) {
            val macroObj = JSONObject().apply {
                put("id", macro.id)
                put("name", macro.name)
                
                val actionsArr = JSONArray()
                macro.actions.forEach { act ->
                    actionsArr.put(JSONObject().apply {
                        put("id", act.id)
                        put("type", act.type.name)
                        put("targetValue", act.targetValue)
                        put("delayAfter", act.delayAfter)
                    })
                }
                put("actions", actionsArr)
            }
            jsonArray.put(macroObj)
        }
        file.writeText(jsonArray.toString())
    }

    fun loadMacros(): MutableList<MacroScript> {
        val list = mutableListOf<MacroScript>()
        if (!file.exists()) return list

        try {
            val jsonArray = JSONArray(file.readText())
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("id")
                val name = obj.getString("name")
                
                val actionsList = mutableListOf<MacroAction>()
                val actionsArr = obj.getJSONArray("actions")
                for (j in 0 until actionsArr.length()) {
                    val actObj = actionsArr.getJSONObject(j)
                    actionsList.add(
                        MacroAction(
                            id = actObj.getString("id"),
                            type = ActionType.valueOf(actObj.getString("type")),
                            targetValue = actObj.getString("targetValue"),
                            delayAfter = actObj.getLong("delayAfter")
                        )
                    )
                }
                list.add(MacroScript(id = id, name = name, actions = actionsList))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }
}
