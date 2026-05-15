# JPA4H2ClipHandler
An api-based clipboard handler for lab networks to get around VMWare's garbage clipboard support.  Clients can push content in, and pull content out, and the web interface shows what is stored.  Simple, no security, just to solve trying to copy/paste code to-from different workstations within your lab.

## Requirements

- Java 17+
- Maven 3.8+
- Python 3.x (clients only, no third-party packages required)

## Build

```bash
mvn package
```

The runnable JAR will be at `target/cliphandler-0.0.1-SNAPSHOT.jar`.

## Run

**From source:**
```bash
mvn spring-boot:run
```

**From the JAR:**
```bash
java -jar target/cliphandler-0.0.1-SNAPSHOT.jar
```

The server starts on port 8080. The H2 database is stored in `./data/cliphandler` (created automatically on first run).

| URL | Purpose |
|-----|---------|
| `http://localhost:8080/` | Web UI — browse and clear clipboard items |
| `http://localhost:8080/h2-console` | H2 database console |
| `http://localhost:8080/api/clipboard` | REST API root |

## Test

```bash
mvn test
```

## Python clients

All four scripts live in `client/` and use only the Python standard library.

| Script | Purpose |
|--------|---------|
| `clipcopy.py` | Copy text or a file to the server |
| `clippaste.py` | Paste content from the server |
| `clipdel.py` | Delete one item or clear all items for a user |
| `cliplist.py` | List stored items |

**Copy text:**
```bash
echo "my snippet" | python3 client/clipcopy.py alice mysnippet
python3 client/clipcopy.py alice mysnippet --text "hello world"
```

**Copy a file (stored base64-encoded on the server):**
```bash
python3 client/clipcopy.py alice myfile --file /path/to/file.pdf
```

**Paste text:**
```bash
python3 client/clippaste.py alice mysnippet
```

**Paste a file back:**
```bash
python3 client/clippaste.py alice myfile --file /tmp/recovered.pdf
```

**List all items (or filter by user):**
```bash
python3 client/cliplist.py
python3 client/cliplist.py alice
```

**Delete an item or clear a user's clipboard:**
```bash
python3 client/clipdel.py alice mysnippet
python3 client/clipdel.py alice          # clears all of alice's items
```

By default all clients connect to `http://localhost:8080`. Use `--server` to point at a different host:

```bash
python3 client/clipcopy.py alice note --text "hi" --server http://192.168.1.50:8080
```

## REST API

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/clipboard` | List all items |
| `GET` | `/api/clipboard/{user}` | List items for a user |
| `GET` | `/api/clipboard/{user}/{name}` | Get a single item (404 if absent) |
| `PUT` | `/api/clipboard/{user}/{name}` | Create or overwrite an item |
| `DELETE` | `/api/clipboard/{user}/{name}` | Delete a single item |
| `DELETE` | `/api/clipboard/{user}` | Clear all items for a user |

**PUT request body:**
```json
{ "content": "your text or base64 here", "file": false }
```
