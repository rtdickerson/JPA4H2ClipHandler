#!/usr/bin/env python3
"""Delete a clipboard item (or all items for a user) from the server.

Usage:
  clipdel.py <username> <name>   # delete one item
  clipdel.py <username>          # clear all items for a user
"""

import argparse
import sys
import urllib.error
import urllib.request


def main() -> None:
    parser = argparse.ArgumentParser(description="Delete from clipboard server")
    parser.add_argument("username", help="Owner username")
    parser.add_argument("name", nargs="?",
                        help="Item name (omit to clear ALL items for this user)")
    parser.add_argument("--server", "-s", default="http://localhost:8080",
                        help="Server base URL (default: http://localhost:8080)")
    args = parser.parse_args()

    base = args.server.rstrip("/")
    url = f"{base}/api/clipboard/{args.username}/{args.name}" if args.name \
        else f"{base}/api/clipboard/{args.username}"

    req = urllib.request.Request(url, method="DELETE")
    try:
        with urllib.request.urlopen(req) as resp:
            resp.read()
        if args.name:
            print(f"Deleted: {args.username}/{args.name}", file=sys.stderr)
        else:
            print(f"Cleared all items for: {args.username}", file=sys.stderr)
    except urllib.error.HTTPError as exc:
        print(f"Error {exc.code}: {exc.reason}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()
