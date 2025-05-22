run_install:
    mvn install -DskipTests

run_tests:
    mvn clean install

run_deploy:
    mvn -B -DskipTests clean deploy
