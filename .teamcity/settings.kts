// Required TeamCity DSL imports
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.BuildFailureOnText
import jetbrains.buildServer.configs.kotlin.v2019_2.failureConditions.failOnText

version = "2019.2"

project {
    // This must match your VCS root ID
    val vcsRootId = "HelloWorldRepo" // Replace with your actual VCS root ID
    
    buildType(BuildAndTest) {
        vcs {
            root(DslContext.settingsRoot)
        }
    }

    buildType(DeployLocally) {
        dependencies {
            snapshot(BuildAndTest) {
                onDependencyFailure = FailureAction.FAIL_TO_START
            }
        }
    }
}

object BuildAndTest : BuildType({
    name = "Build and Test"
    id = AbsoluteId("HelloWorld_LocalBuild")

    vcs {
        root(DslContext.settingsRoot)
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
})

object DeployLocally : BuildType({
    name = "Deploy Locally"
    id = AbsoluteId("HelloWorld_LocalDeploy")

    steps {
        script {
            name = "Deploy to Local Machine"
            scriptContent = """
                echo "Deploying to local machine..."
                mkdir -p /tmp/hello-world-deploy
                cp -r dist/* /tmp/hello-world-deploy/
                echo "Deployed to /tmp/hello-world-deploy"
            """
        }
    }
})
