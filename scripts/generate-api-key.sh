#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
CLIENT_NAME="${1:-new-client}"
APPLY=false

if [[ "${2:-}" == "--apply" ]]; then
  APPLY=true
fi

cd "$ROOT"

OUTPUT="$(mvn -q -pl aggregator-service -DskipTests compile exec:java \
  -Dexec.mainClass=com.fintech.aggregator.aggregator.util.ApiKeyGenerator \
  -Dexec.args="$CLIENT_NAME" 2>&1)"

echo "$OUTPUT"

if [[ "$APPLY" == true ]]; then
  SQL="$(echo "$OUTPUT" | sed -n '/^INSERT INTO/,$p')"
  if [[ -z "$SQL" ]]; then
    echo "Could not extract INSERT statement." >&2
    exit 1
  fi
  echo
  echo "-- Applying to running PostgreSQL container..."
  docker compose exec -T postgres psql \
    -U "${POSTGRES_USER:-aggregator}" \
    -d "${POSTGRES_DB:-aggregator_db}" \
    -c "$SQL"
  echo
  echo "-- Evicting API key cache..."
  curl -sf -X POST -H "X-API-Key: ${ADMIN_API_KEY:-demo-api-key}" \
    "http://localhost:${AGGREGATOR_PORT:-8080}/api/v1/admin/api-keys/cache/evict" \
    && echo "Cache evicted. New key is active immediately." \
    || echo "Could not evict cache (is aggregator-service running?). New key will be active within 5 minutes."
fi
