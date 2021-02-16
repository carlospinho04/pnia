package com.carlos.pnia.rules

import cats.effect.Effect
import cats.implicits._
import com.carlos.pnia.domain.{BusinessSector, BusinessSectorError, PhoneNumber}
import org.http4s.Method._
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.implicits._

trait BusinessSectorRules[F[_]] {
  def get(phoneNumber: PhoneNumber): F[BusinessSector]
}

object BusinessSectorRules {
  def apply[F[_]](implicit ev: BusinessSectorRules[F]): BusinessSectorRules[F] = ev

  def impl[F[_]: Effect](c: Client[F]): BusinessSectorRules[F] = new BusinessSectorRules[F] {
    val dsl: Http4sClientDsl[F] = new Http4sClientDsl[F] {}

    import dsl._

    override def get(phoneNumber: PhoneNumber): F[BusinessSector] = {
      val url = uri"https://challenge-business-sector-api.meza.talkdeskstg.com/".withPath(s"sector/${phoneNumber.number}")
      c.expect[BusinessSector](GET(url)).adaptError { case error =>
        BusinessSectorError(error.getMessage)
      } // Prevent Client Json Decoding Failure Leaking
    }
  }
}


