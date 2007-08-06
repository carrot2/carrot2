This is an example of how to use Carrot2 API directly from a Java
application. Instructions and explanations are in the source code.

Invoke "ant" to build the project. The JARs are in tmp/dist, so:

cd tmp
cd dist
java -Djava.ext.dirs=deps-carrot2-demo-api-example-jar;. org.carrot2.apiexample.Example

should return search results and clusters.

Analyze the source code and JavaDocs carefully.