run_install:
    mvn install -DskipTests

run_tests:
    mvn clean install

run_deploy:
    mvn -B -DskipTests clean deploy

# Release ==============================================================================================================
start_release:
    git remote set-url origin git@github.com:mogami-tech/x402-spring-boot-starter.git
    git checkout development
    git pull
    git status
    mvn gitflow:release-start

finish_release:
    mvn gitflow:release-finish -DskipTests
