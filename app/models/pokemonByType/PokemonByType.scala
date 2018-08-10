package models.pokemonByType

case class DamageRelations (half_damage_from: Seq[Any],
                            no_damage_from: Seq[Generation],
                            half_damage_to: Seq[Generation],
                            double_damage_from: Seq[Generation],
                            no_damage_to: Seq[Generation],
                            double_damage_to: Seq[Any]
                           )

case class GameIndices (generation: Generation,
                        game_index: Int
                       )

case class Generation (url: String,
                       name: String
                      )

case class Names (name: String,
                  language: Generation
                 )

case class Pokemon (slot: Int,
                    pokemon: Generation
                   )

case class PokemonByType (name: String,
                          generation: Generation,
                          damage_relations: DamageRelations,
                          game_indices: Seq[GameIndices],
                          move_damage_class: Generation,
                          moves: Seq[Generation],
                          pokemon: Seq[Pokemon],
                          id: Int,
                          names: Seq[Names]
                         )

