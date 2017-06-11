package ru.tochkak.plugin.salat

import org.specs2.mutable.Specification
import play.api._
import data.validation.ValidationError
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import java.io.File
import play.api.libs.json._
import play.api.libs.json.Json._
import org.bson.types.ObjectId
import Formats.objectId
import Binders._
import play.api.Play.current

object ObjectIdSpec extends Specification {

  "ObjectId" should {
    "bind and unbind to Form" in {
      import play.api.data._
      import play.api.data.Forms._
      val id = new ObjectId()
      val form = Form("id" -> objectId)
      val data = Map("id" -> id.toString)
      form.bind(data).get must equalTo(id)
    }

    "bind and unbind to QueryString" in {
      val id = new ObjectId()
      val data = Map("id" -> Seq(id.toString))
      objectIdQueryStringBindable.bind("id", data).get.right.get must equalTo(id)
      objectIdQueryStringBindable.unbind("id", id) must equalTo("id=%s".format(id.toString))
    }

    "bind and unbind to Path" in {
      val id = new ObjectId()
      objectIdPathBindable.bind("id", id.toString).right.get must equalTo(id)
      objectIdPathBindable.unbind("id", id) must equalTo(id.toString)
    }

    "bind to JavascriptLiteral" in {
      val id = new ObjectId()
      objectIdJavascriptLiteral.to(id) must equalTo(id.toString)
    }

    "write to json and read back into ObjectId" in {
      val id = new ObjectId()
      Json.toJson[ObjectId](id).validate[ObjectId] must beEqualTo(JsSuccess(id))
    }

    "invalidate if faulty not JSString" in {
      JsNumber(1).validate[ObjectId] must equalTo(JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.jsstring")))))
    }

    "invalidate if faulty not ObjectId" in {
      JsString("not a object id").validate[ObjectId] must equalTo(JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.objectid")))))
    }
  }
  
}
