# Supabase database setup

The production backend uses the `supabase` Spring profile and the private
`trading` schema in the `bitcoin-trading-simulation` Supabase project.

Project reference: `nxrajxtplwapwurmiarc`

## 1. Enable the application login role

Generate a strong password and run the following once in the project's
Supabase SQL Editor. Do not commit the real password to Git.

```sql
alter role trading_app
  with login password 'REPLACE_WITH_A_STRONG_DATABASE_PASSWORD';
```

## 2. Configure the EC2 environment

Copy `.env.example` to `.env`. From the Supabase **Connect** dialog, choose
**Session pooler** and copy its host into `SUPABASE_DB_URL`. Session mode uses
port `5432` and supports JDBC prepared statements.

```bash
cp .env.example .env
nano .env
```

Load the variables and start Spring Boot:

```bash
set -a
source .env
set +a
./gradlew bootRun
```

For a packaged production process:

```bash
./gradlew clean bootJar
set -a
source .env
set +a
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

The application validates the existing schema at startup. It does not create
or alter production tables automatically.

## Data API and RLS

The `trading` schema is private: `anon` and `authenticated` have neither
schema usage nor table privileges. Row Level Security is therefore not used by
the current Spring-only architecture. If the schema is ever added to the
Supabase Data API, enable RLS and define ownership policies before granting any
client role access.
