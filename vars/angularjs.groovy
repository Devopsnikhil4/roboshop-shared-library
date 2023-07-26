def call() {
    node {
        git branch: 'main', url: "https://github.com/b54-clouddevops/${COMPONENT}.git"
        env.APP_TYPE="angularjs"
        common.lintChecks()
        env.ARGS="-Dsonar.sources=."              
        common.sonarChecks()
        common.testCases()
        if(env.TAG_NAME != null) {
            common.artifacts()
        }
    }
}               

// def lintChecks(){
//     sh ''' 
//         echo Installing AngularLint for ${COMPONENT}
//         echo lint checks completed for ${COMPONENT}

//     ''' 
// }


// def call(COMPONENT) {
//     pipeline {
//         agent {  label 'WS' }
//         environment {
//             SONARCRED = credentials('SONARCRED') 
//             SONATURL  = "172.31.86.248"
//         }
//         stages {      

//             stage('Lint Checks') {
//                 steps { 
//                     script {
//                         lintChecks()
//                     }
//                 }
//             }

//             stage('Code Quality Analysis') {
//                 steps {                
//                     script {
//                         env.ARGS="-Dsonar.sources=."              
//                         common.sonarChecks()
//                     }
//                 }
//             }

//         }                                                                             
//     }
// }