package models

trait VariantTag {
  def name: String
  def label: String = name
  def filter(level: VariantTag): Boolean = true
}

object VariantTag {
  case object ALL extends VariantTag {
    val name = "ALL"
  }
}

sealed trait VariantType extends VariantTag {

  override def filter(level: VariantTag): Boolean = level == this

  def isBefore(variantType: VariantType): Boolean = name.compareTo(variantType.name) <= 0

}

object VariantType {

  case object Name extends VariantType {
    val name = "Get by name"
  }

  case object Type extends VariantType {
    val name = "Get by type"
  }

  def typeFrom(name: String): models.VariantType = {
    name match {
      case Type.name => Type
      case Name.name => Name
    }
  }

}
