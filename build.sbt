fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "me.topl",
      scalaVersion    := "2.13.9"
    )),
    name := "TrafficData",
    libraryDependencies ++= Seq(

      "org.json4s"        %% "json4s-native"             % "4.0.5",
      "org.scalatest"     %% "scalatest"                 % "3.2.12"         % Test
    )
  )
