// some methods are imported from
// https://gitlab.gwdg.de/dariah-de/jenkins-shared-library

node {
  def mvnHome

  stage('preparation') {
    mvnHome = tool 'Maven 3.0.4'
    checkout scm
    env.JAVA_HOME="/usr/lib/jvm/java-1.8.0-openjdk-amd64"
    env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
  }

  stage('build') {
    sh "${mvnHome}/bin/mvn clean verify"
  }

  stage('deploy') {
    p2deploy('./updatesite/target/repository', 'bundled-jre')
  }

  stage('Notify') {
    notifyTglabDownstream('lab-base')
  }

}
