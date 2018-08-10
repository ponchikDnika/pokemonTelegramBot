package bots

import akka.actor.{ActorRef, ActorSystem, Props}
import info.mukel.telegrambot4s.models.{InlineKeyboardButton, InlineKeyboardMarkup}
import javax.inject.{Inject, Named, Singleton}
import models.VariantType._
import models.YesNoType._
import models.pokemonByName.PokemonByName
import models.pokemonByType.PokemonByType
import models.{VariantType, _}
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse
import play.api.libs.ws._
import pme.bot.control.ChatConversation
import pme.bot.entity.SubscrType.SubscrConversation
import pme.bot.entity.{Command, FSMData, FSMState, Subscription}

import scala.concurrent.{ExecutionContext, Future}


class PokemonConversation @Inject()(ws: WSClient)(implicit ec: ExecutionContext)
  extends ChatConversation with PokemonBot {

  private implicit val formats: DefaultFormats.type = DefaultFormats
  private val url = "https://pokeapi.co/api/v2/"

  when(Idle) {
    case Event(Command(msg, _), _) =>
      bot.sendMessage(msg, "Press how to get info!", Some(choiceTypeSelector))
      goto(Choice)
    case other => notExpectedData(other)
  }

  when(Choice) {
    case Event(Command(msg, callbackData: Option[String]), _) =>
      callbackData match {
        case Some(data) =>
          VariantType.typeFrom(data) match {
            case Name =>
              bot.sendMessage(msg, "Tell me pokemon name")
              goto(ByName)
            case Type =>
              bot.sendMessage(msg, "Tell me pokemon type")
              goto(ByType)
            case _ =>
              bot.sendMessage(msg, "You have to tell me name")
              stay()
          }
        case None =>
          bot.sendMessage(msg, "First you have to make a choice", Some(choiceTypeSelector))
          stay()
      }
    case other => notExpectedData(other)
  }

  when(ByName) {
    case Event(Command(msg, _), _) =>
      msg.text match {
        case Some(name) if name.length > 0 =>
          getDataByName(name.toLowerCase).map(result => bot.sendMessage(msg, result.toString.substring(0, 2048)))
          bot.sendMessage(msg, "Repeat?", Some(YesNoTypeSelector))
          goto(Repeat) using ChoiceTypeData(ByName)
        case None =>
          bot.sendMessage(msg, "You have to tell me name")
          stay()
      }
    case other => notExpectedData(other)
  }

  when(ByType) {
    case Event(Command(msg, _), _) =>
      msg.text match {
        case Some(_type) if _type.length > 0 =>
          getDataByType(_type.toLowerCase).map { result =>
            bot.sendMessage(msg, result.toString.substring(0, 2048))
            bot.sendMessage(msg, "Repeat or start again?", Some(YesNoTypeSelector))
          }
          goto(Repeat) using ChoiceTypeData(ByType)
        case _ =>
          bot.sendMessage(msg, "You have to tell me type")
          stay()
      }
    case other => notExpectedData(other)
  }

  when(Repeat) {
    case Event(Command(msg, callbackData: Option[String]), choiceTypeData: ChoiceTypeData) =>
      callbackData match {
        case Some(data) =>
          YesNoType.typeFrom(data) match {
            case Yes =>
              bot.sendMessage(msg, "Your choice is Repeat")
              choiceTypeData.state match {
                case ByName => goto(ByName)
                case ByType => goto(ByType)
              }
            case No =>
              bot.sendMessage(msg, "Your choice is Start again", Some(choiceTypeSelector))
              goto(Choice)
            case _ =>
              bot.sendMessage(msg, "You have to tell me name")
              stay()
          }
        case None =>
          bot.sendMessage(msg, "First you have to make a choice", Some(YesNoTypeSelector))
          stay()
      }
    case other => notExpectedData(other)
  }


  private def getDataByName(pokemonName: String): Future[PokemonByName] = {
    ws.url(s"${url}pokemon/$pokemonName/").get().map {
      response =>
        val json = parse(response.json.toString())
        val result = json.extract[PokemonByName]
        result
    }
  }

  private def getDataByType(pokemonType: String): Future[PokemonByType] = {
    ws.url(s"${url}type/$pokemonType/").get().map {
      response =>
        val json = parse(response.json.toString())
        val result = json.extract[PokemonByType]
        result
    }
  }

  private lazy val choiceTypeSelector = {
    InlineKeyboardMarkup(Seq(
      Seq(
        InlineKeyboardButton.callbackData(Name.label, tag(Name.name)),
        InlineKeyboardButton.callbackData(Type.label, tag(Type.name))
      )
    ))
  }

  private lazy val YesNoTypeSelector = {
    InlineKeyboardMarkup(Seq(
      Seq(
        InlineKeyboardButton.callbackData(Yes.label, tag(Yes.name)),
        InlineKeyboardButton.callbackData(No.label, tag(No.name))
      )
    ))
  }

  case object Choice extends FSMState

  case object ByName extends FSMState

  case object ByType extends FSMState

  case object Repeat extends FSMState

  case class ChoiceTypeData(state: FSMState) extends FSMData

}

object PokemonConversation {
  val command = "/start"
  def props(ws: WSClient)(implicit ec: ExecutionContext): Props = Props(new PokemonConversation(ws))
}

@Singleton
class PokemonServiceSubscription @Inject()(@Named("commandDispatcher")
                                           val commandDispatcher: ActorRef,
                                           val ws: WSClient,
                                           val system: ActorSystem)
                                          (implicit ec: ExecutionContext) {

  import PokemonConversation._

  commandDispatcher ! Subscription(command, SubscrConversation,
    Some(_ => system.actorOf(props(ws))))
}