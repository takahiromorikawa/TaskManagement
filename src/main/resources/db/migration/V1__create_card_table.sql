CREATE TABLE card (
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(255) NOT NULL,
    status    VARCHAR(20)  NOT NULL,
    priority  VARCHAR(10)  NOT NULL,
    due_date  DATE
);
