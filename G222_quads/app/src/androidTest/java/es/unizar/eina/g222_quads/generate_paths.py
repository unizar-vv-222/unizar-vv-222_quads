"""
Generador de caminos de prueba para el grafo de navegación G222
Cobertura de PARES DE ARISTAS (Edge-Pair Coverage / profundidad 2)

Cambios respecto a la versión anterior:
  - Aristas 12 y 13 fusionadas en una sola: quadSaved  (quad_form → quad)
  - Aristas 35 y 36 fusionadas en una sola: reservaSaved (confirm → reserva)
  - Cobertura ampliada de arista simple a PAR de aristas (profundidad 2)
"""

import json
from collections import defaultdict, deque

EDGES = [
    # Navegación principal
    (1,  "main",          "quad",           "quadList"),
    (2,  "main",          "reserva",        "reservaList"),
    # quad
    (3,  "quad",          "quad_detail",    "quadDetail"),
    (4,  "quad",          "quad_form",      "createQuad"),
    (5,  "quad",          "main",           "goBack"),
    (6,  "quad",          "quad",           "ordenarPorMatricula"),
    (7,  "quad",          "quad",           "ordenarPorPrecio"),
    (8,  "quad",          "quad",           "ordenarPorTipo"),
    # quad_detail
    (9,  "quad_detail",   "quad",           "goBack"),
    (10, "quad_detail",   "quad",           "deleteQuad"),
    (11, "quad_detail",   "quad_form",      "editQuad"),
    # quad_form  (12 = fusión quadCreated+quadEdited)
    (12, "quad_form",     "quad",           "quadSaved"),
    (13, "quad_form",     "quad",           "goBack"),
    (14, "quad_form",     "quad",           "cancel"),
    # reserva
    (15, "reserva",       "reserva_detail", "reservaDetail"),
    (16, "reserva",       "reserva_form",   "createReserva"),
    (17, "reserva",       "main",           "goBack"),
    (18, "reserva",       "reserva",        "filtrarVigentes"),
    (19, "reserva",       "reserva",        "filtrarFuturas"),
    (20, "reserva",       "reserva",        "filtrarCaducadas"),
    (21, "reserva",       "reserva",        "ordenarPorNombre"),
    (22, "reserva",       "reserva",        "ordenarPorTelefono"),
    (23, "reserva",       "reserva",        "ordenarPorFechaRecogida"),
    (24, "reserva",       "reserva",        "ordenarPorFechaDevolucion"),
    # reserva_detail
    (25, "reserva_detail","reserva",        "goBack"),
    (26, "reserva_detail","reserva",        "deleteReserva"),
    (27, "reserva_detail","reserva_form",   "editReserva"),
    (28, "reserva_detail","external",       "sendDetails"),
    # reserva_form
    (29, "reserva_form",  "reserva",        "goBack"),
    (30, "reserva_form",  "reserva",        "cancel"),
    (31, "reserva_form",  "select_quads",   "next"),
    # select_quads
    (32, "select_quads",  "reserva",        "goBack"),
    (33, "select_quads",  "confirm",        "next"),
    # confirm  (34 = fusión reservaSaved)
    (34, "confirm",       "reserva",        "reservaSaved"),
    (35, "confirm",       "reserva",        "cancel"),
    (36, "confirm",       "reserva",        "goBack"),
]

START = "main"

def build_graph():
    graph     = defaultdict(list)
    edge_map  = {}
    out_edges = defaultdict(list)
    for eid, src, dst, action in EDGES:
        graph[src].append((dst, eid, action))
        edge_map[eid] = (src, dst, action)
        out_edges[src].append(eid)
    return graph, edge_map, out_edges

def bfs_node_path(graph, start, target):
    if start == target:
        return [start]
    queue   = deque([[start]])
    visited = {start}
    while queue:
        path = queue.popleft()
        for (nbr, eid, _) in graph[path[-1]]:
            if nbr not in visited:
                new_path = path + [nbr]
                if nbr == target:
                    return new_path
                visited.add(nbr)
                queue.append(new_path)
    return None

def node_path_to_edges(node_path, graph):
    edges = []
    for i in range(len(node_path) - 1):
        frm, to = node_path[i], node_path[i+1]
        for (nbr, eid, _) in graph[frm]:
            if nbr == to:
                edges.append(eid)
                break
    return edges

def prefix_edges_to(graph, start, target_node):
    if start == target_node:
        return []
    path = bfs_node_path(graph, start, target_node)
    if path is None:
        return []
    return node_path_to_edges(path, graph)

def generate_edge_pair_paths(graph, edge_map, out_edges, start):
    paths = []
    for e1_id, (src1, dst1, action1) in edge_map.items():
        for e2_id in out_edges.get(dst1, []):
            src2, dst2, action2 = edge_map[e2_id]
            prefix = prefix_edges_to(graph, start, src1)
            full_path = prefix + [e1_id, e2_id]
            paths.append({
                "pair":         (e1_id, e2_id),
                "e1_action":    action1,
                "e2_action":    action2,
                "intermediate": dst1,
                "path_edges":   full_path,
            })
    return paths

def edge_label(eid, edge_map):
    src, dst, action = edge_map[eid]
    return f"{eid}({action})"

def print_paths(paths, edge_map):
    print("=" * 72)
    print("  CAMINOS DE PRUEBA – COBERTURA DE PARES (profundidad 2)")
    print("=" * 72)
    print(f"  Total de pares a cubrir: {len(paths)}")
    print("=" * 72)
    for i, p in enumerate(paths, 1):
        e1, e2 = p["pair"]
        labels = " -> ".join(edge_label(e, edge_map) for e in p["path_edges"])
        print(f"\nTest #{i:>3}  Par ({e1},{e2})  [{p['e1_action']} -> {p['intermediate']} -> {p['e2_action']}]")
        print(f"  IDs   : {' -> '.join(str(e) for e in p['path_edges'])}")
        print(f"  Nombres: {labels}")

def print_switch_table(edge_map):
    print("\n" + "=" * 72)
    print("  TABLA PARA SWITCH EN JAVA")
    print("=" * 72)
    for eid in sorted(edge_map.keys()):
        src, dst, action = edge_map[eid]
        print(f"  case {eid:>2}: // {src} --{action}--> {dst}")

def export_json(paths, edge_map, filename="paths.json"):
    output = []
    for i, p in enumerate(paths, 1):
        e1, e2 = p["pair"]
        output.append({
            "test_id":        i,
            "pair":           [e1, e2],
            "e1_action":      p["e1_action"],
            "e2_action":      p["e2_action"],
            "intermediate":   p["intermediate"],
            "edge_sequence":  p["path_edges"],
            "named_sequence": [edge_map[e][2] for e in p["path_edges"]],
        })
    with open(filename, "w", encoding="utf-8") as f:
        json.dump(output, f, ensure_ascii=False, indent=2)
    print(f"\nExportado a {filename}  ({len(output)} caminos)")

if __name__ == "__main__":
    graph, edge_map, out_edges = build_graph()
    paths = generate_edge_pair_paths(graph, edge_map, out_edges, START)
    print_paths(paths, edge_map)
    print_switch_table(edge_map)
    export_json(paths, edge_map)
    print("\n" + "=" * 72)
    print(f"  Aristas: {len(edge_map)}  |  Pares cubiertos: {len(paths)}")
