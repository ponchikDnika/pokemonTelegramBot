package models

trait YesNoTag {
  def name: String
  def label: String = name
  def filter(level: YesNoTag): Boolean = true
}

object YesNoTag {
  case object ALL extends VariantTag {
    val name = "ALL"
  }
}

sealed trait YesNoType extends YesNoTag {

  override def filter(level: YesNoTag): Boolean = level == this

  def isBefore(variantType: YesNoType): Boolean = name.compareTo(variantType.name) <= 0

}

object YesNoType {

  case object Yes extends YesNoType {
    val name = "Repeat"
  }

  case object No extends YesNoType {
    val name = "Start again"
  }

  def typeFrom(name: String): models.YesNoType = {
    name match {
      case Yes.name => Yes
      case No.name => No
    }
  }

}