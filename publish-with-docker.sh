#!/bin/bash
rm -rf ./target/
docker run -it --rm \
       -v "$PWD":/usr/src/mymaven \
       -v "$HOME/.m2":/root/.m2 \
       -v "$HOME/.ssh":/root/.ssh \
       -v "$HOME/.gitconfig":/root/.gitconfig \
       -w /usr/src/mymaven \
       maven:3-jdk-7 \
       mvn release:prepare release:perform
