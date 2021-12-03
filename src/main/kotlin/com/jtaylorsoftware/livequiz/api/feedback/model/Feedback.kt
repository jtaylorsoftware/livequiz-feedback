package com.jtaylorsoftware.livequiz.api.feedback.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * An individual submitted feedback for a quiz question.
 * @property quizId The ID of the quiz that this feedback is for.
 * @property username The name of user that submitted the feedback.
 * @property questionNumber The positive integer index of the question in the quiz.
 * @property difficultyRating A `DifficultyRating` value indicating how difficult user thought question was.
 * @property message An optional text message describing the reason for the feedback.
 */
@Table("feedback")
data class Feedback(
    val quizId: String,
    val username: String,
    val questionNumber: Int,
    @Column("difficulty_rating")
    val difficultyRating: Int,
    val message: String?,
    @Id var id: Int? = null
)

/**
 * A value representing how difficult a quiz question is for a user.
 */
enum class DifficultyRating(val value: Int) {
    EASY(0), DIFFICULT(1), CHALLENGING(2), IMPOSSIBLE(3);

    companion object {
        fun fromValue(value: Int) = when (value) {
            0 -> EASY
            1 -> DIFFICULT
            2 -> CHALLENGING
            3 -> IMPOSSIBLE
            else -> throw IllegalArgumentException()
        }
    }
}
