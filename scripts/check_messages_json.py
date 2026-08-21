import sqlite3
import json

try:
    conn = sqlite3.connect(r"g:\mm app\.freebuff\desktop-v2.db")
    cursor = conn.cursor()
    
    # Searching within parts_json for "module_"
    cursor.execute("SELECT parts_json FROM messages WHERE parts_json LIKE '%module_%' LIMIT 10;")
    rows = cursor.fetchall()
    
    print(f"Found {len(rows)} messages containing 'module_' in parts_json.")
    for r in rows:
        # parts_json is likely a string containing JSON
        print(f"Snippet: {r[0][:200]}")
        print("-" * 50)
            
    conn.close()
except Exception as e:
    print("Error:", e)
