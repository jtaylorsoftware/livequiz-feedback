package com.jtaylorsoftware.livequiz.api.feedback.service.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList

suspend fun <T> ServiceResult<Flow<T>>.collectToList(): List<T> {
    val list = mutableListOf<T>()
    this.result.getOrNull()!!.toList(list)
    return list
}