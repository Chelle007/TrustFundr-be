-- Links fundraising_activities to fundraising_categories by UUID (PostgreSQL).
-- Run after deploying code that expects column fundraising_category_id.
-- If column fundraising_category_id already exists, skip this script.

ALTER TABLE fundraising_activities
    ADD COLUMN IF NOT EXISTS fundraising_category_id UUID REFERENCES fundraising_categories (id);

-- Backfill from legacy free-text category column when present
UPDATE fundraising_activities fa
SET fundraising_category_id = sub.id
FROM (
    SELECT fc.id, LOWER(TRIM(fc.name)) AS n
    FROM fundraising_categories fc
    WHERE fc.deleted_at IS NULL
) sub
WHERE fa.fundraising_category_id IS NULL
  AND fa.category IS NOT NULL
  AND TRIM(fa.category) <> ''
  AND sub.n = LOWER(TRIM(fa.category));

-- Rows still without FK: attach an arbitrary active category (first by created_at)
UPDATE fundraising_activities fa
SET fundraising_category_id = (
    SELECT fc.id
    FROM fundraising_categories fc
    WHERE fc.deleted_at IS NULL
    ORDER BY fc.created_at
    LIMIT 1
)
WHERE fa.fundraising_category_id IS NULL;

ALTER TABLE fundraising_activities
    ALTER COLUMN fundraising_category_id SET NOT NULL;

ALTER TABLE fundraising_activities
    DROP COLUMN IF EXISTS category;
