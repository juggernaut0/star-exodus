#!/bin/env bash
if [ ! -e out ]; then
	gradle assemble;
fi

cd out
python3 -m http.server 8080
