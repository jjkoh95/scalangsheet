package controllers

import javax.inject._
import play.api._
import play.api.http.HttpEntity
import play.api.mvc._
import play.api.libs.json._
import akka.util.ByteString
import java.io.ByteArrayInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import akka.stream.scaladsl.StreamConverters
import scala.io.Source
// import org.json.JSONObject

@Singleton
class SheetController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
    def convertToJSON() = Action(parse.json) { implicit request =>
        val sheetURL = (request.body \ "sheetURL").as[String]
        val sheetResponse = scala.io.Source.fromURL(sheetURL).mkString.replace("\r", "")
        // spreadsheet csv is throwing me this
        val rows = sheetResponse.split("\n")

        // rows(0) - headers -> indicates language
        val headers = rows(0).split(',')
        var translations = collection.mutable.Map[String, collection.mutable.Map[String, String]]()
        for (i <- 1 until headers.length) {
            translations(headers(i)) = collection.mutable.Map[String, String]()
        }
        // rows(1:) - contents -> each row contents a message key and its respective translation
        for (r <- rows.slice(1, rows.length)) {
            val cols = r.split(',')
            val translationKey = cols(0)
            for (colIndex <- 1 until cols.length) {
                translations(headers(colIndex))(translationKey) = cols(colIndex)
            }
        }

        val baos = new ByteArrayOutputStream()
        val zos = new ZipOutputStream(baos)
        for ((k, v) <- translations) {
            // k is language
            // v is HashMap<string, string>
            val zipEntry = new ZipEntry(f"$k%s.json")
            zos.putNextEntry(zipEntry)

            // convert map to json
            // val json = new JSONObject(v) // Fuck you JSONObject
            val vMap = v.map(kv => (kv._1,kv._2)).toMap
            val vJSON = scala.util.parsing.json.JSONObject(vMap)

            // write json content
            zos.write(vJSON.toString().getBytes())
            zos.closeEntry()
        }

        zos.close() // closing ZipOutputStream


        Ok(baos.toByteArray()).withHeaders(
            "Content-Disposition"->"attachment; filename=translations.zip"
        )

    }
}