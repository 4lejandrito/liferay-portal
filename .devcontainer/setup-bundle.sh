#!/usr/bin/env bash

# Build the Liferay bundle with `ant all` and provision its configuration. Runs
# once at container creation (postCreateCommand). `ant all` unpacks a Tomcat
# bundle with Liferay deployed into ${project.dir}/../bundles, which resolves to
# /workspaces/bundles in the container, using the mounted binaries cache for
# Gradle dependencies.

set -o errexit
set -o nounset
set -o pipefail

PROJECT_DIR="/workspaces/liferay-portal"
LIFERAY_HOME="/workspaces/bundles"
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# /workspaces is root-owned; create the bundle dir up front and hand it to the
# current user so `ant all` can write into it unprivileged.
if [ ! -d "${LIFERAY_HOME}" ]; then
	sudo mkdir -p "${LIFERAY_HOME}"
	sudo chown "$(id -u):$(id -g)" "${LIFERAY_HOME}"
fi

# The project .gradle is a container-local volume (it shadows the host's, whose
# cache symlinks point at host-only paths). A fresh volume mounts root-owned, so
# hand it to the current user; `ant all` repopulates it from the binaries cache.
sudo chown "$(id -u):$(id -g)" "${PROJECT_DIR}/.gradle"

# Skip the (lengthy) build if a Tomcat bundle is already in place.
if compgen -G "${LIFERAY_HOME}/tomcat-*" > /dev/null; then
	echo "Bundle is already present at ${LIFERAY_HOME}; skipping \"ant all\"."
else
	echo "Building bundle with \"ant all\" (this takes a while)..."
	cd "${PROJECT_DIR}"
	ant all
fi

# Provision portal-ext.properties into the freshly built bundle.
"${HERE}/provision-portal-ext.sh"