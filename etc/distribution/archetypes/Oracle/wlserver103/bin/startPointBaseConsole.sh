#!/bin/sh

# WARNING: This file is created by the Configuration Wizard.
# Any changes to this script may be lost when adding extensions to this configuration.

# Call setDomainEnv here to get the correct pointbase port

DOMAIN_HOME="/Users/mikemochan/dev/bea/wls103/user_projects/domains/base_domain"

. ${DOMAIN_HOME}/bin/setDomainEnv.sh

${WL_HOME}/common/bin/startPointBaseConsole.sh -port=${POINTBASE_PORT}

