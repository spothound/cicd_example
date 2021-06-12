# Running Jenkins in a docker container

### Run docker dind to allow executing docker commands on Jenkins

```
docker run --name jenkins-docker --rm --detach \
  --privileged --network jenkins --network-alias docker \
  --env DOCKER_TLS_CERTDIR=/certs \
  --volume jenkins-docker-certs:/certs/client \
  --volume jenkins-data:/var/jenkins_home \
  --publish 2376:2376 docker:dind --storage-driver overlay2
```

### Build and run Jenkins blueocean container

```
docker build -t myjenkins-blueocean:1.1 .
```

```
docker run --name jenkins-blueocean --rm --detach \
  --network jenkins --env DOCKER_HOST=tcp://docker:2376 \
  --env DOCKER_CERT_PATH=/certs/client --env DOCKER_TLS_VERIFY=1 \
  --publish 8080:8080 --publish 50000:50000 \
  --volume jenkins-data:/var/jenkins_home \
  --volume jenkins-docker-certs:/certs/client:ro \
  myjenkins-blueocean:1.1
```

Jenkins will be served at localhost:8080 if everything went right.

To debug in case of errors, try again removing the `--detach` flag and the logs
will be printed to the console, or remove the `--rm` flag to avoid the
destruction of the container and inspect it for extra information.

