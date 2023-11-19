import os
import time
import requests
import pandas as pd
import json
from io import StringIO
from sqlalchemy import create_engine
from tqdm import tqdm


# Initialize lists for report
no_drops_base_forms = []
removed_drops_pokemon = []
no_form_entry_files = []
no_drops_forms = []


def main():
    """
    The drops_csv to json script does the following:
    1. Retrieves the DataFrame containing the drops data.
    2. Filters the filenames by Pokémon names contained in drops df.
    3. Modifies the files based on the drops data.
    4. Writes the DataFrame to an SQLite database.
    """
    # Retrieve the DataFrame containing the drops data
    drops_df, pokemon_data_dir, _sqlite_db_name, _sqlite_table_name = get_drops_df()

    # Filter the filenames by Pokémon names
    files_to_change = filter_filenames_by_pokemon_names(pokemon_data_dir, drops_df['Pokémon'])

    # Modify the files based on the drops data
    for file in tqdm(files_to_change, bar_format='\033[92m' + 'Processing: ' + '{l_bar}\033[0m{bar:96}\033[92m{r_bar}\033[0m',
                     colour='blue'):
        modify_files(file, pokemon_data_dir, drops_df)

    # Write the DataFrame to an SQLite database
    # write_to_sqlite(drops_df, _sqlite_db_name, _sqlite_table_name)

    # Print report
    if no_drops_base_forms:
        print("\nNo drops specified for base forms in the drops sheet, but species files exists:")
        print(no_drops_base_forms)
    if no_drops_forms:
        print("\nNo drops specified for forms in the drops sheet, but form entries exist:")
        print(no_drops_forms)
    if removed_drops_pokemon:
        print("\nRemoved drops specified for Pokémon in the drops sheet:")
        print(removed_drops_pokemon)
    if no_form_entry_files:
        print("\nNo form entry found for the following files, but form drops were specified in the drops sheet:")
        print(no_form_entry_files)



def get_drops_df():
    drops_spreadsheet_csv_url = 'https://docs.google.com/spreadsheets/d/e/2PACX-1vR51bmzKMTvCfa1UKf454nnlNBCUVMtVNQvxdAiYU09E5pWS7mbsrVt45ABsCGZTByt9N_YEgnSwj8V/pub?gid=0&single=true&output=csv'
    conversion_csv_url = 'https://docs.google.com/spreadsheets/d/e/2PACX-1vRmvHzUc6_UUKbcvRche7AVebNoljqC1bf3iccusJqW9-C3k0KtESJxOCXShykSejIarAB2jHJ2bHCb/pub?gid=0&single=true&output=csv'
    pokemon_data_dir = '../common/src/main/resources/data/cobblemon/species'
    sqlite_db_name = 'pokemon_drops_data.sqlite'
    sqlite_table_name = 'pokemon_drops'
    # Download the CSV from the Google Spreadsheet
    csv_data = download_spreadsheet_data(drops_spreadsheet_csv_url)
    csv_data_for_matching = download_spreadsheet_data(conversion_csv_url)
    # Load the data into a DataFrame
    drops_df = load_data_from_csv(csv_data)
    mapping_df = load_data_from_csv(csv_data_for_matching)
    # Create a mapping dictionary from the Pokémon names to the Minecraft IDs
    mapping_dict = dict(zip(mapping_df['natural_name'], mapping_df['minecraft_ID']))
    # Replace the Item names with the Minecraft IDs
    drops_df['Drops'] = drops_df['Drops'].apply(lambda x: replace_names_in_string(x, mapping_dict))
    # Do the same for the "Spawn Specific Drops" column
    drops_df['Spawn Specific Drops'] = drops_df['Spawn Specific Drops'].apply(lambda x: replace_names_in_string(x, mapping_dict))
    return drops_df, pokemon_data_dir, sqlite_db_name, sqlite_table_name


def modify_files(file, pokemon_data_dir, drops_df):
    with open(pokemon_data_dir + "/" + file, 'r', encoding="utf8") as f:
        data = json.load(f)

    with open(pokemon_data_dir + "/" + file, 'r', encoding="utf8") as f:
        data = json.load(f)

    for _, row in drops_df.iterrows():
        pokemon = sanitize_pokemon(row['Pokémon'].split("[")[0].strip())
        pokemon_form = row['Pokémon'].split("[")[1].split("]")[0].strip() if "[" in row['Pokémon'] else None
        if pokemon_form is None:
            if pokemon == file.split('/')[-1][:-5].lower():
                if pd.isna(row['Drops']) or row['Drops'] == '':
                    no_drops_base_forms.append(file)
                    data.pop('drops', None)
                elif "REMOVED" in row['Drops']:
                    removed_drops_pokemon.append(file)
                    data.pop('drops', None)
                else:
                    data['drops'] = parse_drops(row['Drops'])
        else:
            if pokemon == file.split('/')[-1][:-5].lower():
                if 'forms' in data:
                    form_found = False
                    for form in data['forms']:
                        if form['name'] == pokemon_form:
                            form_found = True
                            if pd.isna(row['Drops']) or row['Drops'] == '':
                                no_drops_forms.append(f'{pokemon} [{pokemon_form}]')
                                form.pop('drops', None)
                            elif "REMOVED" in row['Drops']:
                                removed_drops_pokemon.append(file)
                                form.pop('drops', None)
                            else:
                                form['drops'] = parse_drops(row['Drops'])
                            if 'drops' in form and 'drops' in data:
                                if form['drops'] == data['drops']:
                                    form.pop('drops')
                    if not form_found:
                        no_form_entry_files.append(f'{pokemon} [{pokemon_form}]')
                else:
                    no_form_entry_files.append(f'{pokemon} [{pokemon_form}]')

    with open(pokemon_data_dir + "/" + file, 'w', encoding="utf8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)


def parse_drops(drops_str):
    entries = []
    drops_parts = drops_str.split(', ')
    amount = 0  # Calculate the max amount of drops possible (if everything rolls max)
    no_or = "OR" not in drops_str

    if not no_or:
        drops_parts = drops_str.split(" OR ")
        amount = 1

    for part in drops_parts:
        item_info = part.split(' ')
        item_id = item_info[0]

        if "minecraft:" not in item_id and "cobblemon:" not in item_id:
            print("Item ID: " + item_id)

        current_drop = {"item": item_id}
        quantity_range_present = False

        # Iterate over remaining item info fields and add their values to the currentDrop
        for i in range(1, len(item_info)):
            if '-' in item_info[i]:
                if no_or:
                    amount += (int(item_info[i].split('-')[1]))
                quantity_range = item_info[i]
                current_drop.update({"quantityRange": quantity_range})
                quantity_range_present = True
            elif "%" in item_info[i]:
                percentage = float(item_info[i].replace('%', ''))
                current_drop.update({"percentage": percentage})
                if no_or and not quantity_range_present:
                    amount += 1
            elif '(Nether)' in item_info[i] or '(End)' in item_info[i] or '(Overworld)' in item_info[i] or item_info[i] == '':
                pass
            else:
                quantity = item_info[i]
                if quantity != "1":
                    current_drop.update({"quantityRange": quantity})
                if no_or:
                    amount += (int(item_info[i]))

        if len(item_info) == 1 and no_or:
            amount += 1

        entries.append(current_drop)

    return {
        "amount": amount,
        "entries": entries
    }


def replace_names_in_string(drop_str, mapping_dict):
    for natural_name, minecraft_id in mapping_dict.items():
        if pd.isna(drop_str):
            break
        drop_str = drop_str.replace(natural_name, minecraft_id)
    return drop_str


def download_spreadsheet_data(url, max_retries=5):
    delay = 1
    for attempt in range(max_retries):
        try:
            response = requests.get(url, timeout=10)
            response.raise_for_status()
            return response.content.decode('utf-8')
        except requests.RequestException as e:
            if attempt < max_retries - 1:
                time.sleep(delay)
                delay *= 2
            else:
                raise e


def load_data_from_csv(csv_data):
    return pd.read_csv(StringIO(csv_data), encoding='utf8', engine='python', dtype={'Pokémon': str, 'Drops': str})


def filter_filenames_by_pokemon_names(directory, pokemon_names):
    # Apply the sanitize_pokemon function to the pokemon_names
    pokemon_names = pokemon_names.apply(sanitize_pokemon)

    # Get list of subdirectories in the provided directory
    subdirectories = [d for d in os.listdir(directory) if os.path.isdir(os.path.join(directory, d))]

    all_files = []
    for subdir in subdirectories:
        # List all files from the subdirectory
        files_in_subdir = os.listdir(os.path.join(directory, subdir))
        # Extend the all_files list with these files
        # While adding, prepend the subdirectory name
        all_files.extend([f"{subdir}/{file}" for file in files_in_subdir])

    filtered_files = [file for file in all_files if
                      file.split('/')[-1][:-5].lower() in pokemon_names.str.lower().tolist()]

    # Get the files that did not pass the filter
    not_filtered_files = [file for file in all_files if file not in filtered_files]

    print("\nSpecies file found, but ignored:  [[located at resources/data/cobblemon/species/]]")
    print(not_filtered_files)
    print('\n')
    return filtered_files


def write_to_sqlite(df, db_name, table_name):
    engine = create_engine(f'sqlite:///{db_name}', echo=True)
    df.to_sql(table_name, con=engine, if_exists='replace', index=False)


def sanitize_pokemon(pokemon):
    return pokemon.replace("-", "").replace("♂", "m").replace("♀", "f").replace(".", "").replace("'", "").replace(' ', '').replace('é', 'e').replace(':', '').lower()


if __name__ == "__main__":
    main()
