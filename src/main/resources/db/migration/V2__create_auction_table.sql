CREATE TABLE "auctions" (
                            "id" int PRIMARY KEY NOT NULL,
                            "title" varchar NOT NULL,
                            "starting_price" double precision NOT NULL,
                            "threshold_price" double precision NOT NULL,
                            "start_time" timestamp NOT NULL,
                            "end_time" timestamp NOT NULL,
                            "description_details" text NOT NULL,
                            "default_shipping" boolean,
                            "first_name" varchar NOT NULL,
                            "last_name" varchar NOT NULL,
                            "email" varchar NOT NULL,
                            "currency" varchar NOT NULL,
                            "status" varchar NOT NULL,
                            "reject_reason" varchar,
                            "user_id" int NOT NULL,
                            "auction_identifier" varchar NOT NULL UNIQUE
);