import json
import os
from io import StringIO
import pandas as pd
from mutagen.oggvorbis import OggVorbis
from cobblemon_drops_csv_to_json import download_spreadsheet_data
from cobblemon_spawn_csv_to_json import sanitize_pokemon
from scriptutils import printCobblemonHeader, print_list_filtered, print_cobblemon_script_description, \
    print_cobblemon_script_footer, print_problems_and_paths, print_warning

# Download the CSV file
ASSETS_CSV_URL = "https://docs.google.com/spreadsheets/d/e/2PACX-1vSTisDTkJvV0GzKV1zKjAPdMAQAO7znWxjEWXrM1gZPUVmsTU91oy54aJGMpbbvOqAOg03ER1wl7eeA/pub?gid=0&single=true&output=csv"


def main(print_missing_models=False, print_missing_animations=False):
    """
    cobblemon_cry_checker.py checks the consistency between the cry status of Pokémon in a CSV file and the actual
    presence of their cry audio files in a specific directory. It also checks for the presence of the corresponding
    model and animation files for each Pokémon. The function prints out any inconsistencies it finds.

    Parameters:
    print_missing_models (bool): If set to True, the function will print out the names of Pokémon for which model files are missing.
    print_missing_animations (bool): If set to True, the function will print out the names of Pokémon for which animation files are missing.

    Author: Waldleufer
    """

    printCobblemonHeader()

    scriptname = "Cobblemon Cry Checker"
    scriptdescription = "This script checks the consistency between the cry status of Pokémon in a CSV file and the actual presence of their cry audio files in a specific directory. It also checks for the presence of the corresponding model and animation files for each Pokémon. The function prints out any inconsistencies it finds."

    print_cobblemon_script_description(scriptname, scriptdescription)
    # Load the CSV data into a DataFrame
    csv_data = download_spreadsheet_data(ASSETS_CSV_URL)
    csv_file = StringIO(csv_data)
    # Load the data into a DataFrame while specifying the types of the columns, skip NaN values
    df = pd.read_csv(csv_file, dtype={0: str, 1: str, 2: str, 53: str}, skip_blank_lines=True)

    # Extract the required columns
    gen_numbers = df.iloc[3:, 0]  # Column A
    dex_numbers = df.iloc[3:, 1]  # Column B
    pokemon_names = df.iloc[3:, 2]  # Column C
    cry_statuses = df.iloc[3:, 53]  # Column BB

    # Initialize lists for false positives and negatives
    false_positives = []
    false_negatives = []
    invalid_model_files = []
    invalid_animation_files = []

    # Iterate over the DataFrame rows
    for pokemon_name, gen_number, dex_number, cry_status in zip(pokemon_names, gen_numbers, dex_numbers, cry_statuses):
        cry_status = str(cry_status).strip()  # remove whitespace
        # make the first letter of the Pokémon name uppercase and remove all spaces
        sanitized_pokemon_name_lower = sanitize_pokemon(pokemon_name).replace("é", "e")
        # if [ ] are present in the name, remove them and anything in between
        sanitized_pokemon_name_lower = sanitized_pokemon_name_lower.split("[")[0]
        sanitized_pokemon_name = sanitized_pokemon_name_lower.capitalize()
        # Construct the path to the audio file
        audio_file_path = f"../common/src/main/resources/assets/cobblemon/sounds/pokemon/{sanitized_pokemon_name_lower}/{sanitized_pokemon_name_lower}_cry.ogg"
        # Construct the path to the model file
        model_file_path = f"../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/gen{int(gen_number)}/{sanitized_pokemon_name}Model.kt"
        # Construct the path to the animation.json file
        dex_number = str(dex_number).zfill(4)  # prepend with 0s to make it 4 digits long
        animation_file_path = f"../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/{dex_number}_{sanitized_pokemon_name_lower.lower()}/{sanitized_pokemon_name_lower.lower()}.animation.json"
        # Check if the audio file exists
        audio_file_exists = os.path.isfile(audio_file_path)

        # Check the cry status and update the lists of false positives and negatives
        if cry_status == "✔" and not audio_file_exists:
            false_positives.append(pokemon_name)
        elif cry_status in ["🛇", "❌"] and audio_file_exists:
            # Check if the file is not a blank audio file
            audio = OggVorbis(audio_file_path)  # Check if the duration is 0
            # Check if the duration is 0
            if audio.info.length > 0.11725:
                # The audio file is not empty
                false_negatives.append(pokemon_name)

        # Check if the Model.kt file exists and contains the required import and cryAnimation override
        if cry_status == "✔":
            if os.path.isfile(model_file_path):
                with (open(model_file_path, 'r', encoding='utf-8') as file):
                    content = file.read()
                    if "import com.cobblemon.mod.common.client.render.models.blockbench.pokemon.CryProvider" not in content:
                        invalid_model_files.append(("Missing import", model_file_path.replace(
                            '../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/',
                            "")))
                    elif f'override val cryAnimation = CryProvider {{ _, _ -> bedrockStateful("{sanitized_pokemon_name_lower}", "cry").setPreventsIdle(false) }}' not in content:
                        if "override val cryAnimation = CryProvider {" not in content or 'bedrockStateful("' not in content:
                            invalid_model_files.append(("Missing cryAnimation override", model_file_path.replace(
                                '../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/',
                                "")))
            else:
                invalid_model_files.append(("Missing Model.kt", model_file_path.replace(
                    '../common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/', "")))

            # Check if the animation.json file exists and contains the correct effect
            if os.path.isfile(animation_file_path):
                try:
                    with open(animation_file_path, 'r', encoding="utf-8") as file:
                        data = json.load(file)
                        # Iterate through all animations and check if the cry effect is present
                        for animation_name, animation_data in data['animations'].items():
                            if animation_name == f"animation.{sanitized_pokemon_name_lower}.cry":
                                # Check if the "sound_effects" field is present
                                if animation_name == f"animation.{sanitized_pokemon_name_lower}.cry":
                                    check_sound_effects(animation_data, sanitized_pokemon_name_lower,
                                                        animation_file_path, invalid_animation_files)
                except json.decoder.JSONDecodeError:
                    invalid_animation_files.append(("Invalid JSON in ", animation_file_path.replace(
                        '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', "")))

    # Check if lists are empty
    if not false_positives and not false_negatives and not invalid_model_files and not invalid_animation_files:
        print("No issues found. All cries are in order! ♪♫")
    else:
        # Print out the lists of false positives and negatives
        if false_positives:
            print("False positives (cry marked as done but no audio file found):")
            print_list_filtered(false_positives)

        if false_negatives:
            print("\nFalse negatives (audio file found but cry marked as not done):")
            print_list_filtered(false_negatives)

        # Print out the lists of invalid Model.kt and animation.json files
        if invalid_model_files:
            print("\nInvalid Model.kt files: [Located in common/src/main/kotlin/com/cobblemon/mod/common/client/render/models/blockbench/pokemon/]")
            if print_missing_models:
                print_problems_and_paths(invalid_model_files)
            else:
                print_warning("[[not showing missing Model.kt files]]")
                print_problems_and_paths(invalid_model_files, "Missing Model.kt")

        if invalid_animation_files:
            print(
                "\nInvalid animation.json files: [Located in common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/]")
            if print_missing_animations:
                print_problems_and_paths(invalid_animation_files)
            else:
                print_warning("[[not showing missing animation.json files]]")
                print_problems_and_paths(invalid_animation_files, "Missing animation.json")

    print_cobblemon_script_footer("Thanks for using the Cobblemon cry checker, provided to you by Waldleufer!")


def check_sound_effects(animation_data, sanitized_pokemon_name_lower, animation_file_path, invalid_animation_files):
    sound_effects = animation_data.get('sound_effects')
    if sound_effects is not None:
        # Iterate over the sound effects
        for _time_point, effect_data in sound_effects.items():
            # Check if the "effect" field is present and has the correct value
            effect = effect_data.get('effect')
            if effect == f"pokemon.{sanitized_pokemon_name_lower}.cry":
                return
        invalid_animation_files.append(("Wrong \"effect\": ... in ", animation_file_path.replace(
            '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', "")))
    else:
        invalid_animation_files.append(("Missing sound_effects in ", animation_file_path.replace(
            '../common/src/main/resources/assets/cobblemon/bedrock/pokemon/animations/', "")))


if (__name__ == "__main__"):
    main()
