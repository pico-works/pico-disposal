resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.codacy"         % "sbt-codacy-coverage" % "1.3.0")
addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver"  % "0.9.0")
addSbtPlugin("io.get-coursier"    % "sbt-coursier"        % "1.0.0-M13")
addSbtPlugin("org.scoverage"      % "sbt-scoverage"       % "1.4.0")
addSbtPlugin("org.tpolecat"       % "tut-plugin"          % "0.4.6")
