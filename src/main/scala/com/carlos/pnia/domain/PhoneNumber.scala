package com.carlos.pnia.domain

import cats.Applicative
import cats.effect.Sync
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder, HCursor}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

final case class PhoneNumber(number: String) extends AnyVal

object PhoneNumber {
  implicit val phoneNumberDecoder: Decoder[PhoneNumber] = (cursor: HCursor) =>
    cursor.value.as[String].map(PhoneNumber(_))
  implicit def phoneNumberEntityDecoder[F[_]: Sync]: EntityDecoder[F, PhoneNumber] =
    jsonOf
  implicit val phoneNumberEncoder: Encoder[PhoneNumber] = deriveEncoder[PhoneNumber]
  implicit def phoneNumberEntityEncoder[F[_]: Applicative]: EntityEncoder[F, PhoneNumber] =
    jsonEncoderOf
}
