package com.carlos.pnia.services

import cats.effect.IO
import com.carlos.pnia.domain.{BusinessSector, PhoneNumber}
import com.carlos.pnia.rules.{BusinessSectorRules, PhoneNumberValidatorRules}
import munit.CatsEffectSuite
import cats.implicits._

class AggregatorServicesSpec extends CatsEffectSuite {
  private val businessSectorRules: BusinessSectorRules[IO] = (phoneNumber: PhoneNumber) => BusinessSector(phoneNumber.number, "Clothing").pure[IO]
  private val phoneNumberValidatorRules: PhoneNumberValidatorRules[IO] = (_: PhoneNumber) => Option("6").pure[IO]
  private val aggregatorServices = AggregatorServices.impl[IO](businessSectorRules, phoneNumberValidatorRules)

  test("Aggregate phoneNumbers properly") {
    val test = aggregatorServices.aggregateSectorsByPrefix(Seq(PhoneNumber("+6351"), PhoneNumber("+6351")))
    val expectedResult = Map("6" -> Map("Clothing" -> 2))
    assertIO(test, expectedResult)
  }

}