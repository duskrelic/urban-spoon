function main() {

  clear
  echo "-------- Initializing Dev Environment --------"
  initGlobals
  sleep 3 && clear

  echo "-------- Initializing Docker --------"
  initDocker
  sleep 3 && clear

  echo "-------- Initializing Gradle --------"
  initGradle
  sleep 3 && clear

  echo "-------- Dev Info --------"
  logInfo

}

function initGlobals() {

  # Customize locations by setting in shell env
  export PROJECT_CONFIG_HOME="${CONFIG_HOME:-$HOME/.config}/pebblepost/cpp-sync"
  export PROJECT_DATA_HOME="${DATA_HOME:-$HOME/.local/share}/pebblepost/cpp-sync"
  export PROJECT_HOME=$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)

  export TEMP_HOME=$(mktemp -d)
  chmod 755 "$TEMP_HOME"

  POSTGRES_HOST="localhost:5432"

  export VAULT_ADDR="http://localhost:8200"
  export VAULT_SECRET_PATH="secret/app/cpp-sync"

  export SPRING_PROFILES_ACTIVE="dev"
  export FILES_SRC_DIR="files"

  # Root credentials for postgres and vault
  export DEV_PASSPHRASE="cpp"

  # Colors for shell output
  export ICyan='\033[0;96m'
  export NC='\033[0m'

}

function initDocker() {

  docker-compose kill
  docker-compose rm -f
  docker-compose up -d --remove-orphans --quiet-pull

  VAULT_PID=$(docker ps -lq --filter name=vault)
  docker exec "$VAULT_PID" vault login token=$DEV_PASSPHRASE
  docker exec "$VAULT_PID" vault auth enable approle
  cat <<-EOF >"$PROJECT_CONFIG_HOME"/vault/config/cpp-sync-policy.hcl
		path "$VAULT_SECRET_PATH" {
			capabilities = [
				"list",
				"read"]
		}
		
		path "$VAULT_SECRET_PATH/*" {
			capabilities = [
			"list",
			"read"]
		}
	EOF
  docker exec "$VAULT_PID" vault policy write cpp-sync-postgres /vault/config/cpp-sync-policy.hcl
  docker exec "$VAULT_PID" vault write auth/approle/role/cpp-sync policies="cpp-sync-postgres"
  docker exec "$VAULT_PID" chmod -R +w /vault
  docker exec "$VAULT_PID" \
    sh -c \
    "vault read auth/approle/role/cpp-sync/role-id \
  -format=json >/tmp/vault/approle/role-id.json"
  docker exec "$VAULT_PID" \
    sh -c \
    "vault write -f auth/approle/role/cpp-sync/secret-id \
   -format=json >/tmp/vault/approle/secret-id.json"

  docker exec "$VAULT_PID" vault secrets disable secret
  docker exec "$VAULT_PID" vault secrets enable -version=1 -path=secret kv
  docker exec "$VAULT_PID" \
    vault kv put "$VAULT_SECRET_PATH/postgres" \
    host=$POSTGRES_HOST \
    database=$DEV_PASSPHRASE \
    username=$DEV_PASSPHRASE \
    password=$DEV_PASSPHRASE

  export VAULT_APP_ROLE_ROLE_ID=$(jq .data.role_id "$TEMP_HOME/vault/approle/role-id.json" | sed -e 's/^"//' -e 's/"$//')
  export VAULT_APP_ROLE_SECRET_ID=$(jq .data.secret_id "$TEMP_HOME/vault/approle/secret-id.json" | sed -e 's/^"//' -e 's/"$//')

  cat <<-EOF >"$PROJECT_HOME/.env"
		PROJECT_CONFIG_HOME=$PROJECT_CONFIG_HOME
		PROJECT_DATA_HOME=$PROJECT_DATA_HOME
		TEMP_HOME=$TEMP_HOME
			
		VAULT_ADDR=$VAULT_ADDR
		VAULT_SECRET_PATH=$VAULT_SECRET_PATH
		VAULT_APP_ROLE_ROLE_ID=$VAULT_APP_ROLE_ROLE_ID
		VAULT_APP_ROLE_SECRET_ID=$VAULT_APP_ROLE_SECRET_ID
			
		SPRING_PROFILES_ACTIVE=dev
			
		DEV_PASSPHRASE=$DEV_PASSPHRASE
	EOF
}

function initGradle() {
  ./gradlew clean
  ./gradlew build
}

function logInfo() {

  echo "${ICyan}$PROJECT_HOME/.env${NC}"
  cat "$PROJECT_HOME/.env"
  echo

  echo "${ICyan}Docker Status:${NC}"
  docker-compose ps
  echo

  echo "${ICyan}Postgres Dev Credentials:${NC}"
  echo "host: $POSTGRES_HOST"
  echo "database: $DEV_PASSPHRASE"
  echo "username: $DEV_PASSPHRASE"
  echo "password: $DEV_PASSPHRASE"
  echo

  mkdir -p "$PROJECT_CONFIG_HOME"/vault/dev
  cat <<-EOF >"$PROJECT_CONFIG_HOME"/vault/dev/approle.json
		{
			"role_id": "$VAULT_APP_ROLE_ROLE_ID",
			"secret_id": "$VAULT_APP_ROLE_SECRET_ID"
		}
	EOF

  # Generate client token from approle
  # to test as approle from command line
  VAULT_APP_ROLE_TOKEN=$(curl -s \
    --request POST \
    --data @"$PROJECT_CONFIG_HOME/vault/dev/approle.json" \
    "$VAULT_ADDR/v1/auth/approle/login" |
    jq .auth.client_token | sed -e 's/^"//' -e 's/"$//')

  echo "${ICyan}Vault Dev Credentials:${NC}"
  echo "host: $VAULT_ADDR"
  echo "root token: $DEV_PASSPHRASE"
  echo "role-id: $VAULT_APP_ROLE_ROLE_ID"
  echo "secret_id: $VAULT_APP_ROLE_SECRET_ID"
  echo "app-role token:" "$VAULT_APP_ROLE_TOKEN"
  echo
}

main
