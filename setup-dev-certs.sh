#!/bin/bash

echo "🔐 Setting up development SSL certificates..."

# Create directories
mkdir -p traefik/certs
mkdir -p traefik/dynamic

# Generate self-signed certificate for gokapp.local
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout traefik/certs/gokapp.local.key \
  -out traefik/certs/gokapp.local.crt \
  -subj "/C=SE/ST=VastraGotaland/L=Goteborg/O=Development/CN=gokapp.local" \
  -addext "subjectAltName=DNS:gokapp.local,DNS:*.gokapp.local"

# Create Traefik TLS config
cat > traefik/dynamic/tls.yaml << 'EOF'
tls:
  certificates:
    - certFile: /etc/traefik/certs/gokapp.local.crt
      keyFile: /etc/traefik/certs/gokapp.local.key
EOF

echo "✅ Certificates generated!"
echo ""
echo "📝 Next steps:"
echo "1. Add to your /etc/hosts file:"
echo "   127.0.0.1 gokapp.local"
echo ""
echo "2. Start development environment:"
echo "   cp .env.dev .env"
echo "   docker compose -f compose.yaml -f compose.dev.yaml up -d"
echo ""
echo "3. Access your app at:"
echo "   https://gokapp.local"
echo "   (Accept the self-signed certificate warning)"
echo ""
echo "4. Traefik dashboard:"
echo "   http://localhost:8081"