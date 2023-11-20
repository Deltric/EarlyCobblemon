import glob
import os
import time
import json
import zipfile
from io import BytesIO
import requests
import pandas as pd
from sqlalchemy import create_engine
import openpyxl
from cobblemon_drops_csv_to_json import get_drops_df, parse_drops

# This script is used to convert the data from the cobblemon spawning spreadsheet into a json format

# Define what pokemons to include, if nothing is specified (empty array), all will be included.
# filter by number ranges (dex range)
pokemon_numbers = range(0, 2000)
# filter by type
included_types = ['basic', 'boss']
# filter by context
known_contexts = ['grounded', 'submerged', 'seafloor', 'lavafloor', 'surface']
# filter by bucket ['common', 'uncommon', 'rare', 'ultra-rare']
bucket_mapping = ['common', 'uncommon', 'rare', 'ultra-rare']
# filter by generation
included_generations = []

# exclude the following forms - make them spawn, but don't put these tags in the name.
excluded_forms = ['[Nether]', '[Quartz]']
# Biome mapping, used to convert the Biome column to the format used in the spawn json
biome_mapping = {
    'abyss': '#cobblemon:is_abyss',
    'arid': '#cobblemon:is_arid',
    'badlands': '#cobblemon:is_badlands',
    'bamboo': '#cobblemon:is_bamboo',
    'beach': '#cobblemon:is_beach',
    'bumblezone': '#the_bumblezone:the_bumblezone',
    'cherry grove': 'minecraft:cherry_grove',
    'coast': '#cobblemon:is_coast',
    'cold': '#cobblemon:is_cold',
    'cold ocean': '#cobblemon:is_cold_ocean',
    'crag': '#cobblemon:is_crag',
    'crystal canyon': 'the_bumblezone:crystal_canyon',
    'deep': '#cobblemon:is_deep',
    'deep dark': '#cobblemon:is_deep_dark',
    'deep ocean': '#cobblemon:is_deep_ocean',
    'desert': '#cobblemon:is_desert',
    'dripstone': '#cobblemon:is_dripstone',
    'end': '#minecraft:is_end',
    'end highlands': 'minecraft:end_highlands',
    'floral': '#cobblemon:is_floral',
    'floral meadow': 'the_bumblezone:floral_meadow',
    'forest': '#cobblemon:is_forest',
    'freezing': '#cobblemon:is_freezing',
    'frigid': '#cobblemon:is_frigid',
    'frozen': '#cobblemon:is_frozen',
    'frozen ocean': '#cobblemon:is_frozen_ocean',
    'glacial': '#cobblemon:is_glacial',
    'grassland': '#cobblemon:is_grassland',
    'highlands': '#cobblemon:is_highlands',
    'hills': '#cobblemon:is_hills',
    'howling constructs': 'the_bumblezone:howling_constructs',
    'infested caves': 'terralith:cave/infested_caves',
    'jungle': '#cobblemon:is_jungle',
    'lukewarm ocean': '#cobblemon:is_lukewarm_ocean',
    'lush': '#cobblemon:is_lush',
    'magical': '#cobblemon:is_magical',
    'mangrove swamp': 'minecraft:mangrove_swamp',
    'mountain': '#cobblemon:is_mountain',
    'mushroom': '#cobblemon:is_mushroom',
    'mushroom fields': 'minecraft:mushroom_fields',
    'nether': '#minecraft:is_nether',
    'nether basalt': '#cobblemon:is_nether_basalt',
    'nether crimson': '#cobblemon:is_nether_crimson',
    'nether desert': '#cobblemon:is_nether_desert',
    'nether forest': '#cobblemon:is_nether_forest',
    'nether frozen': '#cobblemon:is_nether_frozen',
    'nether fungus': '#cobblemon:is_nether_fungus',
    'nether mountain': '#cobblemon:is_nether_mountain',
    'nether overgrowth': '#cobblemon:is_nether_overgrowth',
    'nether quartz': '#cobblemon:is_nether_quartz',
    'nether soul fire': '#cobblemon:is_nether_soul_fire',
    'nether soul sand': '#cobblemon:is_nether_soul_sand',
    'nether toxic': '#cobblemon:is_nether_toxic',
    'nether warped': '#cobblemon:is_nether_warped',
    'nether wasteland': '#cobblemon:is_nether_wasteland',
    'ocean': '#cobblemon:is_ocean',
    'overworld': '#cobblemon:is_overworld',
    'peak': '#cobblemon:is_peak',
    'plains': '#cobblemon:is_plains',
    'plateau': '#cobblemon:is_plateau',
    'pollinated fields': 'the_bumblezone:pollinated_fields',
    'reef': '#cobblemon:is_reef',
    'salt': '#cobblemon:is_salt',
    'sandy': '#cobblemon:is_sandy',
    'savanna': '#cobblemon:is_savanna',
    'sky': '#cobblemon:is_sky',
    'skylands autumn': 'terralith:skylands_autumn',
    'skylands spring': 'terralith:skylands_spring',
    'skylands summer': 'terralith:skylands_summer',
    'skylands winter': 'terralith:skylands_winter',
    'snowy': '#cobblemon:is_snowy',
    'snowy forest': '#cobblemon:is_snowy_forest',
    'spooky': '#cobblemon:is_spooky',
    'sunflower plains': 'minecraft:sunflower_plains',
    'swamp': '#cobblemon:is_swamp',
    'taiga': '#cobblemon:is_taiga',
    'temperate': '#cobblemon:is_temperate',
    'thermal': '#cobblemon:is_thermal',
    'tropical island': '#cobblemon:is_tropical_island',
    'tundra': '#cobblemon:is_tundra',
    'void': '#cobblemon:is_void',
    'volcanic': '#cobblemon:is_volcanic',
    'warm ocean': '#cobblemon:is_warm_ocean',
    'warped desert': 'byg:warped_desert'
}
# List of ignored biome identifiers
ignored_biomes = ['freshwater']
# List of all currently known presets
preset_list = [
  'ancient_city',
  'derelict',
  'desert_pyramid',
  'end_city',
  'flowers',
  'foliage',
  'freshwater',
  'gemstone',
  'illager_structures',
  'jungle_pyramid',
  'lava_surface',
  'natural',
  'nether_fossil',
  'nether_structures',
  'ocean_monument',
  'ocean_ruins',
  'pillager_outpost',
  'redstone',
  'river',
  'ruined_portal',
  'shipwreck',
  'stronghold',
  'trail_ruins',
  'treetop',
  'underlava',
  'underwater',
  'urban',
  'village',
  'water_surface',
  'wild',
  'woodland_mansion'
]

def main(only_update_existing_files=True, ignore_filters=False):
    print("♥ Cobblemon Spawn CSV to JSON ♥")

    # Download an excel file and convert it into csv format
    csv_df = download_excel_data(spawn_spreadsheet_excel_url)
    print(csv_df.head()) # Quick integrity check: Display first 5 rows of the dataframe

    # Verify all biome tags are valid
    invalid_biome_tags = verifyBiomeTags()

    # Apply filters
    csv_df = validateAndFilterData(csv_df, only_update_existing_files, ignore_filters)

    # add the drops column to the csv_df
    drops_df = load_special_drops_data()
    csv_df = csv_df.merge(drops_df, on='Pokémon', how='left')

    # alter the data in the b column to be a 4 digit number by prepending 0s if necessary
    csv_df['No.'] = csv_df['No.'].apply(lambda x: str(x).zfill(4))

    # add a column for the pokemon's id using generateID
    csv_df['id'] = csv_df.apply(lambda x: generateID(x['Pokémon'], x['Entry']), axis=1)

    # alter the data in the Pokémon column by using generateName
    csv_df['Pokémon'] = csv_df['Pokémon'].apply(generateName)

    # make the canSeeSky column lowercase
    csv_df['canSeeSky'] = csv_df['canSeeSky'].apply(lambda x: str(x).lower())

    # Creating the output directory if it doesn't exist
    os.makedirs(pokemon_data_dir, exist_ok=True)

    # Group the data by dex number
    csv_grouped = csv_df.groupby('No.')

    # Processing each Pokémon group and converting it to JSON
    for dex, group in csv_grouped:
        file_id = dex
        pokemon_json = transform_pokemon_to_json(group, invalid_biome_tags)
        # if there are multiple entries that are equal except for biomes, then combine them into one entry
        #reduced_pokemon_json = reducePokemonJson(pokemon_json) # no longer needed due to update of the spreadsheet
        save_json_to_file(pokemon_json, file_id, group['Pokémon'].iloc[0], pokemon_data_dir)

    # Save data to SQLite
    # print("Saving data to SQLite")
    # write_to_sqlite(csv_df, sqlite_db_name, sqlite_table_name)

    print("♫♪ Execution finished successfully ♪♫")


def validateAndFilterData(csv_df, only_update_existing_files=False, ignore_filters=False):
    # Data Validation
    # remove any rows that have a blank "No." column
    csv_df = csv_df[csv_df['No.'].notna()]
    # remove any rows that have a blank "Entry" column
    csv_df = csv_df[csv_df['Entry'].notna()]
    # remove any rows that have a blank "Pokémon" column
    csv_df = csv_df[csv_df['Pokémon'].notna()]
    # Filter the data
    if not ignore_filters:
        # Filter by pokemon number
        if pokemon_numbers:
            csv_df = csv_df[csv_df['No.'].isin(list(pokemon_numbers))]
        # Filter by type
        if included_types:
            csv_df = csv_df[csv_df['Type'].isin(included_types)]
        # Filter by context
        if known_contexts:
            csv_df = csv_df[csv_df['Context'].isin(known_contexts)]
        # Filter by bucket
        if bucket_mapping:
            csv_df = csv_df[csv_df['Bucket'].isin(bucket_mapping)]
        # Filter by generation
        if included_generations:
            csv_df = csv_df[csv_df['Gen'].isin(included_generations)]
    if only_update_existing_files:
        # extract the dex numbers from the filenames of the existing files
        existing_dex_numbers = [int(file.split('/')[-1].split('_')[0]) for file in os.listdir(pokemon_data_dir)]
        csv_df = csv_df[csv_df['No.'].isin(existing_dex_numbers)]
    return csv_df


def transform_pokemon_to_json(pokemon_rows, invalid_biome_tags):
    json_data = {
        "enabled": True,
        "neededInstalledMods": [],
        "neededUninstalledMods": [],
        "spawns": []
    }

    for _, row in pokemon_rows.iterrows():
        spawn_data = {
            "id": row['id'],
            "pokemon": row['Pokémon'],
        }
        # Conditional fields
        if pd.notna(row['Preset']):
            spawn_data['presets'] = [preset.strip().lower() for preset in row['Preset'].split(',')]

        more_spawn_data = {
            "type": "pokemon",
            "context": row['Context'],
            "bucket": row['Bucket'],
            "level": f"{int(row['Lv. Min'])}-{int(row['Lv. Max'])}",
            "weight": row['Weight'],
        }
        spawn_data.update(more_spawn_data)

        # Weight Multipliers field
        weight_multipliers = []
        if pd.notna(row['Multiplier']):
            # split weight multipliers row by comma, then split each string by "x" and add it to the weight_multipliers dictionary
            for string in str(row['Multiplier']).split(','):
                string = string.strip()
                items = string.split('x')
                identifier = items[0].strip()
                multiplier = float(items[1].strip())
                # check if the identifier is a weather condition or night, and add it to the weight_multipliers dictionary
                condition = {}
                match identifier:
                    case "Storm":
                        condition["isThundering"] = True
                    case "Night":
                        condition["timeRange"] = "night"
                    case "Day":
                        condition["timeRange"] = "day"
                    case "Twilight":
                        condition["timeRange"] = "twilight"
                    case "Beehive":
                        condition["neededNearbyBlocks"] = ["#minecraft:beehives"]
                    case _:
                        print(f"Unknown weight multiplier identifier: {identifier}")
                weight_multipliers.append({"multiplier": multiplier, "condition": condition})
            if len(weight_multipliers) == 1:
                spawn_data['weightMultiplier'] = weight_multipliers[0]
            elif len(weight_multipliers) > 1:
                spawn_data['weightMultipliers'] = weight_multipliers

        # Condition field
        condition = {}

        if pd.notna(row['canSeeSky']) and row['canSeeSky'] == 'true':
            condition['canSeeSky'] = True
        elif row['canSeeSky'] == 'false':
            condition['canSeeSky'] = False

        if pd.notna(row['Biome']):
            parsed_biomes = parse_biomes(row['Biome'], invalid_biome_tags)
            if parsed_biomes:
                condition['biomes'] = parsed_biomes

        # check if Time column is day or night or any
        if pd.notna(row['Time']):
            if row['Time'].lower() == 'day':
                condition['timeRange'] = row['Time'].lower()
            elif row['Time'].lower() == 'night':
                condition['timeRange'] = row['Time'].lower()

        # check if Weather column is clear or rain or any
        if pd.notna(row['Weather']):
            if row['Weather'].lower() == 'clear':
                condition['isRaining'] = False
            elif row['Weather'].lower() == 'rain':
                condition['isRaining'] = True

        # Requirements
        if pd.notna(row['Requirements']):
            # split by "," and then strip each string
            strings = str(row['Requirements']).split(',')
            for string in strings:
                s = string.strip()
                # if the string is "Desert Well" then add structure = "minecraft:desert_well" to the condition dictionary
                if s == "Desert Well":
                    condition['structures'] = ["minecraft:desert_well"]
                # if the string contains ":" then use it as is and add it to the condition dictionary under "neededBaseBlocks"
                elif ":" in s:
                    # split the string by " " and check if the first string is "on"
                    if s.split(' ')[0] == "on":
                        if 'neededBaseBlocks' not in condition:
                            condition['neededBaseBlocks'] = []
                        condition['neededBaseBlocks'].append(s.split(' ')[1].strip())
                    else:
                        if 'neededNearbyBlocks' not in condition:
                            condition['neededNearbyBlocks'] = []
                        condition['neededNearbyBlocks'].append(s)
                # if the string contains "minY = " then split it and add it to the condition dictionary
                elif "minY = " in string:
                    condition['minY'] = int(string.split('=')[1].strip())
                # if the string contains "maxY = " then split it and add it to the condition dictionary
                elif "maxY = " in string:
                    condition['maxY'] = int(string.split('=')[1].strip())
                # if the string is "Full Moon" then add moonPhase = 0 to the condition dictionary
                elif string == "Full Moon":
                    condition['moonPhase'] = "0"
                # if the string contains "moonPhase =" then split it and look at the next entries in strings that are a number
                elif "moonPhase = " in string:
                    # get the first integer in the string
                    digitsArray = [int(string.split('=')[1].strip())]
                    # get the index of the current string
                    index = strings.index(string)
                    # get the next strings that are a number and add them to the digitsArray
                    while index < len(strings) - 1:
                        index += 1
                        if strings[index].strip().isdigit():
                            digitsArray.append(int(strings[index].strip()))
                        else:
                            break
                    # add the digitsArray to the condition dictionary in the format moonPhase = "1,2,4"
                    condition['moonPhase'] = ','.join(str(x) for x in digitsArray)
                # if the string contains "maxLight = " then split it and add it to the condition dictionary
                elif "maxLight = " in string:
                    condition['maxLight'] = int(string.split('=')[1].strip())
                # if the string contains "minLight = " then split it and add it to the condition dictionary
                elif "minLight = " in string:
                    condition['minLight'] = int(string.split('=')[1].strip())
                # if the string is a digit, pass (was already hanlded by other conditions)
                elif string.strip().isdigit():
                    pass
                else:
                    print(f"Unknown requirement: {string}")

        if condition:
            spawn_data['condition'] = condition

        # Anticondition field
        anticondition = {}

        if pd.notna(row['Excluded']):
            parsed_biomes = parse_biomes(row['Excluded'], invalid_biome_tags)
            if parsed_biomes:
                anticondition['biomes'] = parsed_biomes

        # Check Prohibitions column, and add the values to the anticondition field.
        if pd.notna(row['Prohibitions']):
            s = str(row['Prohibitions'])
            if ":" in s:
                # split the string by " " and check if the first string is "on"
                if s.split(' ')[0] == "on":
                    if 'neededBaseBlocks' not in anticondition:
                        anticondition['neededBaseBlocks'] = []
                    anticondition['neededBaseBlocks'].append(s.split(' ')[1].strip())
                else:
                    if 'neededNearbyBlocks' not in anticondition:
                        anticondition['neededNearbyBlocks'] = []
                    anticondition['neededNearbyBlocks'].append(s)
            else:
                match s:
                    case "Sempiternal Sanctum":
                        anticondition['structures'] = [f"#the_bumblezone:sempiternal_sanctums"]
                    case "Village":
                        anticondition['structures'] = [f"#minecraft:{row['Prohibitions'].lower()}"]


        if anticondition:
            spawn_data['anticondition'] = anticondition

        # Drops field
        if pd.notna(row['Spawn Specific Drops']):
            # Split the string by = and strip each string
            dropcondtion, unprocessedDrops = row['Spawn Specific Drops'].split('=')[0].strip(), \
                row['Spawn Specific Drops'].split('=')[1].strip()
            # Apply the biome mapping to the condition
            if dropcondtion.lower() in biome_mapping:
                dropcondtion = biome_mapping[dropcondtion.lower()]
            # if the current entry's biome matches with the condition, then add the drops to the json_data
            if dropcondtion in condition['biomes']:
                spawn_data["drops"] = parse_drops(unprocessedDrops)

        # Handle Patternkey=value field and Append the spawn_data to the json_data
        if pd.notna(row['Patternkey=Value']):
            # if it does not contain = then raise an error
            if "=" not in str(row['Patternkey=Value']):
                print(f"Patternkey=Value: {row['Patternkey=Value']}")
                raise ValueError
            else:
                # Initialize an empty array
                specialSpawns = []
                keyValuePairs = str(row['Patternkey=Value']).split(';')

                for string in keyValuePairs:
                    key = string.split('=')[0].strip()
                    value = string.split('=')[1].strip()
                    # split the value by "," and strip each string, remove any empty strings
                    values = [v.strip() for v in value.split(',') if v.strip()]

                    if not specialSpawns:
                        for val in values:
                            copy = spawn_data.copy()
                            # Use val instead of value
                            copy['id'] = '-'.join([spawn_data["pokemon"],  val, copy['id'].split(spawn_data["pokemon"] + '-')[1]] )
                            copy['pokemon'] += f" {key}={val}"
                            specialSpawns.append(copy)
                    else:
                        newSpecialSpawns = []
                        for spawn in specialSpawns:
                            for val in values:
                                copy = spawn.copy()
                                # Only add new key=value if it's not already in the pokemon field
                                if f"{key}={val}" not in copy['pokemon']:
                                    copy['pokemon'] += f" {key}={val}"
                                newSpecialSpawns.append(copy)
                        specialSpawns = newSpecialSpawns
                # add the specialSpawns to the json_data
                if specialSpawns:
                    json_data["spawns"] += specialSpawns
        else:
            json_data["spawns"].append(spawn_data)

    return json_data


def reducePokemonJson(pokemon_json):
    """
    check pokemon_json for spawn entries, where everything ecxept biomes is the same
    if there are multiple entries, then combine them into one entry

    :param pokemon_json: the pokemon json to reduce
    :return: the reduced pokemon_json
    """
    # get the first spawn entry
    first_spawn_entry = pokemon_json["spawns"][0]
    # check if there are more than one spawn entry
    if len(pokemon_json["spawns"]) > 1:
        # loop through the rest of the spawn entries
        for spawn_entry in pokemon_json["spawns"][1:]:
            if len(spawn_entry.keys()) != len(first_spawn_entry.keys()):
                continue
            # check if the spawn entry is the same as the first spawn entry
            all_equal = all(
                spawn_entry[key] == first_spawn_entry[key] for key in spawn_entry.keys() if
                key not in ("condition", "id"))
            if all_equal:
                conditions = spawn_entry["condition"]
                firstCoditions = first_spawn_entry["condition"]
                if len(conditions.keys()) != len(firstCoditions.keys()):
                    continue
                all_same = all(conditions[key] == firstCoditions[key] for key in conditions.keys() if key != "biomes")
                if all_same:
                    # if it is the same, then add the biomes from the spawn entry to the first spawn entry
                    first_spawn_entry["condition"]["biomes"] += spawn_entry["condition"]["biomes"]
                    # remove the spawn entry from the pokemon_json
                    pokemon_json["spawns"].remove(spawn_entry)
    return pokemon_json


def generateID(pokemon_name, entry):
    # remove any ' from the pokemon name
    pokemon_name = pokemon_name.replace("'", "")
    # If the pokemon name contains any of the excluded forms, then remove them
    for form in excluded_forms:
        if form in pokemon_name:
            pokemon_name = pokemon_name.replace(form, "").strip().lower()

    # If the pokemon name contains [], then cut the string at the first space, add a dash,
    # the content between the brackets and add the entry
    if "[" in pokemon_name:
        return f"{sanitize_pokemon(pokemon_name.lower().split(' ')[0])}-{pokemon_name.lower().split('[')[1].split(']')[0]}-{entry}"
    return f"{sanitize_pokemon(pokemon_name)}-{entry}"


def generateName(pokemon_name):
    # If the pokemon name contains any of the excluded forms, then remove them
    for form in excluded_forms:
        if form in pokemon_name:
            pokemon_name = pokemon_name.replace(form, "").strip()
    # If the pokemon name contains [], then remove the []
    if "[" in pokemon_name:
        return f"{sanitize_pokemon(pokemon_name.lower().split(' ')[0])} {pokemon_name.lower().split(' [')[1].split(']')[0]}"
    return sanitize_pokemon(pokemon_name)

def sanitize_pokemon(pokemon):
    return pokemon.replace("-", "").replace("♂", "m").replace("♀", "f").replace(".", "").replace("'", "").replace(' ', '').lower()


def parse_biomes(biomes_str, invalid_biome_tags):
# Use the biome_mapping to convert the Biome column to the format used in the spawn json
    biomes = []
    for biome in biomes_str.split(','):
        biome = biome.lower().strip()
        if biome in biome_mapping:
            # Verify that the biome is not in the invalid_biome_tags list
            if biome_mapping[biome] in invalid_biome_tags:
                raise ValueError(f"Tried to use invalid biome tag: {biome}\nThe wrong tag specified in biome_mapping is: {biome_mapping[biome]}")
            biomes.append(biome_mapping[biome])
        elif biome in ignored_biomes:
            pass
        else:
            raise ValueError(f"Unknown biome: {biome}")
    return biomes


def download_excel_data(spawn_spreadsheet_excel_url, max_retries=5):
    '''Download an excel file from a url and return the data in a csv format'''
    delay = 1
    for attempt in range(max_retries):
        try:
            # Send a GET request to the URL
            response = requests.get(spawn_spreadsheet_excel_url, timeout=10)
            response.raise_for_status()  # This will raise an exception for HTTP errors

            # Read the Excel file into a pandas DataFrame
            with BytesIO(response.content) as excel_file:
                df = pd.read_excel(excel_file, engine='openpyxl', dtype={'Pokémon': str, 'Entry': str, 'No.': int})

            return df
        except requests.RequestException as e:
            if attempt < max_retries - 1:
                time.sleep(delay)
                delay *= 2
            else:
                raise e


def write_to_sqlite(df, db_name, table_name):
    engine = create_engine(f'sqlite:///{db_name}', echo=True)
    df.to_sql(table_name, con=engine, if_exists='replace', index=False)


def save_json_to_file(json_data, file_id, pokemon_name, output_directory):
    # Remove any ' from the pokemon name
    pokemon_name = pokemon_name.replace("'", "")
    # If the pokemon name contains a space, remove everything after the space (because of basculin)
    if " " in pokemon_name:
        pokemon_name = pokemon_name.split(" ")[0]
    filename = f"{str(file_id).zfill(4)}_{pokemon_name.lower()}.json"
    file_path = os.path.join(output_directory, filename)
    with open(file_path, 'w', encoding='utf-8') as file:
        json.dump(json_data, file, indent=4)
    return file_path


def readEnvFile(param):
    with open('.env', 'r', encoding='utf-8') as file:
        for line in file:
            if '=' in line:
                key, value = line.strip().split('=', 1)
                os.environ[key] = value
    return os.environ[param]


def load_special_drops_data():
    drops_df, _a, _b, _c = get_drops_df()
    # delete all rows that have a blank "Spawn Specific Drops" column
    drops_df = drops_df[drops_df['Spawn Specific Drops'].notna()]
    # remove all columns except "Pokémon" and "Spawn Specific Drops"
    drops_df = drops_df[['Pokémon', 'Spawn Specific Drops']]
    return drops_df


def verifyBiomeTags():
    # get the list of all biomes by reading the filenames in common/src/main/resources/data/cobblemon/tags/worldgen/biome/
    biome_files = os.listdir('../common/src/main/resources/data/cobblemon/tags/worldgen/biome/')
    # remove the .json from the filenames and prepend "#cobblemon:" to the biome name
    biome_files = [f"#cobblemon:{file[:-5]}" for file in biome_files]
    unknown_biomes = []
    for biome in biome_mapping.values():
        # if biome starts with #cobblemon: then verify that it is in biome_files
        if biome.startswith("#cobblemon:"):
            if biome not in biome_files:
                unknown_biomes.append(biome)
        elif biome.startswith("minecraft:"):
            # search for the biome folder in all external gradle dependencies: data/minecraft/worldgen/biome
            # if it is not found, then add it to unknown_biomes
            biome_name = biome.split(":")[1]
            biome_file_pattern = f"data/minecraft/worldgen/biome/{biome_name}.json"
            if not is_filepattern_in_jars(biome_file_pattern):
                unknown_biomes.append(biome)
        elif biome.startswith("#minecraft"):
            biome_name = biome.split(":")[1]
            biome_file_pattern = f"data/minecraft/tags/worldgen/biome/{biome_name}.json"
            if not is_filepattern_in_jars(biome_file_pattern):
                unknown_biomes.append(biome)
        else:
            print(f"Unverified biome tag: {biome}")
    if unknown_biomes:
        print("Unknown biomes:")
        print(unknown_biomes)
        return unknown_biomes


def is_filepattern_in_jars(biome_file_pattern):
    # Construct the path pattern for JAR files
    jar_pattern = os.path.join("..", ".gradle", "loom-cache", "minecraftMaven", "net", "minecraft", "*",
                               "*", "*.jar")
    for jar_path in glob.glob(jar_pattern):
        with zipfile.ZipFile(jar_path, 'r') as jar:
            for file in jar.namelist():
                if biome_file_pattern in file:
                    return True
                    break
    return False


# Configuration data
# Read excel url from .env in same directory as this script
spawn_spreadsheet_excel_url = readEnvFile('SPAWN_SPREADSHEET_EXCEL_URL')
pokemon_data_dir = '../common/src/main/resources/data/cobblemon/spawn_pool_world'
sqlite_db_name = 'cobblemon_spawn_data.sqlite'
sqlite_table_name = 'cobblemon_spawns'


if __name__ == "__main__":
    main()
