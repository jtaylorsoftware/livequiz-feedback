CREATE TABLE IF NOT EXISTS feedback(
    id                  SERIAL PRIMARY KEY,
    quiz_id             VARCHAR(36) NOT NULL,
    username            VARCHAR(24) NOT NULL,
    question_number     INTEGER NOT NULL,
    difficulty_rating   INTEGER NOT NULL,
    message             VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS response(
    id                  SERIAL PRIMARY KEY,
    quiz_id             VARCHAR(36) NOT NULL,
    username            VARCHAR(24) NOT NULL,
    question_number     INTEGER NOT NULL,
    value               VARCHAR(64) NOT NULL,
    score               INTEGER NOT NULL
);