# Setup
## Prerequisites
- Docker and Docker Compose
- OpenSSL (for generating dev certificates)

## Stack
This application runs three containers:
1. Traefik as proxy and SSL (using lets encrypt)
2. MySQL to store data
3. Gokapp, the application that the users are using

The app is written in java, using Spring Boot, and Thymeleaf for sever side html-rendering. Some plain javascript is also used.
During the build process the "gokapp" is packed as an image that can be used in a container environment.

## Summary setup
Using the start-script first checks that some fundamental settings are 
done or else fixes these issues and then start the application with the profile added (ie. dev or prod)
### First time setup
Clone the repository:
```bash
git clone https://github.com/erydberg/gokapp.git
cd gokapp
```
### Production
Set up fundamental configuration, domain name, passwords etc.
```bash
cp .env.example .env.prod
vi .env.prod  # Configure domain, passwords, email in this file
```
#### Start
```bash
./start.sh prod
```

#### Or manually
```bash
cp .env.prod .env
docker compose -f compose.yaml -f compose.prod.yaml up -d
```
Access at https://your-domain.com


### Development
```bash
./setup-dev-certs.sh
sudo sh -c 'echo "127.0.0.1 gokapp.local" >> /etc/hosts'
```
#### Start
```bash
./start.sh dev
```
#### Or manually
```bash
cp .env.dev .env
docker compose -f compose.yaml -f compose.dev.yaml up -d
```

Access at https://gokapp.local



## More detail

### First Time Setup

1. Clone the repository:
```bash
git clone https://github.com/erydberg/gokapp.git
cd gokapp
```

2. Generate development SSL certificates:
```bash
chmod +x setup-dev-certs.sh
./setup-dev-certs.sh
```

3. Add local domain to your `/etc/hosts`:
```bash
# On Linux/Mac
sudo sh -c 'echo "127.0.0.1 gokapp.local" >> /etc/hosts'

# On Windows (as Administrator in notepad)
# Add to C:\Windows\System32\drivers\etc\hosts:
# 127.0.0.1 gokapp.local
```

4. Set up environment:
```bash
cp .env.dev .env
```

5. Start the application:
```bash
docker compose -f compose.yaml -f compose.dev.yaml up -d
```

6. Access the application:
- **App**: https://gokapp.local (accept self-signed certificate warning)
- **Traefik Dashboard**: http://localhost:8081

7. View logs:
```bash
docker compose logs -f gokapp
```

8. Stop the application:
```bash
docker compose -f compose.yaml -f compose.dev.yaml down
```

### Accepting Self-Signed Certificate

When you first visit https://gokapp.local, your browser will show a security warning because the certificate is self-signed:

- **Chrome/Edge**: Click "Advanced" → "Proceed to gokapp.local (unsafe)"
- **Firefox**: Click "Advanced" → "Accept the Risk and Continue"
- **Safari**: Click "Show Details" → "visit this website"

This is normal for local development!


## Production Deployment

### Prerequisites
- Server with Docker and Docker Compose
- Domain name pointing to your server's IP
- Ports 80 and 443 open in firewall

### Setup

1. Clone the repository on your server:
```bash
git clone https://github.com/erydberg/gokapp.git
cd gokapp
```

2. Create production environment file:
```bash
cp .env.example .env.prod
nano .env.prod
```

3. Update `.env.prod` with:
    - Strong database passwords
    - Your domain name
    - Your email for Let's Encrypt
    - Traefik dashboard password (optional)

4. Copy to active environment:
```bash
cp .env.prod .env
```

5. Create letsencrypt directory with correct permissions:
```bash
mkdir -p letsencrypt
chmod 600 letsencrypt
```

6. Start the application:
```bash
docker compose -f compose.yaml -f compose.prod.yaml up -d
```

7. Watch the logs to see certificate generation:
```bash
docker compose logs -f traefik
```

Let's Encrypt will automatically:
- Generate SSL certificates
- Renew them before expiry
- Handle HTTPS for your domain

8. Access your application:
- **App**: https://your-domain.com
- **Traefik Dashboard**: https://traefik.your-domain.com (if configured)

### Generating Traefik Dashboard Password

To generate a secure password for the Traefik dashboard:
```bash
# Install apache2-utils (if not installed)
sudo apt-get install apache2-utils

# Generate password (replace 'yourpassword' with your desired password)
echo $(htpasswd -nb admin yourpassword) | sed -e s/\\$/\\$\\$/g

# Copy the output to TRAEFIK_DASHBOARD_AUTH in .env.prod
```

## Troubleshooting

### Development

**Certificate errors?**
```bash
# Regenerate certificates
./setup-dev-certs.sh
docker compose -f compose.yaml -f compose.dev.yaml restart traefik
```

**Can't access gokapp.local?**
- Check `/etc/hosts` file has the entry
- Try `ping gokapp.local` to verify DNS

**Port conflicts?**
- Make sure ports 80, 443, 8081 are not in use
- Check with: `sudo lsof -i :80 -i :443 -i :8081`

### Production

**Let's Encrypt rate limits?**
- Use staging server first (uncomment in compose.prod.yaml)
- Production limit: 50 certificates per domain per week

**Certificate not generating?**
- Ensure domain DNS points to your server
- Check ports 80 and 443 are open
- View logs: `docker compose logs traefik`

**Database connection issues?**
- Check environment variables are set
- Verify MySQL is healthy: `docker compose ps`

## Switching Between Environments

**To Development:**
```bash
docker compose -f compose.yaml -f compose.prod.yaml down
cp .env.dev .env
docker compose -f compose.yaml -f compose.dev.yaml up -d
```

**To Production:**
```bash
docker compose -f compose.yaml -f compose.dev.yaml down
cp .env.prod .env
docker compose -f compose.yaml -f compose.prod.yaml up -d
```

## Security Notes

⚠️ **Important for Production:**
- Never commit `.env.prod` to version control
- Use strong, unique passwords
- Regularly update Docker images
- Monitor logs for suspicious activity
- Keep backups of your database




# Start (Docker Compose finds .env automatically)
docker compose up -d

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

# For developers
## Working with local development
### Just run your app - uses dev profile with H2
```bash
mvn spring-boot:run
```
 Access H2 console at http://localhost:8080/h2-console

## Docker development with ssl and mysql
### Start with Docker and Traefik
./start.sh dev

### Or manually
```bash
cp .env.dev .env
docker compose -f compose.yaml -f compose.dev.yaml up -d
```

## On production server
```bash
./start.sh prod
```

### Or manually
```bash
cp .env.prod .env
docker compose -f compose.yaml -f compose.prod.yaml up -d
```



### Build the app image locally
```bash
mvn spring-boot:build-image
```


