package com.carlos.pnia

import cats.effect.Sync
import cats.implicits._
import com.carlos.pnia.controllers.{Aggregator, HelloWorld}
import com.carlos.pnia.domain.PhoneNumber
import io.circe.generic.codec.ReprAsObjectCodec.deriveReprAsObjectCodec
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

object PniaRoutes {

  def aggregatorRoutes[F[_]: Sync](aggregator: Aggregator[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case req @ POST -> Root / "aggregate" => req.decodeJson[Seq[PhoneNumber]].flatMap {
        phoneNumbers => Ok(aggregator.aggregate(phoneNumbers))
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