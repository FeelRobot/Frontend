package com.project.feelrobot.model

data class Manager(
    override val id: String,
    override val name: String,
    override val email: String,
    val students: List<Student>? // 보호자가 관리하는 학생 리스트
) : User(id, name, email)
