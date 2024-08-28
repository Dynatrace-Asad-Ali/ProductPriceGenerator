#!/usr/bin/env bash

kubectl create namespace product-generator
kubectl create namespace dynatrace

sed -i "s,TENANTURL_TOREPLACE,$DT_URL," /workspaces/$RepositoryName/dynatrace/dynakube.yaml
sed -i "s,CLUSTER_NAME_TO_REPLACE,product-generator,"  /workspaces/$RepositoryName/dynatrace/dynakube.yaml

clusterName=`kubectl config view --minify -o jsonpath='{.clusters[].name}'`
#sed -i "s,{ENTER_YOUR_CLUSTER_NAME},$clusterName,"  /workspaces/$RepositoryName/dynatrace/values.yaml
#sed -i "s,{ENTER_YOUR_INGEST_TOKEN},$DT_LOG_INGEST_TOKEN,"  /workspaces/$RepositoryName/dynatrace/values.yaml

sed -i "s,{YOUR_DT_URL},$clusterName,"  /workspaces/$RepositoryName/deployment/ProductPriceGenerator.yaml
sed -i "s,{YOUR_DT_LOG_INGEST_TOKEN},$DT_LOG_INGEST_TOKEN,"  /workspaces/$RepositoryName/deployment/ProductPriceGenerator.yaml

#Extract the tenant name from DT_URL variable
#tenantName=`echo $DT_URL | awk -F "[:,.]" '{print $2}' | cut -c3-`
#sed -i "s,{your-environment-id},$tenantName,"  /workspaces/$RepositoryName/dynatrace/values.yaml

# Create secret for k6 to use
kubectl -n product-generator create secret generic dt-details \
  --from-literal=DT_ENDPOINT=$DT_URL \
  --from-literal=DT_API_TOKEN=$DT_OPERATOR_TOKEN

# Deploy Dynatrace
kubectl -n dynatrace create secret generic dynakube --from-literal="apiToken=$DT_OPERATOR_TOKEN"

wget -O /workspaces/$RepositoryName/dynatrace/kubernetes.yaml https://github.com/Dynatrace/dynatrace-operator/releases/download/v0.15.0/kubernetes.yaml
wget -O /workspaces/$RepositoryName/dynatrace/kubernetes-csi.yaml https://github.com/Dynatrace/dynatrace-operator/releases/download/v0.15.0/kubernetes-csi.yaml
sed -i "s,cpu: 300m,cpu: 100m," /workspaces/$RepositoryName/dynatrace/kubernetes.yaml
sed -i "s,cpu: 300m,cpu: 100m," /workspaces/$RepositoryName/dynatrace/kubernetes-csi.yaml
# Shrink resource utilisation to work on GitHub codespaces (ie. a small environment)
# Apply (slightly) customised manifests
kubectl apply -f /workspaces/$RepositoryName/dynatrace/kubernetes.yaml
kubectl apply -f /workspaces/$RepositoryName/dynatrace/kubernetes-csi.yaml
kubectl -n dynatrace wait pod --for=condition=ready --selector=app.kubernetes.io/name=dynatrace-operator,app.kubernetes.io/component=webhook --timeout=300s
kubectl -n dynatrace apply -f /workspaces/$RepositoryName/dynatrace/dynakube.yaml

#install fluentbit for log ingestion
#helm repo add fluent https://fluent.github.io/helm-charts
#helm repo update
#helm install fluent-bit fluent/fluent-bit -f /workspaces/$RepositoryName/dynatrace/values.yaml --create-namespace --namespace dynatrace-fluent-bit

kubectl apply -f deployment/ProductPriceGenerator.yaml -n product-generator

# Wait for Dynatrace to be ready
kubectl -n dynatrace wait --for=condition=Ready pod --all --timeout=10m

# Wait for travel advisor system to be ready
kubectl -n product-generator wait --for=condition=Ready pod --all --timeout=10m