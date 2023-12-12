#!/bin/bash

REPO_A_OWNER="PietroSassone"
REPO_A_NAME="github-actions-experiment"
REPO_A_WORKFLOW_NAME="repo_dispatch.yaml"

COMMIT_HASH=$(git rev-parse HEAD)

curl -X POST \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Authorization: Bearer $PAT_TOKEN" \
  "https://api.github.com/repos/$REPO_A_OWNER/$REPO_A_NAME/actions/workflows/$REPO_A_WORKFLOW_NAME/dispatches" \
  -d '{"ref":"main", "inputs": {"commit_hash": "'$COMMIT_HASH'"}}'

