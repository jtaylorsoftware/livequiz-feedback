CREATE TABLE IF NOT EXISTS feedback(
    id                  SERIAL PRIMARY KEY,
    quiz_id             VARCHAR(32) NOT NULL,
    username            VARCHAR(24) NOT NULL,
    question_number     INTEGER NOT NULL,
    difficulty_rating   INTEGER NOT NULL,
    message             VARCHAR(255)
);
