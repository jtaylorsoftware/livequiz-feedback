package com.jtaylorsoftware.livequiz.api.feedback.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * An individual scored response to a quiz question.
 * @property quizId ID of the quiz this response is for.
 * @property username Name of the user that submitted the response.
 * @property questionNumber Positive integer index of the question in the quiz.
 * @property value User's input for the response, converted to String. It may have
 * originally been numeric if the question was multiple-choice.
 * @property score The score received for this individual response.
 */
@Table("response")
data class QuizResponse(
    val quizId: String,
    val username: String,
    val questionNumber: Int,
    val value: String,
    val score: Int,
    @Id var id: Int? = null
)

/**
 * A `QuizResponse` projection including only a user's username
 * and their score for an entire quiz.
 */
data class UserWithScore(
    val username: String,
    val totalScore: Int
)