package com.carlos.pnia.rules

import cats.effect.Effect
import cats.implicits._
import com.carlos.pnia.Logger
import com.carlos.pnia.config.ApplicationConf
import com.carlos.pnia.domain.{BusinessSector, BusinessSectorApiError, InvalidUri, PhoneNumber}
import org.http4s.Method._
import org.http4s.Uri
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl

trait BusinessSectorRules[F[_]] {
  def get(phoneNumber: PhoneNumber): F[BusinessSector]
}

object BusinessSectorRules extends Logger {
  def apply[F[_]](implicit ev: BusinessSectorRules[F]): BusinessSectorRules[F] = ev

  def impl[F[_]: Effect](client: Client[F], applicationConf: ApplicationConf): BusinessSectorRules[F] =
    new BusinessSectorRules[F] {
      val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F] {}

      import dsl._

      override def get(phoneNumber: PhoneNumber): F[BusinessSector] = {
        logger.info(s"Getting business sector for number: $phoneNumber")
        Uri
          .fromString(applicationConf.businessSectorApi.uri)
          .fold(
            error => Effect[F].raiseError(InvalidUri(error.getMessage())), { uri =>
              val url = uri.withPath(s"/sector/${phoneNumber.number}")
              client.expect[BusinessSector](GET(url)).adaptError {
                case error =>
                  logger.error(s"Error calling business sector api", error)
                  BusinessSectorApiError(error.getMessage)
              } // Prevent Client Json Decoding Failure Leaking
            }
          )
      }
    }
}
