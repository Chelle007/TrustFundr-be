-- Run once on existing PostgreSQL DBs that already have column donor_account_id.
-- Hibernate ddl-auto=update may not rename columns in place; this keeps data.
ALTER TABLE donations RENAME COLUMN donor_account_id TO donee_account_id;
