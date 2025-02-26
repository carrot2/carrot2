
Carrot2
-------

This is a binary release of Carrot2 ${product.version}. It contains:
  * examples/   - Java API examples,
  * artifacts/  - precompiled POMs and JARs (Maven-compatible repository).
  * dcs/        - HTTP/JSON REST service, including developer documentation
                  and a demo "search-and-cluster" application.

Run the DCS (includes built-in HTTP server):
  cd dcs
  ./dcs

Run the DCS using docker:

  docker run --rm -v "$(pwd)/dcs:/dcs" -p 8080:8080 eclipse-temurin:21-jre-alpine /dcs/dcs

then:
  * Developer documentation:   http://localhost:8080/doc
  * Java API JavaDoc:          http://localhost:8080/javadoc
  * REST service:              http://localhost:8080/service
  * demo "search" application: http://localhost:8080/frontend

Build: ${product.version}, ${product.gitrev}, ${product.buildDate}
