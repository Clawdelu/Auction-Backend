ALTER TABLE favourites
    ADD CONSTRAINT fk_favourites_user_id
        FOREIGN KEY ("user_id")
            REFERENCES users ("id");

ALTER TABLE "favourites"
    ADD CONSTRAINT fk_favourites_auction_id
        FOREIGN KEY ("auction_id")
            REFERENCES "auctions" ("id");

ALTER TABLE "bids"
    ADD CONSTRAINT fk_bids_auction_id
        FOREIGN KEY ("auction_id")
            REFERENCES "auctions" ("id");

ALTER TABLE "bids"
    ADD CONSTRAINT fk_bids_user_id
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id");

ALTER TABLE "company"
    ADD CONSTRAINT fk_users_company_id
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id");

ALTER TABLE "user_role"
    ADD CONSTRAINT fk_user_role_role_id
        FOREIGN KEY ("role_id")
            REFERENCES "roles" ("id");

ALTER TABLE "user_role"
    ADD CONSTRAINT fk_user_role_user_id
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id");

ALTER TABLE "auctions"
    ADD CONSTRAINT fk_users_auction_user_id
        FOREIGN KEY ("user_id")
            REFERENCES "users" ("id");