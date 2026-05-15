#!/usr/bin/env python3
"""List clipboard items stored on the server.

Usage:
  cliplist.py              # list all items
  cliplist.py <username>   # list items for one user
"""

import argparse
import json
import sys
import urllib.error
import urllib.request


def main() -> None:
    parser = argparse.ArgumentParser(description="List clipboard items")
    parser.add_argument("username", nargs="?", help="Owner username (omit to list all)")
    parser.add_argument("--server", "-s", default="http://localhost:8080",
                        help="Server base URL (default: http://localhost:8080)")
    args = parser.parse_args()

    base = args.server.rstrip("/")
    url = f"{base}/api/clipboard/{args.username}" if args.username \
        else f"{base}/api/clipboard"

    try:
        with urllib.request.urlopen(url) as resp:
            items = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        print(f"Error {exc.code}: {exc.reason}", file=sys.stderr)
        sys.exit(1)

    if not items:
        print("No items found.")
        return

    col_w = max(len(i["username"]) for i in items)
    for item in items:
        kind = "file" if item["file"] else "text"
        updated = item["updatedAt"][:16].replace("T", " ")
        print(f"{item['username']:<{col_w}}  {item['name']:<30}  [{kind}]  {updated}")


if __name__ == "__main__":
    main()
