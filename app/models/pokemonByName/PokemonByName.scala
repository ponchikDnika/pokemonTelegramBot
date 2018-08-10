package models.pokemonByName

case class Abilities (slot: Int,
                      is_hidden: Boolean,
                      ability: Forms
                     )

case class Forms (url: String,
                  name: String
                 )

case class GameIndices (version: Forms,
                        game_index: Int
                       )

case class Moves (version_group_details: Seq[VersionGroupDetails],
                  move: Forms
                 )

case class PokemonByName (forms: Seq[Forms],
                          abilities: Seq[Abilities],
                          stats: Seq[Stats],
                          name: String,
                          weight: Int,
                          moves: Seq[Moves],
                          sprites: Sprites,
                          held_items: Seq[Any],
                          location_area_encounters: String,
                          height: Int,
                          is_default: Boolean,
                          species: Forms,
                          id: Int,
                          order: Int,
                          game_indices: Seq[GameIndices],
                          base_experience: Int,
                          types: Seq[Types]
                         )

case class Sprites (back_female: String,
                    back_shiny_female: String,
                    back_default: String,
                    front_female: String,
                    front_shiny_female: String,
                    back_shiny: String,
                    front_default: String,
                    front_shiny: String
                   )

case class Stats (stat: Forms,
                  effort: Int,
                  base_stat: Int
                 )

case class Types (slot: Int,
                  _type: Forms
                 )

case class VersionGroupDetails (move_learn_method: Forms,
                                level_learned_at: Int,
                                version_group: Forms
                               )