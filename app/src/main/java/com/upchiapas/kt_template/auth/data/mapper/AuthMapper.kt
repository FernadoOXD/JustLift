package com.upchiapas.kt_template.auth.data.mapper

import com.upchiapas.kt_template.auth.data.dto.UserDto
import com.upchiapas.kt_template.auth.domain.model.User

fun UserDto.toDomain(): User = User(
    id = this.id,
    name = this.name.orEmpty(),
    email = this.email
)