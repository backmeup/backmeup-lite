import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "backmeup-lite-play2"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Core dependencies
      "commons-io" % "commons-io" % "2.4",
      "org.elasticsearch" % "elasticsearch" % "0.19.10",
      "org.apache.tika" % "tika-core" % "1.2",
      "org.apache.tika" % "tika-parsers" % "1.2",
      "org.im4java" % "im4java" % "1.2.0",
      
      // ZIP plugin dependencies
      "org.apache.commons" % "commons-compress" % "1.3",

      // Twitter plugin dependencies
      "org.twitter4j" % "twitter4j-core" % "2.2.6",
      
      // Dropbox plugin dependencies
      "org.apache.httpcomponents" % "httpclient" % "4.0.3",
      "com.googlecode.json-simple" % "json-simple" % "1.1.1",
      
      // Facebook plugin dependencies
      "ecs" % "ecs" % "1.4.2",
      "com.restfb" % "restfb" % "1.6.11",
      "org.json" % "json" % "20090211",
      
      // Moodle dependencies
      "org.jdom" % "jdom" % "2.0.2",
      
      // Mail dependencies
      "javax.mail" % "mail" % "1.4"      
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}
