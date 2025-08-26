package com.mycompany.firstapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Parcelize
@Entity
data class TaskModel(
    @PrimaryKey(true)
    val id: Int = 0,
    val name: String = "",
    val dueDate: String = "",
    val dueTime: String = "",
    val isRepeatOn: Boolean = false,
    var isTaskDone: Boolean = false,
    val parentListName: String = ""
): Parcelable