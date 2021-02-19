package com.carlos.pnia

import cats.effect.{ConcurrentEffect, Timer}
import com.carlos.pnia.config.ApplicationConf
import com.carlos.pnia.controllers.{Aggregator, HttpErrorHandler}
import com.carlos.pnia.rules.{BusinessSectorRules, PhoneNumberValidatorRules}
import com.carlos.pnia.services.AggregatorServices
import fs2.Stream
import org.http4s.HttpRoutes
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger

import scala.concurrent.ExecutionContext.global

object PniaServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      appConfig = loadConfiguration

      routes: HttpRoutes[F] = PniaRoutes.aggregatorRoutes[F](initializeComponents(client, appConfig))
      routesWithErrorHandler = new HttpErrorHandler().handle(routes)
      httpApp = routesWithErrorHandler.orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(appConfig.server.httpPort, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain

  private def initializeComponents[F[_]: ConcurrentEffect](client: Client[F], appConfig: ApplicationConf): Aggregator[F] = {
    val phoneNumberValidatorRulesAlg = PhoneNumberValidatorRules.impl[F]
    val businessSectorRulesAlg = BusinessSectorRules.impl[F](client, appConfig)
    val aggregatorServicesAlg = AggregatorServices.impl[F](businessSectorRulesAlg, phoneNumberValidatorRulesAlg)
    Aggregator.impl[F](aggregatorServicesAlg)
  }

  private def loadConfiguration: ApplicationConf = {
    ApplicationConf.load() match {
      case Right(c) => c
      case Left(err) => throw new IllegalStateException(s"Could not load AppConfig: ${err.toList.mkString("\n")}")
    }
  }

}
