#!/usr/bin/env python3
"""Convert between Python-format and Java-format theme JSON."""
import argparse, json, math, sys

NCURSES_COLORS = {
    "BLACK": (0, 0, 0), "RED": (255, 0, 0), "GREEN": (0, 255, 0),
    "YELLOW": (255, 255, 0), "BLUE": (0, 0, 255), "MAGENTA": (255, 0, 255),
    "CYAN": (0, 255, 255), "WHITE": (255, 255, 255),
}


def rgb_to_ncurses(rgb):
    """Find nearest ncurses color using Euclidean distance in RGB space."""
    r, g, b = rgb
    best, best_d = "WHITE", float("inf")
    for name, (cr, cg, cb) in NCURSES_COLORS.items():
        d = math.sqrt((r - cr) ** 2 + (g - cg) ** 2 + (b - cb) ** 2)
        if d < best_d:
            best, best_d = name, d
    return best


def ncurses_to_rgb(name):
    return list(NCURSES_COLORS.get(name.upper(), (255, 255, 255)))


def borders_py2java(c):
    """Python (TL,T,TR,L,R,BL,B,BR) -> Java (TL,T,TR,L,BL,B,BR,R)."""
    return c[0:4] + c[5] + c[6] + c[7] + c[4] if len(c) == 8 else c


def borders_java2py(c):
    """Java (TL,T,TR,L,BL,B,BR,R) -> Python (TL,T,TR,L,R,BL,B,BR)."""
    return c[0:4] + c[7] + c[4] + c[5] + c[6] if len(c) == 8 else c


def convert_pair(pair, fn):
    """Convert fg/bg in a color pair dict."""
    return {
        "fg": fn(pair["fg"]) if isinstance(pair["fg"], (list, str)) else pair["fg"],
        "bg": fn(pair["bg"]) if isinstance(pair["bg"], (list, str)) else pair["bg"],
    }


def _copy_fields(theme, result, *keys):
    for k in keys:
        if k in theme:
            result[k] = theme[k]


def convert_to_java(theme):
    """Convert Python-format theme to Java format."""
    result = {}
    _copy_fields(theme, result, "name")
    result["version"] = "1.0"
    _copy_fields(theme, result, "description", "metadata")
    # Colors: components (RGB pairs) -> colors (ncurses name pairs)
    components = theme.get("components", theme.get("colors", {}))
    result["colors"] = {n: convert_pair(p, rgb_to_ncurses)
                        for n, p in components.items()
                        if isinstance(p, dict) and "fg" in p}
    # Borders: reorder Python -> Java
    bc = theme.get("border_chars")
    if bc:
        result["borders"] = ({s: borders_py2java(c) for s, c in bc.items()}
                             if isinstance(bc, dict)
                             else {"single": borders_py2java(bc)})
    # 3D properties: rename keys, nest offset
    td = {}
    for py_k, java_k in [("shadow", "shadow_color"), ("highlight", "highlight_color"),
                          ("lowlight", "lowlight_color")]:
        if py_k in theme:
            td[java_k] = convert_pair(theme[py_k], rgb_to_ncurses)
    if "shadow_offset_x" in theme or "shadow_offset_y" in theme:
        td["shadow_offset"] = {"x": theme.get("shadow_offset_x", 0),
                               "y": theme.get("shadow_offset_y", 0)}
    if "rendering_style" in theme:
        td["rendering_style"] = theme["rendering_style"]
    if td:
        result["3d"] = td
    return result


def convert_to_python(theme):
    """Convert Java-format theme to Python format."""
    result = {}
    _copy_fields(theme, result, "name", "description", "metadata")
    # Colors: ncurses name pairs -> semantic dict + component RGB pairs
    semantic, components = {}, {}
    for comp, pair in theme.get("colors", {}).items():
        if not (isinstance(pair, dict) and "fg" in pair):
            continue
        fg_rgb = ncurses_to_rgb(pair["fg"]) if isinstance(pair["fg"], str) else pair["fg"]
        bg_rgb = ncurses_to_rgb(pair["bg"]) if isinstance(pair["bg"], str) else pair["bg"]
        components[comp] = {"fg": fg_rgb, "bg": bg_rgb}
        for key in ("fg", "bg"):
            if isinstance(pair[key], str):
                semantic[pair[key].lower()] = ncurses_to_rgb(pair[key])
    result["colors"] = semantic
    result["components"] = components
    # Borders: reorder Java -> Python
    borders = theme.get("borders", {})
    if borders:
        result["border_chars"] = (borders_java2py(list(borders.values())[0]) if len(borders) == 1
                                  else {s: borders_java2py(c) for s, c in borders.items()})
    # 3D properties: rename keys, flatten offset
    td = theme.get("3d", {})
    for java_k, py_k in [("shadow_color", "shadow"), ("highlight_color", "highlight"),
                          ("lowlight_color", "lowlight")]:
        if java_k in td:
            result[py_k] = convert_pair(td[java_k], ncurses_to_rgb)
    offset = td.get("shadow_offset")
    if offset:
        result["shadow_offset_x"] = offset.get("x", 0)
        result["shadow_offset_y"] = offset.get("y", 0)
    if "rendering_style" in td:
        result["rendering_style"] = td["rendering_style"]
    return result


def detect_format(theme):
    """Auto-detect theme format. Returns 'java' or 'python'."""
    if "components" in theme:
        return "python"
    colors = theme.get("colors", {})
    for val in colors.values():
        if isinstance(val, dict) and "fg" in val:
            return "java" if isinstance(val["fg"], str) else "python"
        if isinstance(val, list):
            return "python"
    if "border_chars" in theme:
        return "python"
    if "borders" in theme:
        return "java"
    return "java"


def main():
    parser = argparse.ArgumentParser(
        description="Convert between Python and Java theme JSON formats"
    )
    group = parser.add_mutually_exclusive_group()
    group.add_argument("--to-java", action="store_true",
                       help="Convert Python format to Java format")
    group.add_argument("--to-python", action="store_true",
                       help="Convert Java format to Python format")
    parser.add_argument("input", nargs="?",
                        help="Input JSON file (reads stdin if omitted)")
    args = parser.parse_args()

    if args.input:
        with open(args.input) as f:
            theme = json.load(f)
    else:
        theme = json.load(sys.stdin)

    if args.to_java:
        result = convert_to_java(theme)
    elif args.to_python:
        result = convert_to_python(theme)
    else:
        fmt = detect_format(theme)
        result = convert_to_python(theme) if fmt == "java" else convert_to_java(theme)

    json.dump(result, sys.stdout, indent=2, ensure_ascii=False)
    print()


if __name__ == "__main__":
    main()
