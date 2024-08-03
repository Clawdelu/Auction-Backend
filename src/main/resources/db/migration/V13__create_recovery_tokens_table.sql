CREATE TABLE "recovery_tokens" (
                         "id" int PRIMARY KEY,
                         "email" varchar NOT NULL,
                         "token" varchar NOT NULL,
                         "expiration" varchar NOT NULL

);