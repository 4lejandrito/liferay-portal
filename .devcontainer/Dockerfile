FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

# MySQL 8.0 ships in the Ubuntu 24.04 (noble) repositories. Install the server
# and client at build time so the image is self-contained; the service itself is
# started by the devcontainer postStart hook.

RUN export DEBIAN_FRONTEND=noninteractive \
	&& apt-get update \
	&& apt-get install -y --no-install-recommends mysql-server mysql-client \
	&& rm -rf /var/lib/apt/lists/*