package com.mycompany.firstapp.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class TaskListModel(
    @PrimaryKey(true)
    var id: Int = 0,
    var parentListName: String = "",
    @Ignore
    var count: Int? = 0
)
