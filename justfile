run_install:
    mvn install -DskipTests

run_tests:
    mvn clean install

run_deploy:
    mvn -B -DskipTests clean deploy

run_example_server:
    mvn -DskipTests install;mvn spring-boot:run -DskipTests -f src-example-server/pom.xml
