#!/bin/bash
set -e

export PLATFORM=windows

mvn clean source:jar install

