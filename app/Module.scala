import bots.PokemonServiceSubscription
import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import pme.bot.boundary.BotRunner
import pme.bot.control.{CommandDispatcher, LogStateSubscription}

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {

    // the generic CommandDispatcher
    bindActor[CommandDispatcher]("commandDispatcher")
    // starts the Bot itself (Boundary)
    bind(classOf[BotRunner]).asEagerSingleton()

    // your Services:
    // your Conversations:
    bind(classOf[PokemonServiceSubscription]).asEagerSingleton()
    // your RunAspects
    bind(classOf[LogStateSubscription]).asEagerSingleton()
  }
}