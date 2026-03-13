# 🐳 Docker Deployment Guide — Projects Tracker

**Spring Boot + MySQL + DockerHub**

---

## Overview

This guide covers the full process of dockerizing the Projects Tracker Spring Boot app,
pushing it to DockerHub, and running it locally with persistent data storage.

**Tech stack:**
- App: Spring Boot 3.5 / Java 17
- Database: MySQL 8.0
- Containerization: Docker + Docker Compose
- Image registry: DockerHub

---

## Project File Structure

After setup, the root of the project looks like this:

```
projects-tracker/
├── src/
├── pom.xml
├── Dockerfile           ✅ committed to GitHub (no secrets)
├── docker-compose.yml   ✅ committed to GitHub (no secrets)
├── .env                 ⚠️  gitignored — stays local only, never pushed!
├── .gitignore
└── ...
```

---

## Step 1 — Create DockerHub Repository

1. Go to https://hub.docker.com and sign up / log in
2. Click **"Create repository"**
3. Fill in:
   - **Repository name:** `projects-tracker`
   - **Visibility:** Public or Private
4. Click **"Create"**

Your image address will be:
```
yourusername/projects-tracker
```

---

## Step 2 — Create Dockerfile

Create `Dockerfile` (no extension) at the **project root**, same level as `pom.xml`:

```dockerfile
# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Two-stage build explained:**
- **Stage 1** — downloads Maven + Java, copies source code, builds the JAR
- **Stage 2** — takes only the JAR, runs it in a lightweight container
- Result: much smaller final image ✅

---

## Step 3 — Create .env File

Create `.env` at the **project root** (already gitignored — never committed!):

```env
DB_USERNAME=tracker_user
DB_PASSWORD=securepassword123
DB_ROOT_PASSWORD=rootpassword123
```

> ⚠️ Change passwords to anything you like — just keep them consistent.
> This file must NEVER be pushed to GitHub.

---

## Step 4 — Create docker-compose.yml

Create `docker-compose.yml` at the **project root**:

```yaml
services:
  app:
    image: yourusername/projects-tracker:latest
    container_name: projects-tracker-app
    ports:
      - "8080:8080"
    environment:
      DB_URL: jdbc:mysql://db:3306/projects_tracker?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
      DB_USERNAME: ${DB_USERNAME}
      DB_PASSWORD: ${DB_PASSWORD}
    depends_on:
      db:
        condition: service_healthy
    restart: on-failure

  db:
    image: mysql:8.0
    container_name: projects-tracker-db
    environment:
      MYSQL_DATABASE: projects_tracker
      MYSQL_USER: ${DB_USERNAME}
      MYSQL_PASSWORD: ${DB_PASSWORD}
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mysql_data:
```

**Key points:**
- `${DB_USERNAME}` / `${DB_PASSWORD}` — Docker Compose reads these automatically from `.env`
- `mysql_data` volume — persists your database data on local disk permanently
- `depends_on` with `healthcheck` — app waits for MySQL to be fully ready before starting
- `DB_URL` uses `db` (container name) instead of `localhost` ✅

---

## Step 5 — Build Docker Image

Make sure **Docker Desktop is running**, then open terminal at project root:

```bash
docker build -t yourusername/projects-tracker:latest .
```

> Don't forget the dot `.` at the end — it means "build from current folder"

First build takes a few minutes (Maven downloads dependencies).

**Success looks like:**
```
Successfully built abc123def456
Successfully tagged yourusername/projects-tracker:latest
```

---

## Step 6 — Push Image to DockerHub

Login to DockerHub:
```bash
docker login
```

Push your image:
```bash
docker push yourusername/projects-tracker:latest
```

Your image is now live at:
```
https://hub.docker.com/r/yourusername/projects-tracker
```

---

## Step 7 — Run the App Locally

```bash
docker-compose up
```

This will:
1. Pull MySQL 8.0 image automatically
2. Start the database container
3. Wait for MySQL to be healthy
4. Start your Spring Boot app
5. Create all database tables automatically (Hibernate DDL)

**App is ready when you see:**
```
Started ProjectsTrackerApplication in X seconds
```

Open in browser: **http://localhost:8080** 🎉

---

## Daily Usage

### Option A — Docker Desktop (recommended for daily use)
1. Open Docker Desktop
2. Go to **Containers**
3. Find `projects-tracker`
4. Click ▶️ **Play** to start
5. Open http://localhost:8080
6. Click ⏹ **Stop** when done

### Option B — Terminal
```bash
# Start
docker-compose up

# Start in background (detached mode)
docker-compose up -d

# Stop gracefully
docker-compose down
```

> ⚠️ Always use `docker-compose down` WITHOUT `-v` flag to keep your data safe!
> `docker-compose down -v` deletes volumes = **data loss!**

---

## Stopping the App Safely

If running in terminal (not detached):
1. Press **`Ctrl + C`** in terminal first
2. Then close Docker Desktop

---

## Data Persistence — How It Works

```
Docker Volume: mysql_data
      ↕
/var/lib/mysql (inside MySQL container)
      ↕
Your local disk (managed by Docker)
```

| Action | Data Safe? |
|---|---|
| Stop container | ✅ Yes |
| Delete container | ✅ Yes |
| Delete image | ✅ Yes |
| Pull new app version | ✅ Yes |
| Restart PC | ✅ Yes |
| `docker-compose down` | ✅ Yes |
| `docker-compose down -v` | ❌ NO — deletes volume! |
| `docker volume rm projects-tracker_mysql_data` | ❌ NO — deletes volume! |

---

## Updating the App (New Version)

When you make code changes and want to update the Docker image:

```bash
# 1. Rebuild the image
docker build -t yourusername/projects-tracker:latest .

# 2. Push to DockerHub
docker push yourusername/projects-tracker:latest

# 3. Stop current containers
docker-compose down

# 4. Start with new image
docker-compose up
```

> Your data in `mysql_data` volume is untouched throughout this process ✅

---

## Pull and Run on Another Machine

On any machine with Docker installed:

```bash
# 1. Pull the image
docker pull yourusername/projects-tracker:latest

# 2. Create .env file with credentials (manually — never stored on GitHub!)
# 3. Copy docker-compose.yml from GitHub
# 4. Run
docker-compose up
```

---

## GitHub vs DockerHub — Key Difference

| | GitHub | DockerHub |
|---|---|---|
| Stores | Source code (.java, .xml, .html) | Built Docker images |
| Purpose | Version control | Image registry |
| What you push | Code commits | Runnable images |
| Relationship | "What the code looks like" | "What runs in container" |

They are **completely independent systems**. You manually bridge them by:
1. Writing code → GitHub
2. Building image from code → DockerHub

> 💡 **Future improvement:** GitHub Actions can automate this —
> every push to GitHub triggers automatic image build and push to DockerHub.

---

## Troubleshooting

**App fails to start — DataSource error:**
- Check that `.env` file exists at project root
- Check that `docker-compose.yml` has no `SPRING_PROFILE` set to a profile without matching properties file

**Port 8080 already in use:**
- Another app is using port 8080
- Change `"8080:8080"` to `"8081:8080"` in docker-compose.yml
- Access via http://localhost:8081

**Container keeps restarting:**
- MySQL might not be ready yet — wait a moment, it will retry
- Check logs in Docker Desktop by clicking on the container

---

*Generated during deployment session — March 2026*
