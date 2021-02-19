package com.carlos.pnia.controllers

import cats.data.{Kleisli, OptionT}
import cats.effect.Sync
import cats.implicits._
import com.carlos.pnia.domain
import com.carlos.pnia.domain.{BusinessSectorApiError, InvalidUri}
import io.circe.syntax._
import org.http4s.circe.{CirceEntityDecoder, CirceEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Request, Response}

class HttpErrorHandler[F[_]: Sync, E <: Exception]
    extends Http4sDsl[F]
    with CirceEntityEncoder
    with CirceEntityDecoder {
  private val handler: domain.Error => F[Response[F]] = {
    case BusinessSectorApiError(error) => BadRequest(error.asJson)
    case InvalidUri(error) => InternalServerError(error.asJson)
  }

  def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    Kleisli { req: Request[F] =>
      OptionT {
        routes.run(req).value.handleErrorWith {
          case e: domain.Error =>
            handler(e).map(Option(_))
          case _ => InternalServerError("Unexpected error occurred").map(Option(_))
        }
      }
    }
}
