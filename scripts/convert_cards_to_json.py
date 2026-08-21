import os
import json
import re

ref_dir = r"g:\mm app\reference\src\data"
out_dir = r"g:\mm app\app\src\main\assets\module_cards"
os.makedirs(out_dir, exist_ok=True)

files = [
    {"modId": 1, "file": "module1Cards.js", "varName": "module1Topics"},
    {"modId": 2, "file": "module10Cards.js", "varName": "module10Topics"},
    {"modId": 3, "file": "module3Cards.js", "varName": "module3Topics"},
    {"modId": 4, "file": "module4Cards.js", "varName": "module4Topics"},
    {"modId": 5, "file": "module6Cards.js", "varName": "module6Topics"},
    {"modId": 6, "file": "module5Cards.js", "varName": "module5Topics"},
    {"modId": 7, "file": "module7Cards.js", "varName": "module7Topics"},
    {"modId": 8, "file": "module8Cards.js", "varName": "module8Topics"},
    {"modId": 9, "file": "module9Cards.js", "varName": "module9Topics"},
    {"modId": 10, "file": "module2Cards.js", "varName": "module2Topics"},
    {"modId": 11, "file": "module11Cards.js", "varName": "module11Topics"}
]

for f in files:
    filepath = os.path.join(ref_dir, f["file"])
    if not os.path.exists(filepath):
        continue
    with open(filepath, 'r', encoding='utf-8') as fp:
        code = fp.read()
    
    node_script = f"""
const fs = require('fs');
let code = fs.readFileSync({json.dumps(filepath)}, 'utf8');
code = code.replace(/import\\s+.*?;/g, '');
code = code.replace(/export\\s+const\\s+/g, 'const ');
code = code.replace(/export\\s+function\\s+[\\s\\S]*$/, '');
code += "\\nmodule.exports = {f['varName']};";

const tmp = {json.dumps(os.path.join(out_dir, f"tmp_{f['modId']}.js"))};
fs.writeFileSync(tmp, code, 'utf8');
const topics = require(tmp);
fs.unlinkSync(tmp);
fs.writeFileSync({json.dumps(os.path.join(out_dir, f"module_{f['modId']}.json"))}, JSON.stringify(topics, null, 2), 'utf8');
"""
    tmp_runner = os.path.join(out_dir, f"runner_{f['modId']}.js")
    with open(tmp_runner, 'w', encoding='utf-8') as rp:
        rp.write(node_script)
    
    res = os.system(f'node "{tmp_runner}"')
    if os.path.exists(tmp_runner):
        os.remove(tmp_runner)
    
    out_file = os.path.join(out_dir, f"module_{f['modId']}.json")
    if os.path.exists(out_file):
        with open(out_file, 'r', encoding='utf-8') as op:
            data = json.load(op)
        total_cards = sum(len(t.get('cards', [])) for t in data)
        print(f"Module {f['modId']}: {len(data)} topics, {total_cards} cards -> module_{f['modId']}.json")
