package bots

import pme.bot.callback

import scala.language.postfixOps

trait PokemonBot {
  protected def tag(name: String): String = callback + name
}