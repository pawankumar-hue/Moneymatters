import sqlite3

try:
    conn = sqlite3.connect(r"g:\mm app\.freebuff\desktop-v2.db")
    cursor = conn.cursor()
    cursor.execute("PRAGMA table_info(messages);")
    columns = cursor.fetchall()
    print("Columns:", [c[1] for c in columns])
    
    # Let's do a search based on text
    text_col = None
    for c in columns:
        if c[1].lower() in ['text', 'content', 'body']:
            text_col = c[1]
            break
            
    if text_col:
        cursor.execute(f"SELECT * FROM messages LIMIT 1;")
        row = cursor.fetchone()
        if row:
            print("Sample row:", str(row)[:200])
            
        cursor.execute(f"SELECT {text_col} FROM messages WHERE {text_col} LIKE '%module_%' LIMIT 5;")
        rows = cursor.fetchall()
        print(f"Found {len(rows)} messages containing 'module_'.")
        for r in rows:
            print("Message snippet:", r[0][:200])
            print("=" * 40)
            
    conn.close()
except Exception as e:
    print("Error:", e)
