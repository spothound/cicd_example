#!groovy

// Build Parameters
properties([ parameters([
  string( name: 'AWS_ACCESS_KEY_ID', defaultValue: ''),
  string( name: 'AWS_SECRET_ACCESS_KEY', defaultValue: ''),
  choice( name: 'VERBOSITY', choices: ['-v', '-vv', '-vvv']),
  choice( name: 'SERVICE', choices: ['simple_web', 'simple_mq']),
  string( name: 'PORT', defaultValue: '80'),
  string( name: 'MQ_ADDRESS', defaultValue: 'localhost'),
  string( name: 'MQ_PORT', defaultValue: '5672'),
]), pipelineTriggers([]) ])

aws_credentials = '[default]'
aws_credentials += "\naws_access_key_id = ${AWS_ACCESS_KEY_ID}"
aws_credentials += "\naws_secret_access_key = ${AWS_SECRET_ACCESS_KEY}"

repositories = [
  'simple_web': 'https://github.com/spothound/simple_web.git',
  'simple_mq': 'https://github.com/spothound/simple_mq.git'
]

// variables
String service = SERVICE
String verbosity = VERBOSITY
String port = PORT
String mq_address = MQ_ADDRESS
String mq_port = MQ_PORT


node {
  ansiColor{
    try{
      env.PATH += ":/opt/terraform_0.7.13/"

      stage('Build info'){
        currentBuild.displayName = "[#${BUILD_NUMBER}][${SERVICE}]"
      }

      stage ('Checkout') {
        git branch: 'main',
          url: 'https://github.com/spothound/ec2-terraform'
      }

      stage('Set up AWS credentials'){
        dir('terraform/.aws'){
          writeFile(file: 'credentials', text: aws_credentials)
        }
      }

      stage ('Create service') {
        ansiblePlaybook(
            disableHostKeyChecking: true,
            extraVars:[
              service_name: service,
              sources_repository: repositories[service],
              deployment_id: "#${BUILD_NUMBER}",
              port: port,
              mq_address: mq_address,
              mq_port: mq_port
            ],
            extras: verbosity,
            playbook: 'ansible/create.yaml',
            colorized: true
        )
      }

      stage ('Test service') {
        echo "Insert your infrastructure test of choice and/or application validation here."
        sleep 2
      }

      stage ('Manual testing'){
        timeout(time: 8, unit: 'HOURS') {
          input 'Do you want to proceed to the destroy of the service?'
        }
      }

    }finally{
      stage ('Destroy service') {
        ansiblePlaybook(
            disableHostKeyChecking: true,
            extras: verbosity,
            extraVars:[
              deployment_id: "#${BUILD_NUMBER}"
            ],
            playbook: 'ansible/destroy.yaml',
            colorized: true
        )
      }
    }
  }
}
