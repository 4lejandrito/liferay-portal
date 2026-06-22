#!/bin/bash

set -o errexit
set -o nounset
set -o pipefail

cd "$(dirname "${BASH_SOURCE[0]}")"

source ./run.sh

head_ref=${1:-HEAD}

base_commit=$(git merge-base master ${head_ref})

pr_dir=$(mktemp --directory)

trap 'rm --force --recursive "${pr_dir}"' EXIT

_REVIEW_ENV=()
_REVIEW_HOME=${HOME}
_REVIEW_HOME_DIR=${HOME}
_REVIEW_LIFERAY=$(git rev-parse --show-toplevel)
_REVIEW_PATH=/review/sandbox-bin:${PATH}
_REVIEW_USER=${USER}

_run_review "${base_commit}..${head_ref}" "${head_ref}"