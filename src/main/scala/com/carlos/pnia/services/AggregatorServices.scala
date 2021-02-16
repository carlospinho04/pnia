package com.carlos.pnia.services

import cats.effect.Effect
import cats.implicits._
import com.carlos.pnia.domain.{BusinessSector, PhoneNumber}
import com.carlos.pnia.rules.{BusinessSectorRules, PhoneNumberValidatorRules}

trait AggregatorServices[F[_]] {
  def aggregateSectorsByPrefix(phoneNumbers: Seq[PhoneNumber]): F[Map[String, Map[String, Int]]]
}

object AggregatorServices {
  implicit def apply[F[_]](implicit ev: AggregatorServices[F]): AggregatorServices[F] = ev

  def impl[F[_] : Effect](businessSector: BusinessSectorRules[F], phoneNumberValidatorRules: PhoneNumberValidatorRules[F]): AggregatorServices[F] = new AggregatorServices[F] {
    def aggregateSectorsByPrefix(phoneNumbers: Seq[PhoneNumber]): F[Map[String, Map[String, Int]]] = {
      val businessSectorAndPrefix = getBusinessSectorAndPrefix(phoneNumbers)
      groupByPrefixAndBusinessSector(businessSectorAndPrefix)
    }

    private def getBusinessSectorAndPrefix(phoneNumbers: Seq[PhoneNumber]): F[List[(BusinessSector, String)]] = {
      phoneNumbers.toList.traverse { phoneNumber =>
        for {
          businessSector <- businessSector.get(phoneNumber)
          prefixOpt <- phoneNumberValidatorRules.checkAndReturnValidPrefix(phoneNumber)
        } yield {
          prefixOpt.map(p => (businessSector, p))
        }
      }.map(_.flatten)
    }
  }

  private def groupByPrefixAndBusinessSector[F[_] : Effect](businessSectorAndPrefix: F[List[(BusinessSector, String)]]): F[Map[String, Map[String, Int]]] = {
    businessSectorAndPrefix.map { data =>
      data.groupBy { case (_, prefix) =>
        prefix
      }.map { case (prefix, listSectors) =>
        val businessSectorWithCount = listSectors.groupBy { case (sector, _) =>
          sector.sector
        }.map { case (sector, list) =>
          (sector, list.length)
        }
        (prefix, businessSectorWithCount)
      }
    }
  }
}




