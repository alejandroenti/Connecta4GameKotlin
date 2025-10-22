package com.alejandrolopez.connecta4game.classes

import org.json.JSONObject


class GameObject(
    var id: String?,
    var center_x: Double,
    var center_y: Double,
    var radius: Double,
    var col: Int,
    var row: Int
) {
    var color: String? = null

    override fun toString(): String {
        return this.toJSON().toString()
    }

    // Converteix l'objecte a JSON
    fun toJSON(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("center_x", center_x)
        obj.put("center_y", center_y)
        obj.put("radius", radius)
        obj.put("col", col)
        obj.put("row", row)
        obj.put("color", if (color != null) color else "red")
        return obj
    }

    companion object {
        // Crea un GameObjects a partir de JSON
        fun fromJSON(obj: JSONObject): GameObject {
            val go = GameObject(
                obj.optString("id", null),
                obj.optDouble("center_x", 0.0),
                obj.optDouble("center_y", 0.0),
                obj.optDouble("radius", 0.0),
                obj.optInt("col", 1),
                obj.optInt("row", 1)
            )
            go.color = obj.optString("color", "red")
            return go
        }
    }
}