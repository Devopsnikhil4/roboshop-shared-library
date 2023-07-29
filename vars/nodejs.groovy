def call(COMPONENT) {
    pipeline {
        agent {  label 'WS' }
        environment {
            SONARCRED = credentials('SONARCRED') 
            NEXUS = credentials('NEXUS') 
            SONARURL  = "54.205.223.197"
            NEXUSURL  = "107.20.6.151"
        }
        stages {      

            stage('Lint Checks') {
                steps { 
                    script {
                        lintChecks()
                    }
                }
            }

            stage('Code Compile') {
                steps {                
                        sh "npm install"
                }
            }

            stage('Sonar Checks') {
                steps {
                    script {
                        env.ARGS="-Dsonar.sources=."              
                        common.sonarChecks()
                    }
                }
            }

            stage('Test Cases') {
                parallel {
                    stage('Unit Testing') {
                        steps {
                            sh "echo Unit testing started"
                            sh "echo Unit testing completed"
                        }
                    }
                    stage('Integtation Testing') {
                        steps {
                            sh "echo Integtation testing started"
                            sh "echo Integtation testing completed"
                        }
                    }
                    stage('Functional Testing') {
                        steps {
                            sh "echo Functional testing started"
                            sh "echo Functional testing completed"
                        }
                    }
                }
            }

            stage('Validate Artifact Version') {
                when { expression { env.TAG_NAME != null } }
                steps {
                    script {
                       env.UPLOAD_STATUS=sh(returnStdout: true, script: 'curl -L -s http://${NEXUSURL}:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true')
                       print UPLOAD_STATUS
                    }                    
                }
            }

            stage('Prepare Artifacts') {
                when { 
                    expression { env.TAG_NAME != null } 
                    expression { env.UPLOAD_STATUS == "" }
                }
                steps {
                    sh '''
                        echo Preparing Artifacts for ${COMPONENT}
                        npm install
                        zip ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
                       
                       '''
                }
            }

            stage('Upload Artifacts') {
                when { 
                    expression { env.TAG_NAME != null } 
                    expression { env.UPLOAD_STATUS == "" }
                }
                steps {
                    sh  '''
                        echo Uploading ${COMPONENT} Artifacts To Nexus
                        curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip  http://172.31.92.189:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
                        echo Uploading ${COMPONENT} Artifacts To Nexus is Completed

                        '''
                }
            }
        }                                                                             
    }
}

*/