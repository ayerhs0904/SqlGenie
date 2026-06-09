DO $$
BEGIN
  IF NOT EXISTS (
    SELECT FROM pg_catalog.pg_roles
    WHERE  rolname = 'sqlgenie_readonly') THEN
    CREATE ROLE sqlgenie_readonly WITH LOGIN PASSWORD 'readonly_password';
  END IF;
END
$$;

GRANT USAGE ON SCHEMA public TO sqlgenie_readonly;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO sqlgenie_readonly;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO sqlgenie_readonly;
