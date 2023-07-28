def lintChecks(){
    stage('Lint Checks') {
        if(env.APP_TYPE == "nodejs") {
        sh  ''' 
           echo Installing JSLint for ${COMPONENT}
           npm i jslint  
           node_modules/jslint/bin/jslint.js server.js || true
           echo lint checks completed for ${COMPONENT}        
           ''' 
        }
        else if(env.APP_TYPE == "python") {
        sh '''
            # pip3 install pylint
            # pip *.py
            echo lint checks started for ${COMPONENT} using pytlint
            echo lint checks completed for ${COMPONENT}  
        ''' 
        }
        else if(env.APP_TYPE == "java") {
        sh '''
            echo lint checks started for ${COMPONENT} using mvn
            # mvn checkstyle:check
            echo lint checks completed for ${COMPONENT}  
        ''' 
        }
        else  {
        sh '''
            echo lint checks started for ${COMPONENT} using angular js
            echo lint checks completed for ${COMPONENT}  
         ''' 
        }
    }
}


def sonarChecks(){
        stage('Sonar Checks') {
           sh "echo Sonar Checks In Progress"
           sh "sonar-scanner -Dsonar.host.url=http://172.31.86.248:9000  ${ARGS} -Dsonar.projectKey=${COMPONENT}  -Dsonar.login=admin -Dsonar.password=password"
           sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > sonar-quality-gate.sh"
           sh "bash -x sonar-quality-gate.sh admin ${SONARCRED_PSW} ${SONARURL} ${COMPONENT}"       
           sh "echo Sonar Checks Completed"        
        }
}

// This is how we can handle scripted pipeline for paralle stages
// Ref: https://stackoverflow.com/questions/57485965/how-can-i-create-parallel-stages-in-jenkins-scripted-pipeline

def testCases() {
        stage('Test Cases') {
                def stages = [:]

                stages["Unit Testing"] = {
                        echo "Unit Testing Started for ${COMPONENT}"
                        echo "Unit Testing Completed for ${COMPONENT}"
                }
                stages["Integration Testing"] = {
                        echo "Integration Testing Started for ${COMPONENT}"
                        echo "Integration Testing Completed for ${COMPONENT}"
                }
                stages["Functional Testing"] = {
                        echo "Functional Testing Started for ${COMPONENT}"
                        echo "Functional Testing Completed for ${COMPONENT}"
                }

                parallel(stages)
        }                        
}



def artifacts() {
        
        stage('Validate Artifact Version') {
            env.UPLOAD_STATUS=sh(returnStdout: true, script: "curl -L -s http://172.31.92.189:8081/service/rest/repository/browse/${COMPONENT} | grep ${COMPONENT}-${TAG_NAME}.zip || true" )
            print UPLOAD_STATUS
        }                    
                
        if(env.UPLOAD_STATUS == "") {
                stage('Prepare Artifacts') {
                        if(env.APP_TYPE == "nodejs"){
                                sh '''
                                        echo Preparing Artifacts for ${COMPONENT}
                                        npm install
                                        zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js                        
                                '''
                        }
                        else if(env.APP_TYPE == "python"){
                                sh '''
                                        echo Preparing Artifacts for ${COMPONENT}
                                        zip -r ${COMPONENT}-${TAG_NAME}.zip *.py *.int  requirements.txt                     
                                '''
                        }
                        else if(env.APP_TYPE == "java"){
                                sh '''
                                        echo Preparing Artifacts for ${COMPONENT}
                                        mvn clean package
                                        mv target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
                                        zip -r ${COMPONENT}-${TAG_NAME}.zip  ${COMPONENT}.jar          
                                '''
                        }
                        else {
                                sh '''
                                        echo Preparing Artifacts for ${COMPONENT}
                                        cd static
                                        zip -r ../${COMPONENT}-${TAG_NAME}.zip *                  
                                '''
                        }
                }
                
                stage('Upload Artifacts') {
                        withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'NEXUS_PSW', usernameVariable: 'NEXUS_USR')]) {
                                sh "echo Uploading ${COMPONENT} Artifacts To Nexus"
                                sh "curl -f -v -u ${NEXUS_USR}:${NEXUS_PSW} --upload-file ${COMPONENT}-${TAG_NAME}.zip  http://172.31.92.189:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip || true"
                                sh "echo Uploading ${COMPONENT} Artifacts To Nexus is Completed"                   
                                
                        }                
                }
        }
}
            