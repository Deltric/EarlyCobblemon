# Changelog
## [1.4.1](#1-4-1)

### Changes
- Slightly lowered the volume of all cries
- Updated Pokeball animations and model.
- Turtwig can now be put on shoulder.
- Shuckles can now be milked for Berry Juice using a wooden bowl
- Updated Zubat line model, texture, and animations.
- Updated Geodude line models and textures.
- Added animations for Hitmontop, Tyrogue, and Mightyena.
- Tweaked animations for Dusknoir, Ratatta, Bewear, Exeggutor, and Alolan Exeggutor.
- Sized Kantonian Exeggutor down. Still big but not TOO BIG.
- Tweaked cries for Pikachu, Raichu and Alolan Raichu.
- Pasture blocks will now also connect their bottom left and right sides to walls, iron bars, glass panes and any other modded block that follows the same connection rules.
- The config option `consumeHeldItems` has been removed, please see the Datapack & Resourcepack Creators for instructions on the updated method.
- Using Potions, Status Heals, Ethers, and Antidotes will now return a glass bottle
- Using a Remedy, Fine Remedy, or Superb Remedy will no longer lower friendship with a Pokémon
- The Healing Machine now has a more difficult recipe, placing it later game.
- Heal Powder can now be composted with a 75% chance of adding a layer
- Mental, Power, White, and Mirror Herbs can now be composted with a 100% chance of adding a layer.
- Added emissive to Hoothoot and Noctowl.
- Mining Evolution Stone Ores with a Fortune pickaxe will now increase the amount of items recieved
- Black Augurite can now be used to craft stone axes and obsidian.
- Using Experience Candies brings up the Party Pokémon Select screen when not targeting a Pokémon.
- Added tab completion for statuses to commands

### Additions
- Added battle spectating, can disable in config
- Cobblemon now has compatibility with [Adorn](https://modrinth.com/mod/adorn) allowing you to craft Apricorn wood furniture.
- Berries can now be used in recipes from [Farmer's Delight](https://modrinth.com/mod/farmers-delight) and [Farmer's Delight (Fabric)](https://modrinth.com/mod/farmers-delight-fabric), as well as any other mods using the same berry tags.
- The nature of cobblemon will now be displayed italicized when a mint has been applied. Hovering over the nature will display the mint that was applied. This is the intended behaviour, because the original nature and taste of the Cobblemon does not change when a mint is applied.
- Giving Pokémon items now plays a sound
- A boat, sign and hanging sign is now craftable with Apricorn wood. The recipes are the same shape as Minecraft's equivalent with Apricorn planks as replacements.
- Added the Cleanse Tag, Flame Orb, Life Orb, Smoke Ball, and Toxic Orb held items.
- Added Fairy Feather drops to some Pokemon.
- Added the Inferno, Void, and Forsaken patterns for Vivillon. These can be obtained by evolving a Spewpa in the Nether, End, or Deep Dark respectively.
- Added the Litwick and Drifloon lines.
- Bees can now be fed using Pep-Up Flowers.
- Mooshtank can now be milked with a bowl for Mushroom Stew.
- Updated Showdown version to use gen9 battle data
- Added cries to Beldum, Metang and Metagross.

### Fixes
- Fixed various stone related blocks not being valid for Big Roots to spread on the Fabric version.
- Updated the registration of compostable items to improve compatibility with Fabric forks such as Quilt. Please note this does not mean we officially support Quilt, this change was only done since it was possible by correcting the registration to use the new intended way in the Fabric API.
- Fixed Dispensers being unable to shear grown Apricorns.
- Fixed Bowl not being given back to player after using Berry Juice
- Added context for -fail and -block handlers for battle text and added 16 related battle texts for them
- Fixed Battle text for Disable, Laser Focus, Foresight, Fire Spin, Telekinesis, Curse, Recharge and Encore
- Fixed missing text for snowy weather in battles
- Fixed missing text for attempting to catch an uncatchable Pokémon
- Fixed Moonphases for Clefairy line
- Fixed issue where Potions, Super Potions, and Hyper Potions did not work during battle
- Fixed the compatibility patch with the Forge version of [Carry On](https://modrinth.com/mod/carry-on) due to a bug on the mod, the Fabric version was unchanged and is still compatible.
- Added the ability to place Berries on modded Farmland blocks.
- Shouldered Pokémon now hop off when selected in team and r is pressed. This also is in effect in battles leading to shouldered Pokémon jumping of the shoulder of the trainer when it is their turn.
- Made more items compostable and changed the process for making items compostable.
- Added the ability for Hoppers to fill Brewing Stands with Medicinal Brews and Potions.
- Apricorn blocks are now flammable.
- The default pose for Pokémon being passengers is now "standing".
- Fixed issue where some IVs were changing every player relog.
- Fixed cursed battle message
- Fixed mistakes in pokemon spawning JSONs
- Fixed advancement crash from bad datapack evolution data.
- Fixed global influences being applied to TickingSpawners twice.
- Reverted the default SpawningSelector back to FlatContextWeightedSelector. This fixes multiple weight related issues, including weights with SpawningInfluences.
- Apricorn Planting advancement should work again.
- Advancement "Patterned Wings" should now allow High Plains and Icy Snow Vivillon to register.
- Fixed the last battle critical hits evolution requirement not working.
- Fixed the damage taken evolution requirement not saving progress.
- Fixed the defeated Pokémon evolution requirement not saving progress.
- Fixed potion brewing recipes not showing up JEI and similar mods on the Forge version.
- Fixed an exploit that could convert a single piece of Blaze Powder into an extra Medicinal Brew on the Forge version.
- Fixed an issue where health percentages would show incorrectly after healing
- Fixed the move Revival Blessing not allowing you to select from fainted party members.
- Fixed villagers not being able to pick up and plant mint seeds, vivichoke seeds, and revival herbs. 
- Fixed Exeggcute faint.
- Regenerated all pokemon spawn JSONs and fixed missing biome tags, wrong weights, level ranges and biome specific drop tables.
- Fixed Raticate, Onix, Unfezant, Bergmite, Avalugg, Boltund and Revavroom cries not playing.
- Fixed Alolan Ratticate animations causing a crash.
- Fixed Quaxwell not doing its cry.
- Fixed Shroomish not using its idle.
- Fixed a possible visual duplication of sent out Pokémon.
- Fixed battle text for Trace, Receiver, and Power of Alchemy.
- Fixed tooltips being appended too late in items.

### Developer
- Fixed the `SpawnEvent` not respecting usage of `Cancelable#cancel`.
- Added the `EvolutionTestedEvent`, this allows listening and overriding the final result of evolution requirement tests.
- Added utility script that can be used to generate all Spawn JSONS for all pokemon from the spawning spreadsheet in 1 click ([cobblemon_spawn_csv_to_json.py](utilityscripts%2Fcobblemon_spawn_csv_to_json.py)).
- The `HeldItemManager` has a new method `shouldConsumeItem`, this will return false by default to prevent breaking changes, see the documentation and update your implementations as needed.
- Added and implemented minSkyLight and maxSkyLight as config options for SpawnConditions

### Datapack & Resourcepack Creators
- Added 3 new item tags: `cobblemon:held/consumed_in_npc_battle`, `cobblemon:held/consumed_in_pvp_battle` & `cobblemon:held/consumed_in_wild_battle` these will determine which items get consumed in the implied battle types by Cobblemon, keep in mind the controller for this behaviour can be overriden by 3rd party.


## [1.4.0 - The Friends and Farms Update (October 13th, 2023)](#1-4-0)
### Additions
- Added pasture blocks, used to let your PC Pokémon roam around an area.
- Added nicknaming from the summary menu of a Pokémon (click their name).
- Added trading between players. Press R while looking at another player and you'll figure the rest out.
- Added mints for changing Pokémon stats. These are most commonly found at high altitudes.
- Added [Revival Herbs](https://wiki.cobblemon.com/index.php/Revival_Herb), with pep-up flowers when fully grown, growing in lush caves.
- Added [Medicinal Leeks](https://wiki.cobblemon.com/index.php/Medicinal_Leek), growing on the surface of rivers and ponds. It is a potion ingredient and can be cooked as food!.
- Added [Big Roots](https://wiki.cobblemon.com/index.php/Big_Root), generating from cave ceiling dirt which sometimes spread as energy roots.
- Added [69 Berry Trees](https://wiki.cobblemon.com/index.php/Berry_Tree) and [Berries](https://wiki.cobblemon.com/index.php/Berry). Some are found in village farms, some from planting different berries close to each other.
- Added mulches: Mulch Base, Growth Mulch, Surprise Mulch, Coarse Mulch, Humid Mulch, Rich Mulch, Loamy Mulch, Peat Mulch, and Sandy Mulch
- Added [Vivichokes](https://wiki.cobblemon.com/index.php/Vivichoke), obtainable from wandering villager trades and some loot chests.
- Added medicine brewing using medicinal leeks and berries in brewing stands.
- Added Pokémon cries when in battles and being sent out.
- Added medicinal items: Berry Juice, Heal Powder, Remedy, Fine Remedy, Superb Remedy, Revive, Max Revive, Potion, Super Potion, Hyper Potion, Max Potion, Full Restore, Full Heal, Antidote, Awakening, Burn Heal, Ice Heal, and Paralyze Heal.
- Added battle items: X Attack, X Defence, X Sp.Atk, X Sp.Def, X Speed, Dire Hit, and Guard Spec.
- Added EV items: Power Anklet, Power Band, Power Belt, Power Bracelet, Power Lens, Power Weight.
- Added food items: Roasted Leek, Leek and Potato Stew, Braised Vivichoke, and Vivichoke Dip
- Added evolution items: Auspicious Armor and Malicious Armor, which can be used to evolve Charcadet into Armarouge or Ceruledge respectively.
- Added (mostly brewing) recipes for HP Up, Protein, Iron, Calcium, Zinc, Carbos, PP Up, PP Max, and Medicinal Leek to Magenta Dye.
- Added held items: Bright Powder, Destiny Knot
- Added AI for Nosepass to point towards world spawn when idle. We just think it's neat.
- Added shoulder mounting for Mimikyu.
- Added flying placeholder animations to Pidgey, Pidgeotto, Pidgeot, Golbat, Crobat, Scyther, Scizor, Zapdos, Moltres, Articuno, Dragonite, Rowlet, Dartrix, and Decidueye.
- Added loot to various vanilla chest loot tables (Link Cable in Ancient Cities, Woodland Mansions, End Cities, and Igloos, Vivichoke Seeds in Jungle Temples, Dungeons, and Plains, Savanna, Snowy, and Taiga Villages, and all 7 Apricorn Sprouts in Desert, Plains, Savanna, Snowy, and Taiga Villages, as well as the Bonus Chest, which can also have 5 of the basic Poké Ball types)
- Added a `doShinyStarters` gamerule to make it quick and easy to be offered shiny starters.
- Added a `doPokemonLoot` gamerule to toggle Pokémon dropping items/exp on death.
- Added ability activation announcement when in battle.
- Added animations for Wailord and made it BIGGER.
- Added Cherry Torterra variant.
- Added 2 new face spots for Spinda. The number of unique Spindas increases...
- Added Forretress Shulker variant.
- Added the `/teststore <player> <store> <properties>` command allowing command block/mcfunction users to query a party, PC or both for Pokémon matching specific properties and returning the match count, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.teststore` if a permission mod is present.
- Added the `/querylearnset <player> <slot> <move>` command allowing command block/mcfunction users to query a party slot and check if the Pokémon can learn a specific move returning a 1 if yes otherwise 0, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.querylearnset` if a permission mod is present.
- Added the `/testpcslot <player> <slot> <properties>` command allowing command block/mcfunction users to query a pc slot and check if the Pokémon matches specific properties returning a 1 if yes otherwise 0, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.testpcslot` if a permission mod is present.
- Added the `/testpartyslot <player> <slot> <properties>` command allowing command block/mcfunction users to query a party slot and check if the Pokémon matches a specific properties returning a 1 if yes otherwise 0, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.testpartyslot` if a permission mod is present.
- Added the `/clearparty <player>` command for emptying a player's party.
- Added the `/clearpc <player>` command for emptying a player's PC.
- Added the `/pokemonrestart <reset_starters>` and the `/pokemonrestartother <player> <reset_starters>` command allowing command block/mcfunction users to reset a players Pokémon data.

### Pokémon Added
#### Gen 2

- Chikorita
- Bayleef
- Meganium
- Totodile
- Croconaw
- Feraligatr
- Cyndaquil
- Quilava
- Typhlosion
- Spinarak
- Ariados
- Shuckle
- Chinchou
- Lanturn
- Aipom
- Gligar
- Hoothoot
- Noctowl
- Mareep
- Flaaffy
- Ampharos
- Sudowoodo
- Snubbull
- Granbull
- Phanpy
- Donphan
- Teddiursa
- Ursaring

#### Gen 3

- Taillow
- Swellow
- Relicanth
- Duskull
- Dusclops
- Shroomish
- Breloom
- Cacnea
- Cacturne
- Poochyena
- Mightyena
- Wingull
- Pelipper
- Numel
- Camerupt
- Clamperl
- Huntail
- Gorebyss
- Surskit
- Masquerain
- Chimecho
- Barboach
- Whiscash
- Volbeat
- Illumise
- Zigzagoon
- Linoone
- Ralts
- Kirlia
- Gardevoir
- Nincada
- Ninjask
- Shedinja
- Beldum
- Metang
- Metagross

#### Gen 4

- Carnivine
- Shinx
- Luxio
- Luxray
- Ambipom
- Gliscor
- Dusknoir
- Chingling
- Bonsly
- Chatot
- Combee
- Vespiquen
- Buizel
- Floatzel
- Starly
- Staravia
- Staraptor
- Gallade

#### Gen 5

- Bouffalant
- Roggenrola
- Boldore
- Gigalith
- Venipede
- Whirlipede
- Scolipede
- Yamask
- Cofagrigus
- Patrat
- Watchog
- Lillipup
- Herdier
- Stoutland
- Cottonee
- Whimsicott
- Pidove
- Tranquill
- Unfezant
- Timburr
- Gurdurr
- Conkeldurr

#### Gen 6

- Scatterbug
- Spewpa
- Vivillon
- Skrelp
- Dragalge
- Bunnelby
- Diggersby
- Phantump
- Trevenant
- Fletchling
- Fletchinder
- Talonflame

#### Gen 7

- Wishiwashi
- Cutiefly
- Ribombee
- Stufful
- Bewear
- Comfey
- Alolan Exeggutor
- Alolan Raichu
- Alolan Meowth
- Alolan Persian
- Komala
- Wimpod
- Golisopod
- Crabrawler
- Crabominable
- Mudbray
- Mudsdale

#### Gen 8

- Arrokuda
- Barraskewda
- Nickit
- Thievul
- Falinks
- Galarian Farfetch'd
- Sirfetch'd
- Rookidee
- Corvisquire
- Corviknight
- Galarian Ponyta
- Galarian Rapidash
- Yamper
- Boltund
- Galarian Zigzagoon
- Galarian Linoone
- Obstagoon
- Galarian Meowth
- Perrserker
- Ursaluna

#### Gen 9

- Sprigatito
- Floragato
- Meowscarada
- Fuecoco
- Crocalor
- Skeledirge
- Quaxly
- Quaxwell
- Quaquaval
- Flittle
- Espathra
- Garganacl
- Fidough
- Dachsbun
- Armarouge
- Ceruledge
- Cetoddle
- Cetitan
- Shroodle
- Grafaiai
- Tandemaus
- Maushold
- Varoom
- Revavroom
- Squawkabilly
- Glimmet
- Glimmora
- Annihilape
- Tinkatink
- Tinkatuff
- Tinkaton
- Maschiff
- Mabosstiff
- Lechonk
- Oinkologne
- Paldean Wooper
- Clodsire

### Changes
- Removed the existing shoulder effects from Pokémon until we have more balanced versions of them (they're too powerful!)
- Updated models and textures of Weedle, Dwebble and Crustle, Spiritomb, Koffing and Weezing, Kadabra and Alakazam, Emolga, Oshawott, Doduo and Dodrio, Dratini and Dragonair and Dragonite, Sneasel and Weavile, Gyarados, Hitmonlee and Hitmonchan, Chesnaught, Spinda, Mamoswine, Steelix, Misdreavus and Mismagius, Buneary and Lopunny, Golduck, Meowth and Persian, Fennekin and Braixen and Delphox, Snivy and Servine and Serperior, Ratatta and Raticate, Nidorina and Nidoqueen, Nidoran Male and Nidoking, Riolu and Lucario, Haunter and Gengar, Mankey and Primeape, Mew and Mewtwo, Arcanine, Magnemite and Magneton and Magnezone, Exeggcute and Exeggutor, Elekid and Electabuzz and Electivire, Pichu and Pikachu and Raichu, Wooper, Drowzee and Hypno, Aerodactyl, Spearow and Fearow, Lickitung and Lickilicky, Pidgey and Pidgeotto and Pidgeot, Scyther and Scizor and Kleavor, Popplio and Brionne and Primarina, Torchic and Combusken and Blaziken, Happiny and Chansey and Blissey.
- Updated animations for Steelix, Turtwig and Grotle and Torterra, Ponyta and Rapidash, Piplup and Prinplup and Empoleon, Drowzee and Hypno, Farfetch'd, Exeggcute and Exeggutor, Bidoof, Chimecho, Lickitung and Lickilicky, Popplio and Brionne, Luvdisc, Chimchar and Monferno and Infernape, Sobble and Drizzile and Inteleon, Greninja, Heatmor, Aerodactyl, Ditto, Lotad and Lombre and Ludicolo, Pumpkaboo and Gourgeist.
- Updated sprites for EV medicines, the rare candy, and the apricorn door item.
- Updated textures for apricorn doors and all the evolution stone ores.
- Ponyta and Rapidash now have animated textures; they look insane.
- Updated Apricorn Leaves color.
- Wild Pokémon now heal if you are defeated by them or flee from them.
- Doubled the default time between ambient Pokémon cries (they have cries if you're using a resource pack to add them)
- Moved spawn attempts per tick to a config option (ticksBetweenSpawnAttempts)
- PCs can now be waterlogged
- Starter selection prompt now appears as a tutorial-esque toast instead of plain text
- Reorganised the advancements recipes folder
- Pokéedit command now supports IVs and EVs.
- Reorganised creative categories
- Pokémon can now wander into non-solid blocks such as foliage
- Thrown Poké Balls now despawn after 30 seconds so that they don't fly forever.
- Dive Balls will now have the same motion speed underwater as if they were thrown in the air.
- Hardcoded potion shoulder effects have been removed. You can now use any potion vanilla or otherwise with the parameters you'd like, for more information see the [Datapack & Resourcepack Creators](#datapack-&-resourcepack-creators) section.
- Clicking categories of the Stat subsection or the party reorder button in the Summary screen will now produce a click sound.
- Updated PC Recipe.
- Improved Pokémon AI and movement.
- Friendship will slowly increase when Pokémon are shoulder-mounted.
- Master Balls are now unable to be burned when dropped into fire/lava. They're made from stronger stuff.
- Pokémon will appear red when hurt, like regular entities, except when they're fainting.
- Pokémon's air meter no longer depletes while battling underwater.
- Sleeping partially restores PP of Pokémon
- Shoulder mounts now match the shoulder position a bit more accurately when sneaking.
- Poison Heal will now cause poisoned Pokémon to heal outside of battle.
- Updated Poké Ball, PC, UI, evolution and Healing Machine sounds.

### Added cries to the following Pokémon:
- All starters and their evolutions
- Caterpie, Metapod, Butterfree
- Weedle, Kakuna, Beedrill
- Pidgey, Pidgeotto, Pidgeot
- Rattata, Raticate
- Spearow, Fearow
- Ekans, Arbok
- Pichu, Pikachu, Raichu, Alolan Raichu
- Cleffa, Clefairy, Clefable
- Mankey, Primeape, Annihilape
- Ponyta, Rapidash, Galarian Ponyta, Galarian Rapidash
- Farfetch'd, Galarian Farfetch'd, Sirfetch'd
- Onix, Steelix
- Tauros
- Ditto
- Eevee, Vaporeon, Jolteon, Flareon, Espeon, Umbreon, Leafeon, Glaceon, Sylveon
- Hoothoot, Noctowl
- Mareep, Flaaffy, Ampharos
- Aipom, Ambipom
- Wooper, Quagsire, Clodsire
- Snubbull, Granbull
- Miltank
- Poochyena, Mightyena
- Taillow, Swellow
- Ralts, Kirlia, Gardevoir, Gallade
- Shroomish, Breloom
- Nincada, Ninjask, Shedinja
- Buneary, Lopunny
- Chingling
- Chatot
- Riolu, Lucario
- Pidove, Tranquill, Unfezant
- Roggenrola, Boldore, Gigalith
- Venipede, Whirlipede, Scolipede
- Maractus
- Dwebble, Crustle
- Yamask, Cofagrigus
- Bunnelby, Diggersby
- Fletchling, Fletchinder, Talonflame
- Scatterbug, Spewpa, Vivillon
- Honedge, Doublade, Aegislash
- Skrelp, Dragalge
- Phantump, Trevenant
- Pumpkaboo, Gourgeist
- Bergmite, Avalugg
- Mudbray, Mudsdale
- Stufful, Bewear
- Mimikyu
- Rookidee, Corvisquire, Corviknight
- Nickit, Thievul
- Wooloo, Dubwool
- Yamper, Boltund
- Tandemaus
- Fidough, Dachsbun
- Squawkabilly
- Nacli, Naclstack, Garganacl
- Charcadet, Armarouge, Ceruledge
- Maschiff, Mabosstiff
- Shroodle, Grafaiai
- Flittle, Espathra
- Tinkatink, Tinkatuff, Tinkaton
- Varoom, Revavroom
- Glimmet, Glimmora
- Cetoddle, Cetitan
- Tatsugiri

### Fixes
- Fixed spawning moon phase dependent Pokémon only when the moon phase is wrong (that's a funny woopsy)
- Fixed large Pokémon spawning partially inside walls where they suffocate.
- Fixed custom Pokémon in your party or PC not being removed when the addon is removed, causing major issues.
- Fixed messages for entry hazards, screens, weather, damage, healing, Tailwind, Perish Song, Destiny Bond, Shed Skin, Uproar, Forewarn, Disguise, Arena Trap, Yawn, Curse, Clamp, Whirlpool, Liquid Ooze, Miracle Eye, Safeguard, Magic Bounce, Lock On, Focus Energy, Confusion, and more.
- Fixed Porygon not evolving with an Upgrade.
- Fixed super sized Pumpkaboo not having any moves.
- Fixed Infernape look animation.
- Fixed Garchomp T-posing while swimming which was very funny.
- Fixed a bug that caused sleeping Pokémon to stay asleep. Forever. The years passing them by as they dream of a world without hate...
- Fixed a bug that would freeze a battle when a Pokémon gets trapped by an ability, making the trap abilities even scarier and trap-like than they were before.
- Fixed the Poké Ball close animation canceling whenever colliding with a block.
- Fixed lighting and Pokémon label issues when a Pokémon item frame is nearby.
- Fixed Pokémon being able to spawn outside the world border as a tease.
- Fixed deepslate water stone ore items looking like deepslate fire stone ores. Huh?
- Fixed a bunch of client-side logging errors when Pokémon are shoulder mounted. You didn't notice? Good.
- Fixed a crash when wild Pokémon have to struggle under specific circumstances.
- Fixed uncolored pixels on Yanma's shiny texture.
- Fixed apricorn tree leaves looking gross on the Fast graphics mode.
- Fixed hoes not breaking apricorn tree leaves any faster.
- Fixed Shiftry's PC model position.
- Fixed the /pc command not playing the opening sound.
- Fixed different forms of Pokémon not being able to appear as different sizes.
- Fixed the Healing Machine soft locking you from using others when removed by non-players.
- Fixed animations being sped up when using the Replay Mod.
- Fixed particle animations not running when a Pokémon is off-screen.
- Fixed Pokémon variants and layers not rendering correctly when shouldered and playing on a dedicated server, existing shoulders affected will need to be retrieved and shouldered again.
- Fixed shoulder effects not staying applied through situations that remove potion effects such as drinking milk.
- Fixed Shedinja not being able to recover naturally.
- Fixed Shedinja evolving to use the consumed Poké Ball and removed the held item to prevent dupes.
- Fixed Shedinja healing above 1 HP.
- Fixed Shedinja, basically.
- Fixed shearing Pokémon dropping 0-2 wool instead of 1-3.
- Fixed some alignment issues in the stat hexagon of the summary menu. OCD people rejoice.
- Fixed capture calculations not applying ball bonuses entirely correctly.
- Fixed battles soft-locking when consecutive Pokémon faint on switch-in.
- Fixed timing and color of battle window messages.
- Fixed players being able to trade, battle and let out their Pokémon while in spectator mode.
- Fixed Galarian Yamask not being able to evolve and by proxy the ``damage_taken`` evolution requirement.
- Fixed Bisharp not being able to evolve and by proxy the ``defeat`` evolution requirement.
- Fixed White-Striped Basculin not being able to evolve because of a broken ``recoil`` evolution requirement.
- Fixed Primeape, Qwilfish and Stantler not being able to evolve because of a broken ``use_move`` evolution requirement.
- Fixed Bramblin, Pawmo, and Rellor not being able to evolve because of a broken ``blocks_traveled`` evolution requirement.
- Fixed displayName property in spawn files not doing what it's meant to do.
- Fixed Pokémon not sleeping in the wild like we wanted them to.

### Developer
- Added SpawnEvent, ThrownPokeballHitEvent, PokemonSentEvent, PokemonRecalledEvent.
- Added BattleFledEvent, BattleStartedEvent, BattleFaintedEvent.
- Added persistent NBT property inside Pokémon to store quick and simple data.
- Species and FormData have had their evolutions, pre-evolution and labels properties exposed. It is still recommended to work using a Pokémon instance when possible.
- Added capture check to BattleVictoryEvent.
- The various hardcoded potion shoulder effects have been removed, make use of PotionBaseEffect.
- Added ContextManager for tracking causes and contexts of conditions created during a battle. See BattleContext for types of conditions that are tracked. 
- Added MongoDB support for storing Pokémon and Player data. Must be enabled in config, requires MongoDB core and sync drivers (4.10.0+).
- CobblemonShowdown updated to version 10.
- Generation of a battle can be set in BattleFormat.
- Pokémon now have ``teraType``, ``dmaxLevel``, and ``gmaxFactor`` properties. Gimmicks can be used during battle by adding the respective identifiers to ``keyItems`` in PlayerData: ``key_stone``, ``z_ring``, ``dynamax_band``, and ``tera_orb``. Dynamax is only supported in Gen 8 battles. Mega Evolution and Z-Power require custom held items to be added (e.g. an item with the path ``gengarite`` will allow Gengar to Mega Evolve). Currently custom Z-Crystals and Mega Stones are not supported.

### Datapack & Resourcepack Creators
- All potion related shoulder effects have had their IDs changed. They now all share the same type being `potion_effect` and use the vanilla Potion data [parameters](https://minecraft.fandom.com/wiki/Potion#Item_data). For example, here is the converted Pidgey asset:
  - ```json
    {
      "type": "potion_effect",
      "effect": "minecraft:slow_falling",
      "amplifier": 0,
      "ambient": true,
      "showParticles": false,
      "showIcon": false
    }
    ```
- Renamed the ``walked_steps`` evolution requirement to ``blocks_traveled``.
- Added support for scale in animations.
- Added support for jump keyframes (i.e. pre and post keyframes)
- Added structure spawning conditions
- Added Advancement trigger for defeating Pokémon and collecting varieties of Pokémon.
- Added support for "isBattle" and "isTouchingWater" properties on resource pack Pokémon poses. This allows your custom Pokémon to be posed differently when in battle.
- Added support for "isVisible" on a transformed part on resource pack Pokémon poses. This allows your custom Pokémon to have bones disappear in specific poses, such as hiding Greninja's throwing star when not in a battle pose.
- Added support for battle music. Sounds can be added to the ``battle.pvp.default`` and ``battle.pvw.default`` sound events.
- Added 'enabled' optional property on model layers, allowing later variations to disable previously-defined layers. See [this issue](https://gitlab.com/cable-mc/cobblemon/-/issues/335) for how this looks.
- Cobblemon items can now all have their own tooltips via resourcepacks. To add a tooltip, add a lang entry like "item.cobblemon.{item_id}.tooltip". If you want to add multiple tooltip lines you can do so with "item.cobblemon.{item_id}.tooltip_1" and upwards.
- Item interaction evolutions and held item requirements now support NBT by creating an object JSON containing the key ``item`` for what used to be the existing condition support and a ``nbt`` key for the NBT format, this is the string [format](https://minecraft.fandom.com/wiki/NBT_format) expected in commands. Existing data does not need to be updated.
- Fixed faint animations not working properly in add-ons.
- Fixed non-existent species in spawn pool files causing random species to spawn.

### Localization
- Added partial translations for Dutch, Polish, Swedish, Hungarian, Czech, Cyprus Greek, and even Esperanto.
- Updated translation for French and Canadian French, Simplified Chinese, Japanese, Korean, Spanish and Mexican Spanish, Pirate English, German, Thai, Turkish, Portuguese and Brazilian Portuguese, Ukrainian, and Russian.

## [1.3.1 (March 31st, 2023)](#1-3-1)

### Additions
- Added Slugma, Magcargo, Nosepass, and Probopass.
- Elgyem family now drops Chorus Fruit, Geodude family now drops Black Augurite.
- Added missing spawn files for Golett and Bergmite family.
- Apricorns can now be smelted into dyes.
- Added animations to Staryu line and Porygon line.
- Added faint animations to Klink line.
- Add lava surface spawn preset.
- Added an ``any`` evolution requirement allowing you to define ``possibilities`` of other evolution requirements, for example, this allows you to create an evolution that requires the Pokémon to be shiny or a female.
- Added the `/spawnpokemonfrompool [amount]` or `/forcespawn [amount]` command to spawn Pokémon(s) in the surrounding area using the natural spawn rates/pool of that area, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.spawnpokemon` if a permission mod is present. On a successful execution of the command, the amount of Pokémon spawned will be the output.
- Added the `/pokebox` and `/pokeboxall` commands to move Pokémon(s) to the PC from a Player's party, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.pokebox` if a permission mod is present. On a successful execution of the command the output will be the number of pokemon moved to the Player's PC.
- Added the `/pc` command which opens up the PC UI the same way interacting with the block would, this will be a cheat command in the Minecraft permission system or use the permission `cobblemon.command.pc` if a permission mod is present.

### Changes
- You can now click the portraits of other Pokémon in the starter selection screen to navigate directly to them.
- You can now click the right and left arrow keys to navigate PC boxes.
- Link Cables will now require Pokémon to hold any held item normally required for their evolution.
- After a battle, the last Pokémon used now becomes the selected one in your party.
- The `/teach` command can now only allow the Pokémon to be given moves in their learnset, this can be controlled with the permission `cobblemon.command.teach.bypass`, to account for that change the base command now requires the permission `cobblemon.command.teach.base`, this change is meant only for people using a mod capable of providing permissions such as [LuckPerms](https://luckperms.net/).
- Apricorns will no longer collide with their block form when picked, this should improve the experience in automatic farms.
- Increased spawn chances for many Pokémon requiring specific blocks to be nearby.
- Put Cryogonal in more snowy biomes.
- Ditto as well as the Eevee, Gible, and Riolu families have been made more common.
- Lowered spawn rate of Gyarados on the surface of water.
- Apricorn leaves can now be used in the [Composter](https://minecraft.fandom.com/wiki/Composter) block, these have the same chance to raise the compost pile the Minecraft leaves do.
- Updated Gengar's model and texture.
- Updated Swinub line model and animations.
- Tweaked portrait frames for the Pidgey line and for Walking Wake.
- Changed all buff shoulder effects to only give a level 1 buff instead of level 2.
- Made Weavile a little bigger.
- Changed the recipes for Mystic Water, Miracle Seed, and Charcoal Stick to utilise the evolution stones, as well as Never-Melt Ice having an alternate recipe using the Ice Stone.
- Replaced the `Failed to handle` battle messages to `Missing interpretation` to make it more clear that mechanics do work just still pending dedicated messages.
- Healing Machine and PC are now mine-able with pickaxes and Apricorn leaves are mine-able using hoes.

### Fixes
- Fixed killing a Dodrio killing your game. Dodrio will never look the same to us.
- Fixed non-Fire-type Pokémon being immune to lava.
- Fixed custom Pokémon not being usable in battle, properly. A last minute fix caused this to break again; what are these devs not paid for?
- Fixed being locked in an endless healing queue if you broke the healing machine during use.
- Fixed an issue with the experience calculation when the Exp. Share is held.
- Fixed Friendship-based attacks not using friendship values from your Pokémon.
- Fixed Link Cables consuming held items they shouldn't due to not validating the held item of a Pokémon.
- Fixed a crash when Aromatherapy cured the status of party members.
- Fixed moves learnt on evolution not being given when said evolution happens. If you were affected by this issue your existing Pokémon will now be able to relearn those moves.
- Fixed console spam when rendering Pokémon model items.
- Fixed battle messages for 50+ moves and abilities and items.
- Fixed the possible duplicate when capturing Pokémon (probably, this one's hard to reproduce to confirm it's fixed).
- Previously duplicated Pokémon are cleaned from PCs and parties on restart.
- Fixed an issue with some particle effects applying after a Pokémon has died or on top of the wrong Pokémon when using specific mods.
- Fixed Pokémon not looking at each other in battle.
- Fixed Experience Candy and Experience Share attempting to bring Pokémon above level cap causing crashes.
- Fixed level 100 Pokémon having experience go over the cap total amount they should have.
- Fixed `/pokemonspawnat` having the argument positions reverted making it impossible for Brigadier to understand when to suggest coordinates. It is now the intended `/spawnpokemonat <pos> <properties>`.
- Fixed performance issues with shouldered Pokémon in certain systems.
- Fixed learnset issues for Pokémon whose only modern debut was LGPE/BDSP/LA.
- Fixed shiny Zubat, Grimer, Omanyte, Elgyem, Delphox and Aegislash displaying their normal texture.
- Fixed sleeping in beds allowing fainted Pokémon to receive experience after a battle ends somehow.
- Fixed an issue where a Pokémon will claim to have learnt a new move they already have in their moveset when learnt at an earlier level in their previous evolution. I realize that's confusing.
- Fixed Dispensers not being able to shear Wooloo. This will also extend to other mods that check if an entity is valid to shear.
- Fixed the currently held item of your Pokémon not dropping to the ground when removing it if your inventory was full.
- Fixed creative mode allowing you to make your Pokémon hold more than 1 of the same item.
- Fixed a Pokémon duplication glitch when teleporting between worlds.
- Fixed dedicated servers being able to reload Cobblemon data with the vanilla `/reload` command causing unintended behavior for clients.
- Fixed underground Pokémon spawning above ground.
- Fixed Pokémon portrait not reverting back to the Pokémon after a failed capture during battle.
- Fixed edge texture artifacts on pane elements for Tentacool and Tentacruel models.
- Fixed crash caused by Pokémon pathing
- Fixed Pokémon not returning to their balls when being healed in a healing machine
- Fixed all Gen IX Pokémon as well as forms added in PLA and Wyrdeer, Kleavor, Ursaluna, Basculegion, Sneasler, Overqwil, and Enamorus having 0 exp yields.
- Fixed Irons Leaves having bluetooth back legs. If you saw it, you know what I mean.
- Fixed Golurk not having shoulder plates on its shoulders.
- Fixed some water Pokémon walking onto land from the water even though they are fish.
- Fixed Porygon2 and PorygonZ being too small.
- Fixed Snivy line head look animation.
- Fixed Staryu line not being able to swim.
- Fixed an incompatibility with [Thorium](https://modrinth.com/mod/thorium) patch for [MC-84873](https://bugs.mojang.com/browse/MC-84873).
- Fixed Pidgeotto wings when walking.
- Fixed Delphox walk animation.
- Fixed Froakie line sleep animations in battle.
- Fixed Pokémon missing the non-level up moves they could relearn when rejoining a world until a new move was added to their relearn list.
- Fixed instantly fleeing from Pokémon set to be unfleeable.
- Fixed Pumpkaboo line forms not working. (Currently sizes aren't visual but check base stats to see which size you have.)
- Fixed a bug that caused already interpreted messages for moves to be mistaken as uninterpreted.
- Fixed a Pokémon spawner bug that caused Pokémon to not spawn due to dropped item entities.
- Fixed a bug that causes Pokémon model items to be invisible.

### Developer
- Add events that are fired just before and after a Pokémon is released (ReleasePokemonEvent.Pre and .Post)

### Localization
- Added complete translations for Japanese, Thai, and Canadian French.
- Added partial translations for Russian, Ukrainian, Mexican Spanish, and Korean.
- Updated every existing language's translation.
- All the translators that contributed are amazing.

## [1.3.0 - The Foundation Update (March 17th, 2023)](#1-3-0)

### Dependencies
- Upgraded Fabric API dependence to 0.75.1+1.19.2
- Upgraded Architectury API dependence to 6.5.69
- Cobblemon Forge now depends on Kotlin for Forge.

### Additions
- Added new models and animations for Poké Balls and reworked their mechanics to feel much smoother instead of being pure frustration.
- Added party scrolling via holding R and using the mouse wheel so you don't need to take your hand off your mouse.
- Added a cap of Pokémon spawns in an area because waiting a while made things insane. This is controlled by a new `pokemonPerChunk` config option.
- Added models and animations for heaps of Pokémon (101): Riolu, Lucario, Chimchar, Monferno, Infernape, Turtwig, Grotle, Torterra, Popplio, Brionne, Primarina, Treeko, Grovyle, Sceptile, Snivy, Servine, Serperior, Tepig, Pignite, Emboar, Oshawott, Dewott, Samurott, Grookey, Thwackey, Rillaboom, Scorbunny, Raboot, Cinderace, Sobble, Drizzile, Inteleon, Fennekin, Braixen, Delphox, Froakie, Frogadier, Greninja, Chespin, Quilladin, Chesnaught, Miltank, Torkoal, Kricketot, Kricketune, Heatmor, Durant, Wooloo, Dubwool, Pumpkaboo, Gourgeist, Sigilyph, Cryogonal, Whismur, Loudred, Exploud, Misdreavus, Mismagius, Tatsugiri, Eiscue, Luvdisc, Stantler, Wyrdeer, Gible, Gabite, Garchomp, Sneasel, Weavile, Elgyem, Beheeyem, Baltoy, Claydol, Nacli, Naclstack, Alcremie, Milcery, Dhelmise, Morelull, Shiinotic, Xerneas, Klink, Klang, Klinklang, Joltik, Galvantula, Honedge, Duoblade, Aegislash, Spiritomb, Mawile, Carvanha, Sharpedo, Seedot, Nuzleaf, Shiftry, Lotad, Lombre, Ludicolo, Pineco, Forretress, and Spinda.
- Added generation 3, 4, 5, 6, 7, and 8 Starter Pokémon to the starter select screen.
- Added particle effect support for model animations
- Added particle effect and animation for Gastly.
- Added sleep and faint animations to many Pokémon.
- Added item holding for Pokémon. Any Minecraft item can be given to a Pokémon by holding shift and right-clicking them. Traditional Pokémon held items will have their expected battle effects.
- Added heaps of held items with crafting recipes: Assault Vest, Big Root, Black Belt, Black Sludge, Charcoal, Choice Band, Choice Scarf, Choice Specs, Dragon Fang, Exp. Share, Focus Band, Hard Stone, Heavy-Duty Boots, Leftovers, Light Clay, Lucky Egg, Magnet, Miracle Seed, Muscle Band, Mystic Water, Never-Melt Ice, Poison Barb, Quick Claw, Rocky Helmet, Safety Goggles, Sharp Beak, Silk Scarf, Silver Powder, Soft Sand, Spell Tag, Twisted Spoon, and Wise Glasses.
- Added heaps of evolution items with crafting recipes: Milcery's sweets items, Chipped Pot, Cracked Pot, Deep Sea Scale, Deep Sea Tooth, Dragon Scale, Galarica Cuff, Galarica Wreath, Peat Block, Prism Scale, Razor Claw, Razor Fang, Reaper Cloth, Sachet, Sweet Apple, Tart Apple, and Whipped Dream.
- Existing evolution items all now either have a crafting recipe or drop from Pokémon.
- Added the Item [tags](https://minecraft.fandom.com/wiki/Tag) `cobblemon:held/experience_share` and `cobblemon:held/lucky_egg` allowing you to mark any items you desire to have the effects implied in the tag name.
- Added an interface that appears when interacting with your Pokémon while sneaking. The interface allows for interactive options such as shouldering and exchanging held items.
- Added blinking animations to many Pokémon.
- Added animated texture support.
- Added translucent option for aspect layers.
- Added glowing textures to many Pokémon and it looks amazing.
- Added the Saturation shoulder effect.
- Added the Haste shoulder effect, initially for Joltik.
- Added the Water Breathing shoulder effect, initially for Wooper.
- Added the Speed shoulder effect, initially for Pichu and Pikachu.
- [Dispensers](https://minecraft.fandom.com/wiki/Dispenser) can now use shears to automatically harvest fully grown Apricorns.
- Added milking to Miltank.
- Added shearing to Wooloo and Dubwool.
- Added data for generation 9 Pokémon species, moves, and ability data. They're all still Substitute models, but their moves and abilities work.
- Added support for custom Pokémon to implement 'quirks' such as blinks.
- Added sound effect for harvesting Apricorns.
- Added icon to summary and PC interfaces to indicated if a Pokémon is shiny.
- Added the ``/spawnpokemonat <pos> <properties>`` command, the ``pos`` argument uses the same syntax as the Minecraft [summon](https://minecraft.fandom.com/wiki/Commands/summon) command.
- Added the `/giveallpokemon` command which is definitely safe and not insane.
- Added compatibility with Carry On by preventing the mod being able to interact with Cobblemon entities, the mod caused too many gameplay detrimental features to stay enabled.
- Added healing to your party when you sleep in a bed.
- Added the 'ability' Pokémon Property so commands can specify the ability.
- Added block tag support to the 'neededBaseBlocks' and 'neededNearbyBlocks' spawn condition.
- Added a config option for disallowing players from damaging Pokémon by hand.
- Apricorn seeds can now be used with the [Composter](https://minecraft.fandom.com/wiki/Composter), these have the layer increase chance of 65% like Apricorns and various Minecraft crops.
- Added support for Pokémon species data appending making it so datapack developers no longer need to overwrite files.
- Added an implementation of every [catch rate](https://bulbapedia.bulbagarden.net/wiki/Catch_rate) from generation 1 to 9, these can be used by changing the ``captureCalculator`` config value:
  - ``generation_1`` Sets the calculator to the generation 1 implementation.
  - ``generation_2`` Sets the calculator to the generation 2 implementation.
  - ``generation_2_fixed`` Sets the calculator to the generation 2 implementation with the status multiplier bug fixed.
  - ``generation_3_4`` Sets the calculator to the generation 3 and 4 implementation.
  - ``generation_5`` Sets the calculator to the generation 5 implementation.
  - ``generation_6`` Sets the calculator to the generation 6 implementation.
  - ``generation_7`` Sets the calculator to the generation 7 implementation.
  - ``generation_8`` Sets the calculator to the generation 8 implementation.
  - ``generation_9`` Sets the calculator to the generation 9 implementation.
  - ``cobblemon`` Sets the calculator to the custom Cobblemon implementation. This is the default value.
  - ``debug`` Sets the calculator to the debug/cheat implementation, every attempt will be a successful critical capture.

### Changes
- Pokémon now save to the world by default, meaning the same Pokémon will remain in the world and not disappear after you log out and log back in. They still despawn over time though.
- Significantly sped up the Poké Ball shake animation so it takes less time to try to catch Pokémon.
- Update the PC and Healing Machine models and bounding boxes.
- The Healing Machine and PC now emit light when fully charged or when turned on respectively.
- The PC block screen will now turn on when being used.
- The Healing Machine will now visually display its charge level using 6 stages.
- The Healing Machine will now emit a redstone signal with the strength of 1 for every 10% charge it has when attached to a [Redstone Comparator](https://minecraft.fandom.com/wiki/Redstone_Comparator).
- Made it so that particles are not shown whenever you have a shoulder Pokémon that gives potion effects.
- Changed hitbox and size definitions for Decidueye, Blastoise, and Magmortar
- Apricorns can now be harvested with Axes, the speed will scale with enchantments and tool material, only dropping the Apricorn if fully grown, these will still destroy the Apricorn so the manual harvest still is recommended unless you're just keen on destroying trees.
- Apricorns are now a part of the vanilla tag ``minecraft:blocks/mineable/axe``.
- Apricorns are now compatible with any mod that breaks a whole tree at once.
- Apricorns no longer have a config value for the seed drop chance these are now a part of their loot table which can be found in ``cobblemon/loot_tables/blocks/<color>_apricorn.json``.
- Advancements were redone to be slightly more interesting, with improved names, descriptions, and sorting.
- Updated models and textures for Tentacool line, Gengar, Slowpoke line, Tyrogue line, Doduo line, Dratini, Dragonair, Quagsire, and Piplup line. There were probably others, the team lost track.
- Improved sending out Pokémon at the start of battle so that they are positioned in a less annoying way.
- Name Tags will no longer be used on Pokémon and Poke Ball entities, this prevents the item from being wasted.
- Lowered spawn rate of Tauros.
- Sableye now spawns near gem ores as well as amethyst.
- Added evolution stones and items to item tags

### Fixes
- Fixed catch rates being entirely too difficult.
- Fixed various strange battle issues such as Flying types being vulnerable to Ground type moves and status effects hitting despite vulnerabilities.
- Fixed shiny Gyarados not using the red Gyarados texture.
- Improved the framing of all in-game Pokémon in the party and PC GUIs so they aren't halfway out of the screen or something else crazy.
- Fixed incompatibility with Kotlin for Forge (by depending on Kotlin for Forge ourselves)
- Fixed Gengar, Goodra, and many other Pokémon showing the types of an alternate form despite those not being modelled yet.
- Fixed datapack Pokémon not being able to battle.
- Fixed Pokémon always being created with a moveset as if they're level 1 instead of their current level.
- Fixed an issue of Pokémon not rendering in GUIs on some Mac displays.
- Fixed a soft-duplicate that could occur when a party Pokémon is pushed through a Nether Portal or left in a boat.
- Fixed Pokémon that faint from poison appearing to be on full health and suckering you into false hope.
- Fixed incorrect spawns of Tentacool, Tentacruel, Dragonite, Politoed, Tangrowth, Lickilicky, Electivire, and Magmortar.
- Fixed crashes involving opening the Pokémon summary GUI with an empty party.
- Fixed lower brightness settings causing Pokémon to appear much too dark in menus such as the party and PC.
- Fixed Showdown sometimes failing to start, causing crashes.
- Fixed Showdown initialization happening several times when you login, more depending on how many times you have relogged this session.
- Fixed Showdown failing to update on first attempt. We totally weren't accidentally deleting our target directory or anything, nope.
- Fixed HP recovery related battle actions not animating for the client nor updating the in-game Pokémon HP.
- Fixed moves that force a switch such as Teleport and U-Turn soft locking battles.
- Fixed missing battle text for Bide, Speed Boost, Belly Drum, Anger Point, and Haze.
- Fixed battle messages for many field effects starting, ending, and actions caused by them such as blocking certain moves.
- Improved battle messages for effects that prevent a move from being executed such as a Taunt, requiring a recharge, flinched, etc.
- Fixed move names not being translated in battle messages.
- Fixed stat change messages for boosts over 3 stages.
- Fixed experience calculation not being completely accurate.
- Fixed positioning of Poké Balls when rendered in Healing Machines.
- Fixed a desync issue on servers where all Pokémon seemed like they were special forms when they weren't.
- Fixed an incompatibility with [Exordium](https://www.curseforge.com/minecraft/mc-mods/exordium).
- Fixed datapack Pokémon language key generation. A Pokémon under the namespace ``example`` named ``Pogemon`` will now correctly look for the lang key ``example.species.pogemon.name``.
- Fixed client not receiving messages for the different "stages" for the move Bide.
- Fixed the Medium Slow and Medium Fast experience group IDs, they're now ```medium_slow``` and ``medium_fast``. Any custom datapack Pokémon will require an update.
- Fixed Pokémon friendship being capped to the maximum level config value instead of the friendship one when loading Pokémon data.
- Fixed Poké Balls taking forever to capture Pokémon if you are underwater or up in the air where it takes a long time to hit the ground.
- Fixed Pokémon being unable to spawn on blocks such as snow layers.
- Fixed Pokémon spawning inside of trees.
- Fixed Pokémon experience not loading after a restart and instead going back to the minimal amount for the current level.
- Fixed being able to use ``/healpokemon`` in battle.
- Fixed being able to select fainted party members in the switch menu causing the battle to lock.
- Fixed ``/spawnpokemon`` command not supporting any command source other than players.
- Fixed issues with Charizard's sleep pose.
- Fixed players being able to use multiple healer machines at once.
- Fixed Pokémon layers not rendering when a Pokémon is on your shoulder.
- Fixed Caterpie and Weedle not moving or looking at players. That was meant to be Metapod and Kakuna; how embarrassing.
- Fixed Pokémon not carrying over the correct equivalent original ability when evolving from stages that only had one ability.
- Fixed Deerling and Sawsbuck not spawning with the correct season.
- Fixed issue of not being able to drag the scroll bar in summary and battle interfaces.
- Fixed optional aspects not saving and loading properly.
- Fixed layering logic so multiple texture layers can exist on a Pokémon (probably).
- Fixed not all Poké Balls being associated with the `cobblemon:pokeballs` item tag.
- Fixed the `/pokemoneditother` command not working.
- Fixed ambient sound file path for Porygon2.
- Fixed forms not being able to unset the secondary type of a Pokémon in the stat JSON.
- Fixed moves that haven't carried over from generation 8 onwards having the description they did in the generation 8 games instead of their last valid one.
- Fixed shoulder mounted pokemon not returning to party on healer use and on evolution

### Developer
- Reworked CatchRateModifier, as such, existing implementations need to be updated.
- Fixed minimumDistanceBetweenEntities option being half of what it's set as.
- Fixed the contents of CobblemonEvents, CobblemonBlocks etc having getters instead of just being public static properties.
- Added ApricornHarvestEvent.
- Added a new item for representing Pokémon within native UI menus or item frames which display as the Pokémon's model. It's called a PokemonItem, with static functions to build one.

### Localization
- Added complete translations for French, German, Simplified Mandarin, Brazilian Portuguese, and Pirate English.
- Added partial translations for Traditional Mandarin, Italian, and Spanish. We'd love more help with this!
- Thank you to all of the fantastic volunteer translators for taking the time to help with this!

## [1.2.0 - The Customization Update (January 1st, 2023)](#1-2-0)
### Additions
- Added models for Natu and Xatu, Murkrow and Honchkrow, Wailmer and Wailord.
- Added new PC interface and it is beautiful.
- Reworked the battle system so that battles load faster, cause fewer bugs, and can run on shared server hosts. This is a very big change that also reduced the size of the mod by 50mb!
- Added full resource pack / data pack customization of models, textures, animations, spawning, and spawn file presets to make custom Pokémon species and variations very easy to create. You can find the guide for creating custom Pokémon on [our website](https://cobblemon.com/guides/custompokemon.html)!
- Added water surface spawning for Pokémon like Lapras.
- Added emissive texture support to Pokémon render layers.
- Added compatibility for Mod Menu ([CurseForge](https://www.curseforge.com/minecraft/mc-mods/modmenu), [Modrinth](https://modrinth.com/mod/modmenu)).
- Added blank ambient Pokémon cries so cries can be added via resource packs.
- Added new sounds for GUIs and item uses.
- Added `nature` and `pokeball` options to commands such as `/spawnpokemon` and `/givepokemon`.

### Changes
- Reinforced party and PC saving to make data corruption from crashes less bad.
- Added a config option for whether the starter config will be exported, making it more maintainable as we add starter Pokémon.
- Battles now start with the options menu open for convenience.
- Doubled the default charge rate of healers. You'd need to reset your config under `./config/cobblemon/main.json` to see this change!
- Changed the default Apricorn seed chance config value from 6% to 10%.
- The mod now correctly reports our dependency on Architectury API so people don't get super confused when things don't work.
- Pokémon now look at their opponents during battle.
- Updated Sableye's animations to be super freaky.
- Changed the healer advancements to make the healing machine's mechanics more obvious.

### Fixes
- Fixed an incompatibility with [Porting Lib](https://github.com/Fabricators-of-Create/Porting-Lib) used by Fabric ports of mods such as Create or Twilight Forest.
- Fixed HP and max HP values in the battle GUI not being correct.
- Fixed some animations on snake-type Pokémon being super slow.
- Fixed a typo in the English name for Calcium. Calcuim.
- Fixed Pokémon gradually becoming rarer around you if you move long distances.
- Fixed a shoulder mount crash on Fabric.
- Fixed a rare issue where chunks would take a really long time to generate.
- Fixed a singleplayer bug where battles wouldn't work after leaving then rejoining a world.
- Fixed stat calculations for everything except HP. HP was fine though :).
- Fixed a randomized Pokémon spawning in mountains that corrupted the data of whatever party or PC it got into. Yikes.
- Fixed a rare crash involving literally random number generation. A random crash involving random numbers.
- Fixed all regular Mewtwo having the stats and types of Mega Mewtwo X - same deal with many other Pokémon.
- Fixed the framing of many Pokémon in GUIs.
- Fixed texture glitches with Rattata and Nidoqueen (again!).
- Fixed dropped item forms of all Pokéballs and the Healing Machine, and slightly adjusted all other display settings
- Fixed issues with forms not showing the correct evolution in the evolution menu.
- Fixed some alternate forms not having the alternate stats and types.
- Fixed moves that only work in double battles not being selectable at all (such as Helping Hand and Aromatic Mist).
- Fixed abilities not remaining legal in some forms.
- Fixed Poké Ball capture effects not triggering after a successful capture, such as the Heal Ball's healing effect.
- Fixed multiple-hit moves sending gibberish into the battle chat.
- Fixed Pyukumuku not being appropriately scaled.
- Fixed shiny and other variations of Pokémon not showing in the battle GUI.
- Fixed Eevee being poorly positioned and un-animated on shoulders.
- Fixed a Pokémon's hitbox not updating when it evolves while sent out.
- Fixed a Pokémon's PP going from zero to above the maximum when entering another battle.

## [1.1.1 (November 27th, 2022)](#1-1-1)
### Fixes
- Fixed a critical issue with servers where Pokémon data didn't properly synchronize and so you couldn't see any.

## [1.1.0 - The Swim and Sleep Update (November 27th, 2022)](#1-1-0)
#### "Ideally not at the same time."
### Additions
- Added a new GUI for viewing party information, rearranging moves, and evolving Pokémon. It looks too good.
- Starter Pokémon will sleep on top of you if sent out when you get on a bed.
- Added sleeping animations for starters, the Weedle family, and the Caterpie family. More to come.
- Added Alolan Rattata and Alolan Raticate, Sableye, Deerling and Sawsbuck, and Pyukumukurutudulu or whatever it's called.
- Added swimming AI so Pokémon don't sink to the bottom in water.
- Aquatic Pokémon like Magikarp desperately move to water if they're on land.
- Added status condition indicators in the party overlay.
- Added HP labels to the battle interface so that you can see how much health you actually have.
- Added spawn data for all final and special evolutions previously lacking spawn data.
- Added shiny textures for many Pokémon (thank you MageFX!): Aerodactyl, Articuno, Zapdos, Moltres, Chansey, the Dratini family, Electabuzz, Goldeen and Seaking, Hitmonchan and Hitmonlee, Jynx, Kabuto and Kabutops, Magmar, Lickitung, Mr. Mime, Omanyte and Omastar, Rhyhorn and Rhydon, Koffing and Weezing, Porygon, Scyther, Seadra, Staryu and Starmie, and Tangela. Phew!
- Added a couple of new advancements.
- Added new items: Calcium, Carbos, HP Up, Iron, Protein, and Zinc. Currently only obtainable in Creative Mode (It's a surprise tool that will help us later).

### Changes
- Significantly improved AI pathing for larger entities so they won't keep trying to move to places they cannot possibly fit.
- Changed the starter menu and summary menu keybinds to `M` by default.
- Pokémon that are sent out slowly raise friendship. Before this it was faster and even worked when not sent out (wildly unbalanced).
- Updated Link Cable and Protector item sprites to be prettier.
- Slightly polished the Poké Ball opening and Poké Ball item use sounds.
- `/givepokemon random`, `/spawnpokemon random`, and `/spawnallpokemon` will now only choose implemented Pokémon.
- The battle message box now displays even when the battle GUI is minimised so that you can still see what's going on.
- Moved the `R` prompt in battle to be higher on the screen so that it's not as distracting.

### Fixes
- Fixed shinies and form variations not displaying in the party or PC.
- Fixed servers getting stuck on shutdown due to non-closed showdown server connections.
- Fixed a niche situation where players could challenge themselves. It's a little inspirational if you think about it.
- Fixed Pokémon natures not saving such that every time you restarted they had a totally different nature.
- Fixed some underground Pokémon spawning above ground instead. I'm told other weird spawns were probably fixed at the same time.
- Fixed Pokémon sometimes running in place. "It's still possible, but much less likely" - Yeah, ok devs.
- Fixed mod incompatibility with many Architectury API mods, including *Biome Makeover* and *Earth2Java*.
- Fixed a mod incompatibility with Minecraft Transit Railway Fabric and probably a bunch of other Fabric mods.
- Fixed being unable to customize keybinds on Forge.
- Fixed Summary keybinding being labeled as PokéNav. That comes later.
- Fixed apricorns spawning without leaves which meant sometimes apricorns were growing on the side of dirt and stone which doesn't make much sense to me.
- Fixed messages appearing in the console whenever a healer is used.
- Fixed spawning in several biome tag categories.
- Fixed resource pack support for Pokémon models and textures.
- **Model Fixes**
  - Fixed Paras and Nidoqueen looking very weird.
  - Fixed Hitmonchan asserting dominance with a T-pose as well as Grimer and Muk pointing their hands to the sky.
  - Fixed specific Pokémon suddenly pausing their animation after staying still for a long time.
  - Fixed Mankey's feet being buried in the ground.
  - Updated the Substitute model and updated its shiny texture to be better for the red-green colorblind.
  - Improved Horsea, Seadra, and Kingdra animations, especially on land.
- **Battle Fixes**
  - Fixed an issue with battles where Pokémon had a different max HP causing some desync issues with the health bars.
  - Fixed Magnitude battle messages.
  - Moves that are disabled or out of PP now show transparently in the battle GUI instead of being hidden completely.
  - Statuses like sleep and frozen no longer last forever if it was carried over from a previous battle.

### Localization
- Added species, ability, and move translations for `ko_ko`, `jp_jp`, `fr_fr`, `es_es`, `it_it`, and `zh_cn`.

## [1.0.0 (November 12th, 2022)](#1-0-0)
- Initial release.
