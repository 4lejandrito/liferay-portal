#!/usr/bin/env bash

# Provision portal-ext.properties into the Liferay Home. The app server lives at
# ${project.dir}/../bundles (app.server.properties), which resolves to
# /workspaces/bundles inside the container. Idempotent and non-destructive: an
# existing portal-ext.properties is left untouched so local edits survive a
# container rebuild.

set -o errexit
set -o nounset
set -o pipefail

LIFERAY_HOME="/workspaces/bundles"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# /workspaces is root-owned; create the sibling bundles dir with sudo and hand
# it to the current user so the rest runs unprivileged.
if [ ! -d "${LIFERAY_HOME}" ]; then
	sudo mkdir -p "${LIFERAY_HOME}"
	sudo chown "$(id -u):$(id -g)" "${LIFERAY_HOME}"
fi

if [ -f "${LIFERAY_HOME}/portal-ext.properties" ]; then
	echo "portal-ext.properties is already present at ${LIFERAY_HOME}; leaving it as is."
else
	cp "${HERE}/portal-ext.properties" "${LIFERAY_HOME}/portal-ext.properties"
	echo "Provisioned ${LIFERAY_HOME}/portal-ext.properties."
fi

# Give the developer an empty file for one-off overrides without editing the
# managed portal-ext.properties.
[ -f "${LIFERAY_HOME}/portal-custom.properties" ] || \
	touch "${LIFERAY_HOME}/portal-custom.properties"