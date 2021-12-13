package com.jtaylorsoftware.livequiz.api.feedback.mapping

import com.jtaylorsoftware.livequiz.api.feedback.model.QuizResponse

data class QuizResponseDto(
    val quizId: String,
    val username: String,
    val questionNumber: Int,
    val value: String,
    val score: Int
)

fun QuizResponseDto.toModel(): QuizResponse =
    QuizResponse(
        quizId = quizId,
        username = username,
        questionNumber = questionNumber,
        value = value,
        score = score
    )