package com.carlos.pnia

import cats.effect.Sync
import cats.implicits._
import com.carlos.pnia.controllers.{Aggregator, HelloWorld}
import com.carlos.pnia.domain.PhoneNumber
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object PniaRoutes {

  def aggregatorRoutes[F[_]: Sync](A: Aggregator[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "aggregate" / phoneNumber =>
        for {
          response <- A.aggregate(PhoneNumber(phoneNumber))
          resp <- Ok(response)
        } yield resp
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