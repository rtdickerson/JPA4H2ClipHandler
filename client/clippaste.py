#!/usr/bin/env python3
"""Paste content from the clipboard server.

Usage:
  clippaste.py <username> <name>               # print to stdout
  clippaste.py <username> <name> --file out    # write to file (decodes base64 if stored as file)
"""

import argparse
import base64
import json
import sys
import urllib.error
import urllib.request


def main() -> None:
    parser = argparse.ArgumentParser(description="Paste from clipboard server")
    parser.add_argument("username", help="Owner username")
    parser.add_argument("name", help="Clipboard item name")
    parser.add_argument("--file", "-f", metavar="PATH",
                        help="Write output to this file (binary for file items, text otherwise)")
    parser.add_argument("--server", "-s", default="http://localhost:8080",
                        help="Server base URL (default: http://localhost:8080)")
    args = parser.parse_args()

    url = f"{args.server.rstrip('/')}/api/clipboard/{args.username}/{args.name}"
    try:
        with urllib.request.urlopen(url) as resp:
            item = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        if exc.code == 404:
            print(f"Not found: {args.username}/{args.name}", file=sys.stderr)
        else:
            print(f"Error {exc.code}: {exc.reason}", file=sys.stderr)
        sys.exit(1)

    content = item["content"]

    if item["file"]:
        data = base64.b64decode(content)
        if args.file:
            with open(args.file, "wb") as fh:
                fh.write(data)
            print(f"Written {len(data)} bytes to {args.file}", file=sys.stderr)
        else:
            sys.stdout.buffer.write(data)
    else:
        if args.file:
            with open(args.file, "w", encoding="utf-8") as fh:
                fh.write(content)
            print(f"Written to {args.file}", file=sys.stderr)
        else:
            sys.stdout.write(content)


if __name__ == "__main__":
    main()
