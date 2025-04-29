# RedisLite - A Simplified Redis Server in Java

RedisLite is a minimal Redis-like server built from scratch in Java for learning purposes. It supports basic Redis operations using the RESP (Redis Serialization Protocol) format, including `SET` and `GET` commands.

---

## Features

- **RESP Protocol Support:** Parses and responds to clients using Redis' wire protocol.
- **Basic Commands:**
  - `SET key value` — Stores a key-value pair.
  - `GET key` — Retrieves the value associated with a key.
- **In-Memory Data Store:** Simple and efficient key-value storage using Java's `HashMap`.
- **RDB Persistence:** Supports saving and loading the datastore from an RDB file to persist data across server restarts.
- **Event-Loop Based Server:** Single-threaded event-driven architecture.
- **Socket Communication:** Custom TCP server and client built using Java Sockets.
- **Clean Client-Server Architecture:** Separate encoder/decoder for RESP messages.
- **Expiration Support:** SET command supports key expiration using EX (seconds) and PX (milliseconds).

---

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven (for building the project)

### Cloning the Repository

```bash
https://github.com/your-github-username/RedisLite.git
cd RedisLite
```

### Building the Project

```bash
mvn clean install
```

### Running the Server

```bash
java -jar target/redis-lite-server.jar
```

The server listens on `localhost:6379` by default.

### Running the Client

```bash
java -jar target/redis-lite-client.jar
```

You can now type Redis-like commands (`SET`, `GET`, `EXIT`) into the client!

---

## Project Structure

```
RedisLite/
|➜ src/main/java/
   |➜ server/ (Server logic)
   |➜ client/ (Client logic)
   |➜ protocol/ (RESP Encoder/Decoder)
   |➜ datastore/ (In-Memory Key-Value Store)
   |➜ persistence/ (RDB Snapshot Logic)
```

---

## Key Components:

```
DataStore: Manages the in-memory data (key-value pairs).
ValueWrapper: Wraps the values to handle expiration times.
RDBPersistenceManager: Manages saving and loading the datastore to/from an RDB file.
RedisServer: Listens for client commands and processes them.
RedisCommandHandler: Handles parsing and execution of Redis-like commands.
ConfigurationManager: Loads configuration settings like RDB file paths.

```
---

## Commands Supported

- `SET key value`
  - Example: `SET name Umang`
  - Response: `+OK`

- `GET key`
  - Example: `GET name`
  - Response: Bulk String containing the value, or `(nil)` if the key does not exist.

- `EXIT`
  - Terminates the client connection gracefully.

---

## Upcoming Features (WIP)

- `EXPIRE key seconds` — Add TTL (Time To Live) for keys.
- `TTL key` — Check time to live for a key.
- Advanced error handling.
- Background cleaning of expired keys.

---

## Contributing

This project is a personal learning journey! Feel free to fork it, raise issues, suggest features, or just build your own cool Redis clone!

---

## License

MIT License — Use it, modify it, learn from it! 🚀

---

## Author

Made with ❤️ by Umang Upadhayay

LinkedIn: [Umang Kumar Upadhayay](https://www.linkedin.com/in/umang-kumar-upadhayay-39121b96/)

---

**Happy Hacking!** 🚀

