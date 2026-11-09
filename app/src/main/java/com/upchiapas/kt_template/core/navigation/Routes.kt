package com.upchiapas.kt_template.core.navigation

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"

    const val RUTINA_ID = "rutinaId"
    const val NO_ID = -1

    const val DETAIL = "detail/{$RUTINA_ID}"
    const val FORM = "form?$RUTINA_ID={$RUTINA_ID}"

    fun detail(id: Int) = "detail/$id"
    fun form(id: Int? = null) = if (id == null) "form" else "form?$RUTINA_ID=$id"
}