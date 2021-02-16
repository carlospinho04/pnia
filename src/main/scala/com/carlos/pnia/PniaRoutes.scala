package com.carlos.pnia

import cats.effect.Sync
import cats.implicits._
import com.carlos.pnia.controllers.{Aggregator, HelloWorld}
import com.carlos.pnia.domain.PhoneNumber
import com.carlos.pnia.rules.PhoneNumberValidatorRules
import io.circe.generic.codec.ReprAsObjectCodec.deriveReprAsObjectCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object PniaRoutes {

  def aggregatorRoutes[F[_]: Sync](A: Aggregator[F], PNV: PhoneNumberValidatorRules[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "aggregate" / phoneNumber =>
        for {
          response <- A.aggregate(PhoneNumber(phoneNumber))
          resp <- Ok(response.toString)
        } yield resp

      case req @ POST -> Root / "aggregate" => req.decodeJson[Seq[PhoneNumber]].flatMap {
        phoneNumbers => Ok(phoneNumbers.toList.traverse(PNV.hasValidPrefix))
      }

    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }
}