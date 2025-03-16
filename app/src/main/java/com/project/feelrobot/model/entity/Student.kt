package com.project.feelrobot.model.entity

data class Student(
    override val id: String,
    override val name: String,
    override val email: String,
    val manager: Manager? // 학생이 속한 매니저 정보
) : User(id, name, email)
