package com.carlos.pnia

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    PniaServer.stream[IO].compile.drain.as(ExitCode.Success)
}
