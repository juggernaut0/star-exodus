#!/usr/bin/env bash
if [ ! -e out ]; then
	./gradlew assemble;
fi

cd out
python3 -m http.server 8080
