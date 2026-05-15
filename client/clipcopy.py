#!/usr/bin/env python3
"""Copy content to the clipboard server.

Usage:
  clipcopy.py <username> <name>                  # read from stdin
  clipcopy.py <username> <name> --text "hello"   # literal text
  clipcopy.py <username> <name> --file path/to/file  # file (base64-encoded)
"""

import argparse
import base64
import json
import sys
import urllib.error
import urllib.request


def main() -> None:
    parser = argparse.ArgumentParser(description="Copy to clipboard server")
    parser.add_argument("username", help="Owner username")
    parser.add_argument("name", help="Clipboard item name")
    source = parser.add_mutually_exclusive_group()
    source.add_argument("--file", "-f", metavar="PATH",
                        help="File to copy (stored as base64 on server)")
    source.add_argument("--text", "-t", metavar="TEXT",
                        help="Literal text to copy")
    parser.add_argument("--server", "-s", default="http://localhost:8080",
                        help="Server base URL (default: http://localhost:8080)")
    args = parser.parse_args()

    if args.file:
        with open(args.file, "rb") as fh:
            content = base64.b64encode(fh.read()).decode("ascii")
        is_file = True
    elif args.text is not None:
        content = args.text
        is_file = False
    else:
        content = sys.stdin.read()
        is_file = False

    url = f"{args.server.rstrip('/')}/api/clipboard/{args.username}/{args.name}"
    body = json.dumps({"content": content, "file": is_file}).encode("utf-8")
    req = urllib.request.Request(url, data=body, method="PUT",
                                 headers={"Content-Type": "application/json"})
    try:
        with urllib.request.urlopen(req) as resp:
            resp.read()
        print(f"Saved: {args.username}/{args.name}", file=sys.stderr)
    except urllib.error.HTTPError as exc:
        print(f"Error {exc.code}: {exc.reason}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
