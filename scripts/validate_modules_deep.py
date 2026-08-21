import os
import re
import json

VERIFIED_DIR = r"g:\mm app\verified modules"

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

def analyze_module_file(filepath, folder_name, filename):
    num_match = re.search(r'module_(\d+)', filename, re.IGNORECASE)
    if not num_match:
        return {"error": "Filename does not match module_(\\d+)"}
    
    mod_id = int(num_match.group(1))
    
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except UnicodeDecodeError as e:
        try:
            with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
                content = f.read()
            encoding_issue = "Had decoding errors, opened with errors ignored."
        except Exception as ex:
            return {"error": f"Failed to read file: {str(ex)}"}
    except Exception as e:
        return {"error": f"Failed to read file: {str(e)}"}
    
    encoding_issue = None

    # 1. H1 title extraction
    lines = content.splitlines()
    title = None
    for line in lines:
        if line.startswith("# "):
            title = line.replace("# ", "").strip()
            break
        elif line.startswith("MODULE ") or line.startswith("Module "):
            title = line.strip()
            break
            
    # 2. Chapter/Heading extraction (mimic package_modules.py but deeper)
    chapters = []
    # Try pattern with double hash or beginning of line
    chapter_matches = re.findall(r'(?:^|\n)(?:##\s*)?(\d+\.\d+\s+[^:\n\r]+)', content)
    for m in chapter_matches:
        ch_title = m.strip()
        if len(ch_title) < 80 and ch_title not in chapters:
            chapters.append(ch_title)

    if not chapters:
        h2_matches = re.findall(r'(?:^|\n)##\s+(.+)', content)
        chapters = [h.strip() for h in h2_matches[:5]]

    # 3. Stats
    char_count = len(content)
    word_count = len(content.split())
    
    # 4. Content issues
    placeholders = []
    lower_content = content.lower()
    for ph in ["todo", "placeholder", "lorem ipsum", "insert translation", "translation missing", "[insert", "write here"]:
        if ph in lower_content:
            placeholders.append(ph)
            
    # Check for unclosed code blocks
    code_block_count = content.count("```")
    unclosed_code_blocks = (code_block_count % 2 != 0)
    
    # Check tables (lines with '|' should have matching pipes and be balanced)
    table_issues = []
    table_lines = [l for l in lines if "|" in l]
    if table_lines:
        # Check if any line has only one pipe or unbalanced pipes
        for idx, tl in enumerate(table_lines):
            pipes = tl.count("|")
            if pipes < 2:
                table_issues.append(f"Line {idx} with low pipe count ({pipes}): {tl[:30]}")

    return {
        "mod_id": mod_id,
        "title": title,
        "chapters": chapters,
        "char_count": char_count,
        "word_count": word_count,
        "placeholders": placeholders,
        "unclosed_code_blocks": unclosed_code_blocks,
        "table_issues_count": len(table_issues),
        "table_issues": table_issues[:3],
        "encoding_issue": encoding_issue
    }

def main():
    print("=" * 80)
    print("STARTING DEEP VALIDATION OF ALL VERIFIED MODULES")
    print("=" * 80)
    
    summary_report = {}
    total_files_checked = 0
    warnings_count = 0
    errors_count = 0
    
    short_modules = []
    missing_titles = []
    missing_chapters = []
    has_placeholders = []
    has_unclosed_code_blocks = []
    has_table_issues = []
    has_errors = []

    for folder_name, lang_code in LANG_MAP.items():
        lang_dir = os.path.join(VERIFIED_DIR, folder_name)
        if not os.path.exists(lang_dir):
            lang_dir = os.path.join(VERIFIED_DIR, folder_name.lower())
        if not os.path.exists(lang_dir):
            print(f"[!] Warning: Folder for {folder_name} ({lang_dir}) does not exist.")
            warnings_count += 1
            continue
            
        print(f"Scanning directory: {folder_name} ({lang_code})")
        summary_report[folder_name] = {
            "total_files": 0,
            "min_words": 999999,
            "max_words": 0,
            "avg_words": 0,
            "total_words": 0,
            "issues": []
        }
        
        files = [f for f in os.listdir(lang_dir) if f.endswith('.md')]
        files.sort()
        
        for filename in files:
            filepath = os.path.join(lang_dir, filename)
            res = analyze_module_file(filepath, folder_name, filename)
            total_files_checked += 1
            
            if "error" in res:
                err_msg = f"{folder_name}/{filename}: ERROR: {res['error']}"
                print(f"  [X] {err_msg}")
                has_errors.append(err_msg)
                errors_count += 1
                summary_report[folder_name]["issues"].append(res["error"])
                continue
                
            summary_report[folder_name]["total_files"] += 1
            w_count = res["word_count"]
            summary_report[folder_name]["total_words"] += w_count
            if w_count < summary_report[folder_name]["min_words"]:
                summary_report[folder_name]["min_words"] = w_count
            if w_count > summary_report[folder_name]["max_words"]:
                summary_report[folder_name]["max_words"] = w_count
                
            # Warnings/checks:
            # Short file (less than 200 words - might be placeholder)
            if w_count < 200:
                msg = f"{folder_name}/{filename} (Module {res['mod_id']}) is very short ({w_count} words)"
                short_modules.append(msg)
                summary_report[folder_name]["issues"].append(msg)
                warnings_count += 1
                
            # Missing Title
            if not res["title"]:
                msg = f"{folder_name}/{filename} has NO title header"
                missing_titles.append(msg)
                summary_report[folder_name]["issues"].append(msg)
                warnings_count += 1
                
            # Missing Chapters
            if not res["chapters"]:
                msg = f"{folder_name}/{filename} has NO extracted chapters"
                missing_chapters.append(msg)
                summary_report[folder_name]["issues"].append(msg)
                warnings_count += 1
                
            # Placeholders
            if res["placeholders"]:
                msg = f"{folder_name}/{filename} contains placeholder keywords: {res['placeholders']}"
                has_placeholders.append(msg)
                summary_report[folder_name]["issues"].append(msg)
                warnings_count += 1
                
            # Unclosed code blocks
            if res["unclosed_code_blocks"]:
                msg = f"{folder_name}/{filename} has unclosed code blocks (unbalanced '```')"
                has_unclosed_code_blocks.append(msg)
                summary_report[folder_name]["issues"].append(msg)
                warnings_count += 1
                
            # Table issues
            if res["table_issues_count"] > 0:
                msg = f"{folder_name}/{filename} has {res['table_issues_count']} potential table layout issues (e.g., unbalanced pipes)"
                has_table_issues.append(msg)
                summary_report[folder_name]["issues"].append(msg)
                warnings_count += 1
                
        if summary_report[folder_name]["total_files"] > 0:
            summary_report[folder_name]["avg_words"] = int(summary_report[folder_name]["total_words"] / summary_report[folder_name]["total_files"])
        else:
            summary_report[folder_name]["min_words"] = 0

    print("\n" + "=" * 80)
    print("DETAILED VERIFICATION REPORT SUMMARY")
    print("=" * 80)
    print(f"Total files analyzed: {total_files_checked}")
    print(f"Total critical errors: {errors_count}")
    print(f"Total warnings/issues found: {warnings_count}")
    print("-" * 80)
    
    if has_errors:
        print("\n[CRITICAL ERRORS]")
        for err in has_errors:
            print(f"  - {err}")
    else:
        print("\n[OK] No critical loading errors.")
        
    if short_modules:
        print(f"\n[WARNING] Very Short Modules (<200 words): {len(short_modules)}")
        for m in short_modules[:10]:
            print(f"  - {m}")
        if len(short_modules) > 10:
            print(f"  ... and {len(short_modules) - 10} more.")
            
    if missing_titles:
        print(f"\n[WARNING] Missing Titles: {len(missing_titles)}")
        for m in missing_titles[:10]:
            print(f"  - {m}")
        if len(missing_titles) > 10:
            print(f"  ... and {len(missing_titles) - 10} more.")
            
    if missing_chapters:
        print(f"\n[WARNING] Missing Chapters: {len(missing_chapters)}")
        for m in missing_chapters[:10]:
            print(f"  - {m}")
        if len(missing_chapters) > 10:
            print(f"  ... and {len(missing_chapters) - 10} more.")
            
    if has_placeholders:
        print(f"\n[WARNING] Contains Placeholder Text: {len(has_placeholders)}")
        for m in has_placeholders[:10]:
            print(f"  - {m}")
        if len(has_placeholders) > 10:
            print(f"  ... and {len(has_placeholders) - 10} more.")
            
    if has_unclosed_code_blocks:
        print(f"\n[WARNING] Unclosed Code Blocks: {len(has_unclosed_code_blocks)}")
        for m in has_unclosed_code_blocks[:10]:
            print(f"  - {m}")
        if len(has_unclosed_code_blocks) > 10:
            print(f"  ... and {len(has_unclosed_code_blocks) - 10} more.")
            
    if has_table_issues:
        print(f"\n[WARNING] Potential Table Issues: {len(has_table_issues)}")
        for m in has_table_issues[:10]:
            print(f"  - {m}")
        if len(has_table_issues) > 10:
            print(f"  ... and {len(has_table_issues) - 10} more.")

    print("\n" + "-" * 80)
    print("LANGUAGE-WISE STATISTICS")
    print("-" * 80)
    for lang, stat in summary_report.items():
        print(f"{lang:12} ({LANG_MAP.get(lang, '??')}): {stat['total_files']} files | Word count (Min: {stat['min_words']}, Max: {stat['max_words']}, Avg: {stat['avg_words']}) | Issues: {len(stat['issues'])}")

if __name__ == "__main__":
    main()
