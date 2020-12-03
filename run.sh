#!/bin/bash
docker build -t tiny-concurrent-server:1.0 . && docker run -P -di --name tiny-concurrent-server-container tiny-concurrent-server:1.0