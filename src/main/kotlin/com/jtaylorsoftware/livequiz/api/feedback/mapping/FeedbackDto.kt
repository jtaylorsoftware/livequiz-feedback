package com.jtaylorsoftware.livequiz.api.feedback.mapping

import com.jtaylorsoftware.livequiz.api.feedback.model.DifficultyRating
import com.jtaylorsoftware.livequiz.api.feedback.model.Feedback

data class FeedbackDto(
    val quizId: String,
    val username: String,
    val questionNumber: Int,
    val difficultyRating: DifficultyRating,
    val message: String?,
)

fun FeedbackDto.toModel() = Feedback(
    quizId = quizId,
    username = username,
    questionNumber = questionNumber,
    difficultyRating = difficultyRating.value,
    message = message
)