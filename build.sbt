name := "template-api-rest-java-playframework"

version := "1.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
//  "org.codehaus.jackson" % "jackson-mapper-asl" % "1.8.5",
  "org.mongodb" % "mongo-java-driver" % "3.4.2",
  "com.rabbitmq" % "amqp-client" % "5.0.0"
)

// https://mvnrepository.com/artifact/com.rabbitmq/amqp-client


// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
