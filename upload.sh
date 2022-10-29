#!/bin/bash

# Script to upload CI builds to nnchan

curl -F "debug=@${DBG}" -F "normal=@${NML}" -F "key=${KEY}" -F "branch=${BRANCH}" -F "commit=${COMMIT}" http://nnp.nnchan.ru/nm/dev/ci.php
