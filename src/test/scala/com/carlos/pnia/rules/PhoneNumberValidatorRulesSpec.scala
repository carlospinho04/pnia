package com.carlos.pnia.rules

import cats.effect.IO
import com.carlos.pnia.domain.PhoneNumber
import munit.CatsEffectSuite

class PhoneNumberValidatorRulesSpec extends CatsEffectSuite {

  test("Get valid prefix") {
    val phoneNumberValidatorRules = PhoneNumberValidatorRules.impl[IO]
    val test = phoneNumberValidatorRules.checkAndReturnValidPrefix(PhoneNumber("6"))
    assertIO(test, Option("6"))
  }

  test("Get None when no valid prefix is passed") {
    val phoneNumberValidatorRules = PhoneNumberValidatorRules.impl[IO]
    val test = phoneNumberValidatorRules.checkAndReturnValidPrefix(PhoneNumber("7"))
    assertIO(test, None)
  }

  test("Get clean number and return valid prefix when number starts with +") {
    val phoneNumberValidatorRules = PhoneNumberValidatorRules.impl[IO]
    val test = phoneNumberValidatorRules.checkAndReturnValidPrefix(PhoneNumber("+61234"))
    assertIO(test, Option("6"))
  }

  test("Get clean number and return valid prefix when number starts with 00") {
    val phoneNumberValidatorRules = PhoneNumberValidatorRules.impl[IO]
    val test = phoneNumberValidatorRules.checkAndReturnValidPrefix(PhoneNumber("0033510096789"))
    assertIO(test, Option("3351009"))
  }
 

}