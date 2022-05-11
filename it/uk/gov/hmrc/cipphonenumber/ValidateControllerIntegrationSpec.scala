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

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.{Json, OWrites}
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsJson, defaultAwaitTimeout, status}
import uk.gov.hmrc.cipphonenumber.controllers.ValidateController
import uk.gov.hmrc.cipphonenumber.models.PhoneNumber

class ValidateControllerIntegrationSpecSpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite {
  private val fakeRequest = FakeRequest()
  private lazy val controller = app.injector.instanceOf[ValidateController]
  private implicit val writes: OWrites[PhoneNumber] = Json.writes[PhoneNumber]

  "POST /" should {
    "return 200 with valid UK phone number" in {
      val result = controller.validatePhoneNumber()(
        fakeRequest.withBody(Json.toJson(PhoneNumber("07877823456"))))
      status(result) shouldBe Status.OK
    }
    "return 200 with UK phone number with country code" in {
      val result = controller.validatePhoneNumber()(
        fakeRequest.withBody(Json.toJson(PhoneNumber("+447877823456"))))
      status(result) shouldBe Status.OK
    }
    "return 400 with 3 digit emergency number" in {
      val result = controller.validatePhoneNumber()(
        fakeRequest.withBody(Json.toJson(PhoneNumber("999"))))

      status(result) shouldBe Status.BAD_REQUEST
      (contentAsJson(result) \ "message" ).as[String] shouldBe "Enter a valid telephone number"
    }

    "return 400 with random non-numeric characters" in {
      val result = controller.validatePhoneNumber()(
        fakeRequest.withBody(Json.toJson(PhoneNumber("sdjaksdj"))))

      status(result) shouldBe Status.BAD_REQUEST
      (contentAsJson(result) \ "message" ).as[String] shouldBe "Enter a valid telephone number"
    }

  }
}