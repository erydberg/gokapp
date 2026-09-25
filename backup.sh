#!/bin/bash
set -euo pipefail

# Backs up the gokapp MySQL database using MySQL Shell's util.dumpInstance().
# mysqldump is being phased out upstream in favor of this tool, which also
# dumps in parallel and compresses output.
#
# Usage: ./backup.sh [backup-dir]
#
# mysqlsh runs inside the gokapp-mysql container (the mysql image ships it),
# so the shell version always matches the server.
#
# Restore with (load-dump needs local_infile, which is off by default; it is
# enabled only for the load):
#   docker cp "<backup-dir>/<dump>" gokapp-mysql:/tmp/restore
#   docker exec gokapp-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SET GLOBAL local_infile=ON"
#   docker exec -i gokapp-mysql mysqlsh --uri root@localhost:3306 \
#     --passwords-from-stdin -- util load-dump /tmp/restore <<< "$MYSQL_ROOT_PASSWORD"
#   docker exec gokapp-mysql mysql -uroot -p"$MYSQL_ROOT_PASSWORD" -e "SET GLOBAL local_infile=OFF"
#   docker exec gokapp-mysql rm -rf /tmp/restore
#
# A dump can also be used to move between MySQL versions that can't upgrade
# in place (e.g. 9.5 -> 26.7): restore it into a fresh volume on the new version.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f .env ]; then
    echo "Error: .env not found. Run ./start.sh dev|prod first (it copies .env.dev/.env.prod to .env)."
    exit 1
fi
# Strip CR so .env files with Windows line endings don't leak '\r' into values
# (docker compose strips it, so MySQL's actual password has none).
set -a
source <(tr -d '\r' < .env)
set +a

if [ "$(docker inspect -f '{{.State.Running}}' gokapp-mysql 2>/dev/null)" != "true" ]; then
    echo "Error: gokapp-mysql container is not running."
    exit 1
fi

BACKUP_ROOT="${1:-$SCRIPT_DIR/backups}"
RETENTION_DAYS="${BACKUP_RETENTION_DAYS:-14}"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
DUMP_NAME="gokapp-${TIMESTAMP}"

mkdir -p "$BACKUP_ROOT"

echo "Dumping database '${MYSQL_DATABASE}' from gokapp-mysql into ${BACKUP_ROOT}/${DUMP_NAME} ..."

CONTAINER_DUMP_DIR="/tmp/${DUMP_NAME}"
trap 'docker exec gokapp-mysql rm -rf "$CONTAINER_DUMP_DIR" >/dev/null 2>&1 || true' EXIT

echo "$MYSQL_ROOT_PASSWORD" | docker exec -i gokapp-mysql \
    mysqlsh --uri "root@localhost:3306" --passwords-from-stdin \
    -- util dump-instance "$CONTAINER_DUMP_DIR"

docker cp "gokapp-mysql:${CONTAINER_DUMP_DIR}" "${BACKUP_ROOT}/${DUMP_NAME}"

echo "Backup written to ${BACKUP_ROOT}/${DUMP_NAME}"

if [ "$RETENTION_DAYS" -gt 0 ]; then
    echo "Pruning backups older than ${RETENTION_DAYS} days..."
    find "$BACKUP_ROOT" -mindepth 1 -maxdepth 1 -type d -name 'gokapp-*' -mtime "+${RETENTION_DAYS}" -exec rm -rf {} +
fi

echo "Done."
