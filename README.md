### Setup

1. Create your environment configuration:
```bash
cp .env.example .env
```

2. **IMPORTANT**: Edit `.env` and change the default passwords:
```bash
nano .env  # or use your preferred editor
```

3. Start the application:
```bash
docker compose up -d
```

4. Access at http://localhost:8080

### Security Notes

⚠️ **Never use default passwords in production!**

The `.env.example` file contains placeholder passwords. You must:
1. Copy it to `.env`
2. Change all passwords to strong, unique values
3. Never commit `.env` to version control

### Stopping the Application
```bash
# Stop containers
docker compose down

# Stop and remove all data
docker compose down -v
```