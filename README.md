# CICD Pipeline example

This project is just a small demo of how can we build a CICD pipeline to deploy services in AWS using Jenkins, Ansible, Terraform and Docker.

![Diagram](https://github.com/spothound/cicd_example/blob/main/sequence_diagram.png?raw=true)

## Structure
- ec2-terraform: Ansible+Terraform pipeline to deploy EC2 instances, provision them and run pre-defined Docker services on them.
- jenkins_docker: Dockerfile and data to easily build a dockerized Jenkins server to run the pipelines.
- jenkins_files: Jenkins pipelines scripts written in groovy.
- simple_mq: Simple rabbitMQ service encapsulated in a docker-compose.yaml file.
- simple_web: Simple web application written in NodeJS that tries to connect with an MQ service and report the status of this one.

