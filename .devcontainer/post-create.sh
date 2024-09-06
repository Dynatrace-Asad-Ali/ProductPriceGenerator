#!/bin/bash

# Install
kind create cluster --config .devcontainer/kind-cluster.yml --wait 300s
#curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64
#sudo install minikube-linux-amd64 /usr/local/bin/minikube
chmod +x .devcontainer/deployment.sh && .devcontainer/deployment.sh
