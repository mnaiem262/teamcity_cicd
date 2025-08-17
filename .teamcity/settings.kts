version = "2023.05"

project {
    buildType {
        id("HelloWorldBuild")
        name = "Build and Test"
        
        vcs {
            root(TeamCityGitRepo)
        }
        
        steps {
            script {
                name = "Install dependencies"
                scriptContent = "pip install -r requirements.txt"
            }
            
            script {
                name = "Run tests"
                scriptContent = "python -m pytest"
            }
            
            script {
                name = "Build package"
                scriptContent = """
                    mkdir -p dist
                    echo "Building Python package..."
                    cp -r src/ dist/
                """
            }
        }
        
        features {
            feature {
                type = "xml-report"
                param("xmlReportParsing.reportType", "junit")
                param("xmlReportParsing.reportDirs", "**/test-results/*.xml")
            }
        }
    }
    
    buildType {
        id("HelloWorldDeploy")
        name = "Deploy"
        
        dependencies {
            snapshot(HelloWorldBuild) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
        
        steps {
            script {
                name = "Deploy"
                scriptContent = """
                    echo "Deploying application..."
                    # Add your deployment commands here
                """
            }
        }
    }
}
