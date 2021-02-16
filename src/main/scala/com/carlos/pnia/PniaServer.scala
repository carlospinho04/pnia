package com.carlos.pnia

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import com.carlos.pnia.controllers.{Aggregator, HelloWorld, HttpErrorHandler}
import com.carlos.pnia.rules.{BusinessSectorRules, PhoneNumberValidatorRules}
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object PniaServer {

  def stream[F[_] : ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      helloWorldAlg = HelloWorld.impl[F]
      phoneNumberValidatorRulesAlg = PhoneNumberValidatorRules.impl[F]
      businessSectorRulesAlg = BusinessSectorRules.impl[F](client)
      aggregatorAlg = Aggregator.impl[F](businessSectorRulesAlg)


      routes: HttpRoutes[F] = PniaRoutes.helloWorldRoutes[F](helloWorldAlg) <+> PniaRoutes.aggregatorRoutes[F](aggregatorAlg, phoneNumberValidatorRulesAlg)
      routesWithErrorHandler = new HttpErrorHandler().handle(routes)
      httpApp = routesWithErrorHandler.orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
