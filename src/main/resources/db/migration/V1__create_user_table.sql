CREATE TABLE "users" (
                         "id" int PRIMARY KEY,
                         "first_name" varchar NOT NULL,
                         "last_name" varchar NOT NULL,
                         "email" varchar NOT NULL UNIQUE ,
                         "password" varchar NOT NULL,
                         "profile_picture" varchar NOT NULL
);