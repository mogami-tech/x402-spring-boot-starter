run_install:
    mvn install -DskipTests

run_tests:
    mvn clean install

run_example_server:
    mvn install spring-boot:run -DskipTests -f src-example-server/pom.xml