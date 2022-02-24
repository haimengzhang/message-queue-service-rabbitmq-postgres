## Message queue service running on kubernetes


### Setup

Install kubernetes, minikube and docker on your machine. Also, install Postman to test the endpoints.
We will build two local service images and push them to docker hub for later use in kubernetes.

Start the kubernetes cluster with 8GB of RAM:
``` 
minikube start \
  --memory 8096 \
  --extra-config=controller-manager.horizontal-pod-autoscaler-upscale-delay=1m \
  --extra-config=controller-manager.horizontal-pod-autoscaler-downscale-delay=2m \
  --extra-config=controller-manager.horizontal-pod-autoscaler-sync-period=10s
  
  ```
Increase docker desktop RAM limit if you get resource limited error.

Then, connect the Docker client to minikube:
```
  minikube docker-env
```
We want to pull docker images from docker hub to local machine with docker running in the background:
```
docker pull rabbitmq:3-management
docker pull postgres
```

Next, build the local service images and push them to docker hub.
In the service directory spring-boot-message-consumer, run:

```
  mvn install -Dskiptests
  docker build -t message-consumer .
  docker tag message-consumer:latest miniocean/message-consumer
  docker push miniocean/message-consumer
```

In the service directory spring-boot-message-producer, run:

```
    mvn install -DskipTests
    docker build -t message-producer .
    docker tag message-producer:latest miniocean/message-producer
    docker push miniocean/message-producer
```
## Run the service cointainers in docker:
Now that we have our images ready, use docker-compose to launch the multiple containers at once in detach mode:

```
docker-compose up -d
```
Now, our services are running in containers.
We could test our endpoint by going to `localhost:8090/message/publish` to publish message and `localhost:8091/message/` to fetch the images saved to db.

To stop and remove the containers:
```
docker-comose down
```

## Moving the project to Kubernetes
Use kompose to translate the docker-compose.yaml to kubernetes deployment and service yaml files. Deployment yamls define how to create the pods to host the container instance. Servce yaml files will be used to expose the deployed object with an external IP, which is used by kubernetes to load balance across the pods. 
First, We should configure the database pod to use a persistent volume for storage in the cluster.
PV is backed by physical storage and will retain the data across pods even after the pods are restarted.

### Create the persistent volume and persistent volume claim that bounds to the volume.
```
  kubectl apply -f persistent-volume.yml
  kubectl apply -f persistent-volume-claim-1.yaml
```

### Deploy POSTGRES service and deployment object:
```
kubectl apply -f postgresql-deployment.yaml
kubectl apply -f postgresql-service.yaml
kubectl apply -f persistent-volume.yml
kubectl apply -f persistent-volume-claim-1.yaml
```

### Deploy the message-consumer service and deployment object:
```   
   kubectl apply -f message-consumer-deployment.yaml
   kubectl apply -f message-consumer-service.yaml
```

### Deploy the message-publisher service and deployment object:

```   
  kubectl apply -f kube/message-publisher-deployment.yaml
  kubectl apply -f kube/message-publisher-service.yaml
```


### Deploy Rabbitmq service and deployment object:
``` 
kubectl apply -f rabbitmq-service.yaml
kubectl apply -f rabbitmq-deployment.yaml
``` 

### Deploy network policy:
``` 
kubectl apply -f resolute-networkpolicy.yaml
``` 

### Run the application in Kubernetes and test the services
Extract the URL exposed by the publisher and consumer pods by running:

``` 
minikube service message-publisher

minikube service message-consumer
```
Copy and paste the load balancer URL like shown below to Postman.
In our case, it is http://127.0.0.1:54682/message/publish.
This is the address we will use for our POST request.

![message-publisher](./message-publisher-url.png)

Note, the URL changes each time you restart the pod.

Now, draft a request Json and use postman to post to the endpoint:
```
POST <message-publisher-url>/message/publish

```

```
{

        "ts": "19941114",
        "sender": "haimeng",
        "messageAttributes": {
            "pubisher": "---publisher is ready rabbitmq---",
            "consumer" : "postgres IS READY!"
        }
        "sent_from_ip": "0.0.0.0",
        "priority": 1
}
```
Next, extract the message-consumer URL the same way as above and verify that the message is saved to our Postgres database:

```
GET <message-consumer-URL>/message
```

Flow of pushing and polling messages:
1. User send a message request to endpoint hosted on the publisher pod.
2. Kubernetes cluster schedules the available nodes to run the pods. Traffic is distributed among the same pods.
2. Publisher pod pushes the messages to a queue hosted on the rabbitmq pod.
4. Consumer pod listens and polls from the queue, and saves the data to the database running on Postgres pod.

## Productionize it

Now that we have our service, we want to scale up the deployment so that it handles more load, e.g. 100k RPS.
To better scale the services and pods, I have decoupled the application to four different components so that they can be scaled independently.
For example, we could have hundreds of message publishers running and a single instance of the message producer.

In this application, currently there is one container per pod. Each pod runs an instance of the application container on the cluster nodes independent of one another.
In need of handling large traffic, we can duplicate the number of pods in deployment to have more instances of each application running.
We can also increase the number of nodes in the cluster where the scheduled pods run. In case of node failure, identical pods will be scheduled on other nodes in the cluster.


In case of large incoming traffic to queue the messages, we can scale the number of replicas to 8 for the message publisher and rabbitmq.

```
kubectl scale --replicas=20 deployment/message-publisher
kubectl scale --replicas=20 deployment/rabbitmq
```
### Horizontal pod autoscaler
Another way is to use horizontal pod autoscaling, we can run:
```
kubectl autoscale deployment/message-publisher --min=10 --max=20 --cpu-percent=80
```
Kubernetes auto scaler works by monitoring metrics. As it sees more messages request coming to queue up, it will add more pods to run more application instances.
To use the metrics, we will have to install a metrics API, e.g. Custom Metrics API. We also need Prometheus to collect and store the metrics data as time series data.

With this setup, we could write a yaml file to create an horizontal pod autoscaling object in Kubernetes. In the yaml, we tell Kubernetes to use the metrics to scale up the pods. For example, we can scale up if there are more than 50 messages in the queue. We also tell Kubernetes the min and max pods to run as a limit. 
Run the command to apply the autoscaling object:
```
kubectl create -f hpa.yaml
```
### Cluster autoscaler
In addition, if we have the kubernetes cluster autoscaler set up such as in GCE, the autoscaler will resize the cluster by adding more nodes to the cluster if there are pending pods which could schedule on a new node.

