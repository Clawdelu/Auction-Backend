ALTER TABLE auctions ADD COLUMN tsv tsvector
    GENERATED ALWAYS AS (to_tsvector('english', title)) STORED;

CREATE INDEX tsv_index ON auctions USING GIN (tsv);