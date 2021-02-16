package com.carlos.pnia.controllers

import cats.Applicative
import com.carlos.pnia.domain.PhoneNumber
import com.carlos.pnia.services.AggregatorServices

trait Aggregator[F[_]]{
  def aggregate(phoneNumbers: Seq[PhoneNumber]): F[Map[String, Map[String, Int]]]
}

object Aggregator {
  implicit def apply[F[_]](implicit ev: Aggregator[F]): Aggregator[F] = ev

  def impl[F[_]: Applicative](aggregatorServices: AggregatorServices[F]): Aggregator[F] = new Aggregator[F] {
    override def aggregate(phoneNumbers: Seq[PhoneNumber]): F[Map[String, Map[String, Int]]] = aggregatorServices.aggregateSectorsByPrefix(phoneNumbers)
  }
}


