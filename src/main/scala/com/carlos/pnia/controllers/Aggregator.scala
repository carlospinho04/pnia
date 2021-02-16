package com.carlos.pnia.controllers

import cats.Applicative
import com.carlos.pnia.domain.{BusinessSector, PhoneNumber}
import com.carlos.pnia.rules.BusinessSectorRules

trait Aggregator[F[_]]{
  def aggregate(phoneNumber: PhoneNumber): F[BusinessSector]
}

object Aggregator {
  implicit def apply[F[_]](implicit ev: Aggregator[F]): Aggregator[F] = ev

  def impl[F[_]: Applicative](businessSector: BusinessSectorRules[F]): Aggregator[F] = new Aggregator[F] {
    override def aggregate(phoneNumber: PhoneNumber): F[BusinessSector] = businessSector.get(phoneNumber)
  }
}


