import os
import re

VERIFIED_DIR = r"g:\mm app\verified modules"

def report_table_issues():
    report = []
    
    # Walk through all directories
    for root, dirs, files in os.walk(VERIFIED_DIR):
        for file in files:
            if file.endswith('.md'):
                filepath = os.path.join(root, file)
                
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        lines = f.readlines()
                    
                    issues = []
                    for idx, line in enumerate(lines):
                        # Simple heuristic: line has pipe but doesn't look like a valid table row
                        if "|" in line:
                            # A table row should usually have at least two pipes
                            if line.count("|") < 2:
                                issues.append((idx + 1, line.strip()))
                    
                    if issues:
                        report.append({
                            "file": os.path.relpath(filepath, VERIFIED_DIR),
                            "issues": issues
                        })
                except Exception as e:
                    print(f"Error reading {filepath}: {e}")
    
    return report

def main():
    table_issues = report_table_issues()
    
    with open("table_formatting_report.txt", "w", encoding="utf-8") as f:
        f.write("=== TABLE FORMATTING ISSUES REPORT ===\n\n")
        for entry in table_issues:
            f.write(f"File: {entry['file']}\n")
            for line_num, content in entry['issues']:
                f.write(f"  Line {line_num}: {content}\n")
            f.write("\n")
            
    print(f"Report generated: table_formatting_report.txt. Found issues in {len(table_issues)} files.")

if __name__ == "__main__":
    main()
