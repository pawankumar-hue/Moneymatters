import os
import json
import re

VERIFIED_DIR = r"g:\mm app\verified modules"
OUTPUT_DIR = r"g:\mm app\app\src\main\assets\modules"

LANG_MAP = {
    "English": "en",
    "Hindi": "hi",
    "Hinglish": "hinglish",
    "Bengali": "bn",
    "Bhojpuri": "bho",
    "Gujarati": "gu",
    "Kannada": "kn",
    "Malayalam": "ml",
    "Marathi": "mr",
    "Punjabi": "pa",
    "Tamil": "ta",
    "Telugu": "te"
}

LANG_DISPLAY_NAMES = {
    "en": "English",
    "hi": "Hindi (हिंदी)",
    "hinglish": "Hinglish",
    "bn": "Bengali (বাংলা)",
    "bho": "Bhojpuri (भोजपुरी)",
    "gu": "Gujarati (ગુજરાતી)",
    "kn": "Kannada (કನ್ನಡ)",
    "ml": "Malayalam (മലയാളം)",
    "mr": "Marathi (मराठी)",
    "pa": "Punjabi (ਪੰਜਾਬੀ)",
    "ta": "Tamil (தமிழ்)",
    "te": "Telugu (తెలుగు)"
}

CATEGORIES = [
    "Foundations", "Budgeting & Saving", "Banking & Credit", 
    "Investing & Markets", "Tax & Govt Schemes", "Real World & Career"
]

def get_category(mod_id):
    if mod_id <= 4:
        return "Foundations"
    elif mod_id <= 6:
        return "Budgeting & Saving"
    elif mod_id <= 9:
        return "Banking & Credit"
    elif mod_id <= 12:
        return "Investing & Markets"
    elif mod_id <= 18:
        return "Tax & Govt Schemes"
    else:
        return "Real World & Career"

def parse_module_file(filepath, mod_id, lang_code):
    with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
        content = f.read()

    lines = content.splitlines()
    title = f"Module {mod_id}"
    for line in lines:
        if line.startswith("# "):
            title = line.replace("# ", "").strip()
            break
        elif line.startswith("MODULE ") or line.startswith("Module "):
            title = line.strip()
            break

    # Extract chapters from headings (e.g. ## 1.1 Title or line-starting 1.1 Title)
    chapters = []
    chapter_matches = re.findall(r'(?:^|\n)(?:##\s*)?(\d+\.\d+\s+[^:\n\r]+)', content)
    for m in chapter_matches:
        ch_title = m.strip()
        if len(ch_title) < 80 and ch_title not in chapters:
            chapters.append(ch_title)

    if not chapters:
        h2_matches = re.findall(r'(?:^|\n)##\s+(.+)', content)
        chapters = [h.strip() for h in h2_matches[:5]]

    # Estimate read time (word count / 150)
    word_count = len(content.split())
    read_time = max(3, round(word_count / 150))

    return {
        "id": mod_id,
        "title": title,
        "category": get_category(mod_id),
        "language": lang_code,
        "readTimeMinutes": read_time,
        "wordCount": word_count,
        "xpReward": mod_id * 20 + 50,
        "chapters": chapters[:6],
        "content": content
    }

def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    module_index = []

    for folder_name, lang_code in LANG_MAP.items():
        lang_dir = os.path.join(VERIFIED_DIR, folder_name)
        if not os.path.exists(lang_dir):
            lang_dir = os.path.join(VERIFIED_DIR, folder_name.lower())
        if not os.path.exists(lang_dir):
            continue
        
        target_lang_dir = os.path.join(OUTPUT_DIR, lang_code)
        os.makedirs(target_lang_dir, exist_ok=True)

        files = [f for f in os.listdir(lang_dir) if f.endswith('.md')]
        files.sort()

        for filename in files:
            num_match = re.search(r'module_(\d+)', filename, re.IGNORECASE)
            if not num_match:
                continue
            mod_id = int(num_match.group(1))

            filepath = os.path.join(lang_dir, filename)
            mod_data = parse_module_file(filepath, mod_id, lang_code)

            out_file = os.path.join(target_lang_dir, f"module_{mod_id}.json")
            with open(out_file, 'w', encoding='utf-8') as f:
                json.dump(mod_data, f, ensure_ascii=False, indent=2)

            module_index.append({
                "id": mod_id,
                "title": mod_data["title"],
                "category": mod_data["category"],
                "language": lang_code,
                "readTimeMinutes": mod_data["readTimeMinutes"],
                "xpReward": mod_data["xpReward"],
                "filename": f"{lang_code}/module_{mod_id}.json"
            })

    index_file = os.path.join(OUTPUT_DIR, "index.json")
    with open(index_file, 'w', encoding='utf-8') as f:
        json.dump({
            "languages": LANG_DISPLAY_NAMES,
            "categories": CATEGORIES,
            "modules": module_index
        }, f, ensure_ascii=False, indent=2)

    print(f"Successfully packaged {len(module_index)} module files across {len(LANG_MAP)} languages!")

if __name__ == "__main__":
    main()
