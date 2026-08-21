import sqlite3

try:
    conn = sqlite3.connect(r"g:\mm app\.freebuff\desktop-v2.db")
    cursor = conn.cursor()
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table';")
    tables = cursor.fetchall()
    print("Tables:", tables)
    conn.close()
except Exception as e:
    print("Error:", e)
