package com.carlos.pnia.domain

import cats.Applicative
import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

final case class BusinessSector(number: String, sector: String)

object BusinessSector {
  implicit val businessSectorDecoder: Decoder[BusinessSector] = deriveDecoder[BusinessSector]
  implicit def businessSectorEntityDecoder[F[_]: Sync]: EntityDecoder[F, BusinessSector] =
    jsonOf
  implicit val businessSectorEncoder: Encoder[BusinessSector] = deriveEncoder[BusinessSector]
  implicit def businessSectorEntityEncoder[F[_]: Applicative]: EntityEncoder[F, BusinessSector] =
    jsonEncoderOf
}
