package com.formious.app

import java.nio.file.Paths

import org.rogach.scallop.ScallopConf

import scalaz.concurrent.Task
import org.http4s.{BuildInfo => _, _}
import org.http4s.server.staticcontent
import org.http4s.dsl._

import io.circe.generic.auto._
import io.circe.syntax.EncoderOps

import org.http4s.server.{Server, ServerApp}
import org.http4s.server.blaze._

import com.formious.services.{mturk, responses, experiments, util}
import com.formious.services.api

object Main extends ServerApp {
  case class ProjectInfo(organization: String,
                         name: String,
                         version: String,
                         scalaVersion: String,
                         libraryDependencies: Seq[String])

  case class RequestInfo(httpVersion: String,
                         method: String,
                         uri: String,
                         headers: Map[String, String],
                         remoteAddress: String)

  import org.slf4j.LoggerFactory
  private val logger = LoggerFactory.getLogger(getClass.getName)

  override def server(args: List[String]): Task[Server] = {
    object opts extends ScallopConf(args.toSeq) {
      appendDefaultToDescription = true
      banner("formious --port 1451 -v")

      val hostname = opt[String]("hostname", descr="hostname to listen on",
        default=Some(sys.env.getOrElse("HOSTNAME", "127.0.0.1")))
      val port = opt[Int]("port", 'p', descr="port to listen on",
        default=Some(sys.env.getOrElse("PORT", "80").toInt))
      val verbose = opt[Boolean]("verbose", 'v', descr="print extra output")
      val version = opt[Boolean]("version", descr="print version")
      // val help = opt[Boolean]("help", "print this help message")
      verify()
    }
    val hostname = opts.hostname()
    val port = opts.port()

    // TODO: set logging level depending on opts.verbose() or sthg like: process.env.DEBUG !== undefined

    logger.info(s"Starting http4s listening on http://$hostname:$port")

    val uiServiceConfig = staticcontent.FileService.Config(Paths.get("ui").toString)
    val uiService = staticcontent.fileService(uiServiceConfig)

    // rewrite all /admin/* requests to /ui/index.html
    val adminService = HttpService {
      case originalRequest @ _ =>
        val Request(method, _, httpVersion, headers, body, attributes) = originalRequest
        // we have to both reset the uri
        val rewrittenRequest = Request(method, Uri(path="/ui/index.html"), httpVersion, headers, body, attributes)
        // and pretend like it was routed through the root server
        // Reference: org.http4s.server.middleware.URITranslation
        val reroutedRequest = rewrittenRequest.withAttribute(Request.Keys.PathInfoCaret(3))
        uiService.run(reroutedRequest)
    }
    val faviconService = HttpService {
      case originalRequest @ _ -> Root / "favicon.ico" =>
        val Request(method, _, httpVersion, headers, body, attributes) = originalRequest
        val rewrittenRequest = Request(method, Uri(path="/ui/img/favicon.ico"), httpVersion, headers, body, attributes)
        uiService.run(rewrittenRequest)
    }

    val infoService = HttpService {
      case _ =>
        val payload = ProjectInfo(BuildInfo.organization, BuildInfo.name,
          BuildInfo.version, BuildInfo.scalaVersion, BuildInfo.libraryDependencies)
        Ok(payload.asJson.spaces2)
    }

    val echoService = HttpService {
      case request =>
        val httpVersion = request.httpVersion.toString
        val method = request.method.toString
        val remoteAddr = request.remoteAddr.getOrElse("").toString
        val headers = request.headers.map { header => header.name.toString -> header.value }.toMap
        val payload = RequestInfo(httpVersion, method, request.uri.toString, headers, remoteAddr)
        Ok(payload.asJson.spaces2)
    }

    BlazeBuilder
      .bindHttp(port, hostname)
      // static endpoints
      .mountService(uiService, "/ui")
      .mountService(adminService, "/admin")
      .mountService(faviconService, "/favicon.ico")
      // /api/*
      .mountService(api.AccessTokens.service, "/api/access_tokens")
      .mountService(api.Administrators.service, "/api/administrators")
      .mountService(api.AWSAccounts.service, "/api/aws_accounts")
      .mountService(api.Experiments.service, "/api/experiments")
      .mountService(api.ExperimentBlocks.service, "/api/experiments!")
      .mountService(api.MTurk.service, "/api/mturk")
      .mountService(api.Responses.service, "/api/responses")
      .mountService(api.Templates.service, "/api/templates")
      // /misc
      .mountService(infoService, "/info")
      .mountService(echoService, "/echo")
      .mountService(experiments.service, "/experiments")
      .mountService(mturk.service, "/mturk")
      .mountService(responses.service, "/responses")
      .mountService(util.service, "/util")
      // TODO: add global exception handler
      //case otherwise =>
      //  Console.err.println(s"No matching route for $otherwise")
      .start
  }
}