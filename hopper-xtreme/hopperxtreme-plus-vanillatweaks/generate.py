"""Regenerates this resource pack from the mod's current hopper models and the
bundled VanillaTweaks arrow textures. Run from anywhere: python generate.py"""
import json, glob, os, shutil

out = os.path.dirname(os.path.abspath(__file__))
root = os.path.dirname(out)
src = os.path.join(root, 'common/src/main/resources/assets/hopperxtreme/models/block')
shutil.rmtree(out + '/assets/hopperxtreme', ignore_errors=True)
os.makedirs(out + '/assets/hopperxtreme/models/block')

for f in sorted(glob.glob(src + '/*_hopper_side.json')):
    m = json.load(open(f))
    m['textures']['inside_arrow'] = 'minecraft:block/hopper_inside_arrow'
    m['textures']['side_arrow'] = 'minecraft:block/hopper_outside_arrow'
    top = walls = 0
    for e in m['elements']:
        fc = e['faces']
        if e['from'] == [0, 10, 0] and e['to'] == [16, 11, 16] and fc.get('up', {}).get('texture') == '#inside':
            fc['up']['texture'] = '#inside_arrow'
            top += 1
        elif e['from'] in ([0, 11, 0], [14, 11, 0]) and e['to'][1] == 15 and e['to'][2] == 16:
            walls += 1
            if fc['east']['texture'] == '#side':
                fc['east']['texture'] = '#side_arrow'
            if fc['west']['texture'] == '#side':
                fc['west']['texture'] = '#side_arrow'
                fc['west']['uv'] = [16, 1, 0, 5]
    assert walls == 2 and top == (0 if 'honey_glazed' in f else 1), (f, walls, top)
    with open(out + '/assets/hopperxtreme/models/block/' + os.path.basename(f), 'w', newline='\n') as o:
        json.dump(m, o, indent='\t')
        o.write('\n')
with open(out + '/pack.mcmeta', 'w', newline='\n') as o:
    o.write('''{
	"pack": {
		"min_format": 84,
		"max_format": 97,
		"description": "Hopper X-Treme + Vanilla Tweaks Compat\\nAdds Vanilla Tweaks hopper arrows to Hopper X-Treme hoppers"
	}
}
''')
