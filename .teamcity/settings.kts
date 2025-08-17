version = "2023.05"

project {
    buildType {
        id("HelloWorld_LocalBuild")
        name = "Build and Test (Local)"
        
        vcs {
            root(TeamCityGitRepo)
        }
        
        steps {
            script {
                name = "Install Dependencies"
                scriptContent = "pip install -r requirements.txt"
            }
            
            script {
                name = "Run Tests"
                scriptContent = "python -m pytest"
            }
            
            script {
                name = "Build Artifact"
                scriptContent = """
                    mkdir -p dist
                    cp -r src/ dist/
                    echo "Built at: $(date)" > dist/build-info.txt
                """
            }
        }
    }
    
    buildType {
        id("HelloWorld_LocalDeploy")
        name = "Deploy (Local)"
        
        dependencies {
            snapshot(HelloWorld_LocalBuild) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
        
        steps {
            script {
                name = "Deploy Locally"
                scriptContent = """
                    echo "Deploying to local machine..."
                    cp -r dist/ /tmp/hello-world-deploy/
                    echo "Deployed to /tmp/hello-world-deploy"
                """
            }
        }
    }
}
