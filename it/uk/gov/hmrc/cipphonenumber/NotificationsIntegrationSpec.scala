/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.cipphonenumber

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import uk.gov.hmrc.cipphonenumber.utils.DataSteps

class NotificationsIntegrationSpec
  extends AnyWordSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with DataSteps {

  "/notifications" should {
    "respond with 200 status with valid notification id" in {
      //generate otp
      val verifyResponse = verify("07849123456").futureValue

      val notificationId = verifyResponse.json.\("notificationId")

      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/notifications/$notificationId")
          .get
          .futureValue

      response.status shouldBe 200
      response.json \ "code" shouldBe 102
      response.json \ "message" shouldBe "Message has been sent"
    }

    "respond with 404 status when notification id not found" in {
      val response =
        wsClient
          .url(s"$baseUrl/customer-insight-platform/phone-number/notifications/a283b760-f173-11ec-8ea0-0242ac120002")
          .get
          .futureValue

      response.status shouldBe 404
    }
  }
}
