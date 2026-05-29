#!/usr/bin/env bash

set -o nounset -o pipefail

HOOKS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

export GIT_CONFIG_GLOBAL=/dev/null
export GIT_CONFIG_SYSTEM=/dev/null

function fail {
	echo "FAIL: ${*}" >&2

	exit 1
}

function assert_equals {
	local actual="${1}"
	local expected="${2}"
	local message="${3:-}"

	[[ ${actual} == "${expected}" ]] ||
		fail "expected [${expected}] but got [${actual}] ${message}"
}

function assert_contains {
	local haystack="${1}"
	local needle="${2}"
	local message="${3:-}"

	[[ ${haystack} == *"${needle}"* ]] ||
		fail "expected to find [${needle}] in [${haystack}] ${message}"
}

function assert_file_exists {
	[[ -e ${1} ]] || fail "expected file to exist: ${1}"
}

function assert_file_absent {
	[[ ! -e ${1} ]] || fail "expected file to be absent: ${1}"
}

function assert_file_contains {
	local file="${1}"
	local needle="${2}"

	[[ -f ${file} ]] || fail "expected file to exist: ${file}"

	grep --fixed-strings --quiet -- "${needle}" "${file}" ||
		fail "expected file ${file} to contain [${needle}]; contents:
$(cat "${file}")"
}

function assert_file_not_contains {
	local file="${1}"
	local needle="${2}"

	[[ -f ${file} ]] || fail "expected file to exist: ${file}"

	! grep --fixed-strings --quiet -- "${needle}" "${file}" ||
		fail "expected file ${file} NOT to contain [${needle}]; contents:
$(cat "${file}")"
}

function assert_file_has_line {
	local file="${1}"
	local line="${2}"

	[[ -f ${file} ]] || fail "expected file to exist: ${file}"

	grep --fixed-strings --line-regexp --quiet -- "${line}" "${file}" ||
		fail "expected file ${file} to contain exact line [${line}]; contents:
$(cat "${file}")"
}

function assert_success {
	local status="${1}"
	local message="${2:-}"

	[[ ${status} -eq 0 ]] || fail "expected success but got exit ${status} ${message}"
}

function assert_failure {
	local status="${1}"
	local message="${2:-}"

	[[ ${status} -ne 0 ]] || fail "expected failure but got exit 0 ${message}"
}

function load_common {
	source "${HOOKS_DIR}/_common.sh"

	set +o errexit +o nounset +o pipefail
}

function setup_mocks {
	MOCK_BIN="${TEST_TMP}/mock-bin"
	MOCK_LOG="${TEST_TMP}/mock.log"

	mkdir -p "${MOCK_BIN}"
	: > "${MOCK_LOG}"

	export MOCK_BIN MOCK_LOG
	export PATH="${MOCK_BIN}:${PATH}"

	install_mock mysql 0
	install_mock nc 1
	install_mock ant 0
}

function install_mock {
	local name="${1}"
	local exit_code="${2:-0}"

	cat > "${MOCK_BIN}/${name}" <<EOF
#!/usr/bin/env bash
echo "${name} \${*}" >> "${MOCK_LOG}"
exit ${exit_code}
EOF

	chmod +x "${MOCK_BIN}/${name}"
}

function mock_log {
	cat "${MOCK_LOG}" 2>/dev/null || true
}

function make_bundle {
	local bundles_dir="${1}"
	local main_tomcat="${2:-__MAIN_TOMCAT__}"

	local tomcat="${bundles_dir}/tomcat-9.0.99"

	mkdir -p "${tomcat}/bin"
	mkdir -p "${tomcat}/conf"
	mkdir -p "${tomcat}/webapps/ROOT/WEB-INF/classes"
	mkdir -p "${bundles_dir}/glowroot"

	cat > "${tomcat}/conf/server.xml" <<'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<Server port="8005" shutdown="SHUTDOWN">
	<Service name="Catalina">
		<Connector connectionTimeout="20000" port="8080" protocol="HTTP/1.1" redirectPort="8443" />
		<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol" SSLEnabled="true" />
		<Connector protocol="AJP/1.3"
			address="::1"
			port="8009"
			redirectPort="8443" />
	</Service>
</Server>
EOF

	cat > "${tomcat}/bin/setenv.sh" <<EOF
#!/usr/bin/env bash
# CATALINA_BASE resolves under ${main_tomcat}
# WORKTREE_REF=__MAIN_WORKTREE__/modules
JPDA_ADDRESS="8000"
JPDA_TRANSPORT="dt_socket"
EOF

	cat > "${tomcat}/bin/catalina.sh" <<'EOF'
#!/usr/bin/env bash
echo "catalina ${*}" >> "$(cd "$(dirname "${0}")/../.." && pwd)/.catalina-invoked"
EOF
	chmod +x "${tomcat}/bin/catalina.sh"

	cat > "${tomcat}/bin/shutdown.sh" <<'EOF'
#!/usr/bin/env bash
echo "shutdown ${*}" >> "$(cd "$(dirname "${0}")/../.." && pwd)/.shutdown-invoked"
EOF
	chmod +x "${tomcat}/bin/shutdown.sh"

	cat > "${tomcat}/webapps/ROOT/WEB-INF/classes/portal-developer.properties" <<'EOF'
module.framework.properties.osgi.console=localhost:11311
EOF

	cat > "${bundles_dir}/glowroot/admin.json" <<'EOF'
{"web":{"port":4000}}
EOF

	cat > "${bundles_dir}/portal-setup-wizard.properties" <<'EOF'
jdbc.default.username=root
EOF

	echo "${tomcat}"
}

function make_main_worktree {
	FIXTURE_ROOT="${TEST_TMP}/repo"
	FIXTURE_MAIN_WORKTREE="${FIXTURE_ROOT}/liferay-portal"

	local worktree="${FIXTURE_MAIN_WORKTREE}"

	FIXTURE_BUNDLES="${FIXTURE_ROOT}/bundles"

	mkdir -p "${worktree}"

	FIXTURE_MAIN_TOMCAT="$(make_bundle "${FIXTURE_BUNDLES}")"

	_sed_inplace "s|__MAIN_TOMCAT__|${FIXTURE_MAIN_TOMCAT}|g" \
		"${FIXTURE_MAIN_TOMCAT}/bin/setenv.sh"
	_sed_inplace "s|__MAIN_WORKTREE__|${FIXTURE_MAIN_WORKTREE}|g" \
		"${FIXTURE_MAIN_TOMCAT}/bin/setenv.sh"

	git -C "${worktree}" init --quiet --initial-branch=master
	git -C "${worktree}" config user.email "test@example.com"
	git -C "${worktree}" config user.name "Test"

	cat > "${worktree}/app.server.properties" <<EOF
app.server.parent.dir=${FIXTURE_BUNDLES}
EOF

	mkdir -p "${worktree}/modules"
	echo "root project" > "${worktree}/settings.gradle"

	mkdir -p "${worktree}/.gradle"
	cat > "${worktree}/.gradle/gradle.properties" <<EOF
liferay.home=${FIXTURE_BUNDLES}
gradle.user.home=${FIXTURE_BUNDLES}/.gradle-home
systemProp.liferay.arquillian.port=32763
some.worktree.path=${worktree}/modules
EOF

	git -C "${worktree}" add --all
	git -C "${worktree}" commit --quiet --message "Initial"
}

function hook_input {
	jq --null-input --arg cwd "${1}" --arg name "${2}" \
		'{cwd: $cwd, name: $name}'
}

function run_create {
	CREATE_OUTPUT="$(
		"${HOOKS_DIR}/worktree-create.sh" \
			< <(hook_input "${1}" "${2}") \
			2> "${TEST_TMP}/create.stderr"
	)"

	CREATE_STATUS=${?}
	CREATE_STDERR="$(cat "${TEST_TMP}/create.stderr")"
}

function test_main_provision_none_only_creates_worktree {
	load_common
	make_main_worktree
	setup_mocks

	export LIFERAY_PROVISION=none

	run_create "${FIXTURE_MAIN_WORKTREE}" lite

	local worktree="${FIXTURE_ROOT}/liferay-portal-lite"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"
	assert_equals "${CREATE_OUTPUT}" "${worktree}"
	assert_file_exists "${worktree}/settings.gradle"

	assert_file_absent "${worktree}/app.server.${USER}.properties"
	assert_file_absent "${worktree}/bundle"
	assert_equals "$(mock_log)" ""
}

function test_main_reuse_provisions_full_worktree {
	load_common
	make_main_worktree
	setup_mocks

	unset LIFERAY_PROVISION LIFERAY_PROVISION_SKIP_TOMCAT

	run_create "${FIXTURE_MAIN_WORKTREE}" demo

	local worktree="${FIXTURE_ROOT}/liferay-portal-demo"
	local bundle="${worktree}/bundle"
	local tomcat="${bundle}/tomcat-9.0.99"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"
	assert_equals "${CREATE_OUTPUT}" "${worktree}"

	assert_file_has_line "${worktree}/app.server.${USER}.properties" \
		'app.server.parent.dir=${project.dir}/bundle'
	assert_file_exists "${tomcat}/conf/server.xml"

	assert_file_contains "${bundle}/.worktree-port-offset" "1"

	local server_xml="${tomcat}/conf/server.xml"

	assert_file_contains "${server_xml}" 'port="8081"'
	assert_file_contains "${server_xml}" 'port="8006"'
	assert_file_contains "${server_xml}" 'port="8010"'
	assert_file_contains "${server_xml}" 'port="8444"'
	assert_file_contains "${server_xml}" 'redirectPort="8444"'
	assert_file_not_contains "${server_xml}" 'port="8080"'
	assert_file_not_contains "${server_xml}" 'port="8005"'
	assert_file_not_contains "${server_xml}" 'port="8009"'
	assert_file_not_contains "${server_xml}" 'port="8443"'
	assert_file_not_contains "${server_xml}" 'redirectPort="8443"'

	assert_file_has_line "${tomcat}/bin/setenv.sh" 'JPDA_ADDRESS="8001"'

	local portal_ext="${bundle}/portal-ext.properties"

	assert_file_has_line "${portal_ext}" \
		"module.framework.properties.osgi.console=11312"
	assert_file_has_line \
		"${tomcat}/webapps/ROOT/WEB-INF/classes/portal-developer.properties" \
		"module.framework.properties.osgi.console=localhost:11312"

	local es_config="${bundle}/osgi/configs/com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration.config"

	assert_file_has_line "${es_config}" 'sidecarHttpPort="9202"'
	assert_file_has_line "${es_config}" 'transportTcpPort="9302"'
	assert_file_has_line "${es_config}" 'networkBindHost="127.0.0.1"'
	assert_file_has_line "${es_config}" 'networkPublishHost="127.0.0.1"'

	assert_file_has_line \
		"${bundle}/osgi/configs/com.liferay.arquillian.extension.junit.bridge.connector.ArquillianConnector.config" \
		'port="32764"'
	assert_file_has_line \
		"${bundle}/osgi/configs/com.liferay.data.guard.connector.DataGuardConnector.config" \
		'port="42764"'
	assert_file_has_line "${worktree}/.gradle/gradle.properties" \
		"systemProp.liferay.arquillian.port=32764"
	assert_equals "$(jq .web.port "${bundle}/glowroot/admin.json")" 4001

	assert_file_has_line "${portal_ext}" "liferay.home=${bundle}"
	assert_file_has_line "${portal_ext}" \
		"portal.instance.inet.socket.address=localhost:8081"

	assert_file_has_line "${portal_ext}" \
		"jdbc.default.driverClassName=com.mysql.cj.jdbc.Driver"
	assert_file_has_line "${portal_ext}" \
		"jdbc.default.url=jdbc:mysql://localhost/lportal_demo?characterEncoding=UTF-8&dontTrackOpenResources=true&holdResultsOpenOverStatementClose=true&serverTimezone=GMT&useFastDateParsing=false&useUnicode=true"
	assert_file_has_line "${portal_ext}" "jdbc.default.username=root"
	assert_file_has_line "${portal_ext}" "jdbc.default.password="
	assert_contains "$(mock_log)" "CREATE DATABASE IF NOT EXISTS lportal_demo"

	assert_file_has_line "${worktree}/modules/test/playwright/.env.local" \
		"PORTAL_URL=http://localhost:8081"

	local poshi="${worktree}/test.${USER}.properties"

	assert_file_has_line "${poshi}" "default.portal.url=http://localhost:8081"
	assert_file_has_line "${poshi}" "instance.url=http://localhost:8081"
	assert_file_has_line "${poshi}" "test.url=http://localhost:8081"

	assert_file_contains "${worktree}/.gradle/init.d/worktree-ports.gradle" \
		"extension.portNumber = 8081"

	assert_file_exists "${bundle}/.catalina-invoked"
}

function test_main_reuse_rewrites_paths_from_main_to_worktree {
	load_common
	make_main_worktree
	setup_mocks

	unset LIFERAY_PROVISION LIFERAY_PROVISION_SKIP_TOMCAT

	run_create "${FIXTURE_MAIN_WORKTREE}" demo

	local worktree="${FIXTURE_ROOT}/liferay-portal-demo"
	local bundle="${worktree}/bundle"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"

	local gradle_properties="${worktree}/.gradle/gradle.properties"

	assert_file_has_line "${gradle_properties}" "liferay.home=${bundle}"
	assert_file_has_line "${gradle_properties}" "gradle.user.home=${bundle}/.gradle-home"
	assert_file_has_line "${gradle_properties}" "some.worktree.path=${worktree}/modules"
	assert_file_not_contains "${gradle_properties}" "${FIXTURE_BUNDLES}"
	assert_file_not_contains "${gradle_properties}" "${FIXTURE_MAIN_WORKTREE}/"

	local setenv="${bundle}/tomcat-9.0.99/bin/setenv.sh"

	assert_file_contains "${setenv}" "${bundle}/tomcat-9.0.99"
	assert_file_not_contains "${setenv}" "${FIXTURE_MAIN_TOMCAT}"
	assert_file_contains "${setenv}" "${worktree}/modules"
	assert_file_not_contains "${setenv}" "${FIXTURE_MAIN_WORKTREE}/modules"
}

function test_main_reuse_assigns_next_free_offset_when_one_is_claimed {
	load_common
	make_main_worktree
	setup_mocks

	unset LIFERAY_PROVISION LIFERAY_PROVISION_SKIP_TOMCAT

	local occupied="${FIXTURE_ROOT}/liferay-portal-occupied"
	local occupied_bundle="${FIXTURE_ROOT}/occupied-bundle"

	git -C "${FIXTURE_MAIN_WORKTREE}" worktree add -b occupied "${occupied}" >&2

	mkdir -p "${occupied_bundle}"
	printf 'app.server.parent.dir=%s\n' "${occupied_bundle}" \
		> "${occupied}/app.server.${USER}.properties"
	echo 1 > "${occupied_bundle}/.worktree-port-offset"

	run_create "${FIXTURE_MAIN_WORKTREE}" demo

	local bundle="${FIXTURE_ROOT}/liferay-portal-demo/bundle"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"

	assert_file_contains "${bundle}/.worktree-port-offset" "2"
	assert_file_contains "${bundle}/tomcat-9.0.99/conf/server.xml" 'port="8082"'
}

function test_main_reuse_uses_existing_elasticsearch7_config {
	load_common
	make_main_worktree
	setup_mocks

	unset LIFERAY_PROVISION LIFERAY_PROVISION_SKIP_TOMCAT

	local configs="${FIXTURE_BUNDLES}/osgi/configs"

	mkdir -p "${configs}"
	: > "${configs}/com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.config"

	run_create "${FIXTURE_MAIN_WORKTREE}" demo

	local new_configs="${FIXTURE_ROOT}/liferay-portal-demo/bundle/osgi/configs"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"
	assert_file_contains \
		"${new_configs}/com.liferay.portal.search.elasticsearch7.configuration.ElasticsearchConfiguration.config" \
		'sidecarHttpPort="9202"'
	assert_file_absent \
		"${new_configs}/com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration.config"
}

function test_main_reuse_checks_out_existing_branch {
	load_common
	make_main_worktree
	setup_mocks

	unset LIFERAY_PROVISION
	export LIFERAY_PROVISION_SKIP_TOMCAT=1

	git -C "${FIXTURE_MAIN_WORKTREE}" branch existing >&2

	run_create "${FIXTURE_MAIN_WORKTREE}" existing

	local worktree="${FIXTURE_ROOT}/liferay-portal-existing"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"
	assert_equals "${CREATE_OUTPUT}" "${worktree}"
	assert_equals \
		"$(git -C "${worktree}" rev-parse --abbrev-ref HEAD)" \
		existing
}

function test_main_reuse_skips_tomcat_when_requested {
	load_common
	make_main_worktree
	setup_mocks

	unset LIFERAY_PROVISION
	export LIFERAY_PROVISION_SKIP_TOMCAT=1

	run_create "${FIXTURE_MAIN_WORKTREE}" demo

	local bundle="${FIXTURE_ROOT}/liferay-portal-demo/bundle"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"

	assert_file_contains "${bundle}/.worktree-port-offset" "1"
	assert_file_absent "${bundle}/.catalina-invoked"
}

function test_main_dies_when_name_missing {
	load_common
	make_main_worktree
	setup_mocks

	local status=0

	"${HOOKS_DIR}/worktree-create.sh" \
		< <(jq --null-input --arg cwd "${FIXTURE_MAIN_WORKTREE}" '{cwd: $cwd}') \
		> /dev/null 2>&1 || status=${?}

	assert_failure "${status}"
}

function test_main_fresh_runs_ant_all {
	load_common
	make_main_worktree
	setup_mocks

	export LIFERAY_PROVISION=fresh
	export LIFERAY_PROVISION_SKIP_TOMCAT=1
	export FIXTURE_BUNDLES

	cat > "${MOCK_BIN}/ant" <<EOF
#!/usr/bin/env bash
echo "ant \${*}" >> "${MOCK_LOG}"
cp -a "${FIXTURE_BUNDLES}" "\$(pwd)/bundle"
EOF
	chmod +x "${MOCK_BIN}/ant"

	run_create "${FIXTURE_MAIN_WORKTREE}" fresh

	local bundle="${FIXTURE_ROOT}/liferay-portal-fresh/bundle"

	assert_success "${CREATE_STATUS}" "${CREATE_STDERR}"
	assert_contains "$(mock_log)" "ant all"

	assert_file_contains "${bundle}/.worktree-port-offset" "1"
	assert_file_contains "${bundle}/tomcat-9.0.99/conf/server.xml" 'port="8081"'
}

function main {
	local name_filter="${1:-}"

	if [[ -t 1 ]]
	then
		local green=$'\033[32m'
		local red=$'\033[31m'
		local dim=$'\033[2m'
		local reset=$'\033[0m'
	else
		local green="" red="" dim="" reset=""
	fi

	local test_functions

	mapfile -t test_functions < <(
		declare -F | awk '{print $3}' | grep '^test_' | sort
	)

	local passed=0
	local failed=0
	local failed_names=()

	local test_function

	for test_function in "${test_functions[@]}"
	do
		if [[ -n ${name_filter} && ${test_function} != *"${name_filter}"* ]]
		then
			continue
		fi

		local output

		output="$(
			TEST_TMP="$(mktemp -d "${TMPDIR:-/tmp}/worktree-create-test.XXXXXX")"
			export TEST_TMP

			trap 'rm -rf "${TEST_TMP}"' EXIT

			set +o errexit +o nounset

			"${test_function}" 2>&1
		)"

		if [[ ${?} -eq 0 ]]
		then
			passed=$((passed + 1))

			echo "${green}ok${reset}   ${test_function}"
		else
			failed=$((failed + 1))
			failed_names+=("${test_function}")

			echo "${red}FAIL${reset} ${test_function}"

			if [[ -n ${output} ]]
			then
				echo "${output}" | sed "s/^/     ${dim}|${reset} /"
			fi
		fi
	done

	echo
	echo "${dim}--------------------------------------------------${reset}"

	if [[ ${failed} -eq 0 ]]
	then
		echo "${green}All ${passed} tests passed.${reset}"

		return 0
	fi

	echo "${red}${failed} failed${reset}, ${passed} passed."

	local name

	for name in "${failed_names[@]}"
	do
		echo "  - ${name}"
	done

	return 1
}

main "${@}"