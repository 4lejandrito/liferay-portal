#!/usr/bin/env bash

# Start MySQL and ensure the Liferay database and user exist. Idempotent: safe to
# run on every container start. Liferay connects over JDBC at:
# jdbc:mysql://localhost:3306/lportal (user: liferay / password: liferay)

set -o errexit
set -o nounset
set -o pipefail

sudo service mysql start

sudo mysql <<'SQL'
CREATE DATABASE IF NOT EXISTS lportal CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'liferay'@'%' IDENTIFIED WITH mysql_native_password BY 'liferay';
GRANT ALL PRIVILEGES ON lportal.* TO 'liferay'@'%';
FLUSH PRIVILEGES;
SQL

echo "MySQL is ready with database \"lportal\" and user \"liferay\"."