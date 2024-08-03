CREATE TABLE "bids" (
                        "id" int PRIMARY KEY NOT NULL,
                        "auction_id" int NOT NULL,
                        "user_id" int NOT NULL,
                        "amount_bid" double precision NOT NULL,
                        "time_stamp" timestamp NOT NULL
);