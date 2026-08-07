#!/bin/bash

show_usage() {
    echo "Usage: ./start.sh [dev|prod]"
    echo ""
    echo "Options:"
    echo "  dev   - Start development environment with self-signed SSL"
    echo "  prod  - Start production environment with Let's Encrypt"
    echo ""
    exit 1
}

if [ "$#" -ne 1 ]; then
    show_usage
fi

ENV=$1

case $ENV in
    dev)
        echo "🚀 Starting DEVELOPMENT environment..."

        # Check if dev certs exist
        if [ ! -f "traefik/certs/gokapp.local.crt" ]; then
            echo "⚠️  Dev certificates not found. Running setup..."
            ./setup-dev-certs.sh
        fi

        # Check /etc/hosts
        if ! grep -q "gokapp.local" /etc/hosts; then
            echo "⚠️  Warning: gokapp.local not found in /etc/hosts"
            echo "   Run: sudo sh -c 'echo \"127.0.0.1 gokapp.local\" >> /etc/hosts'"
        fi

        cp .env.dev .env
        docker compose -f compose.yaml -f compose.dev.yaml up -d --pull always

        echo ""
        echo "✅ Development environment started!"
        echo "   App: https://gokapp.local"
        echo "   Traefik Dashboard: http://localhost:8081"
        ;;

    prod)
        echo "🚀 Starting PRODUCTION environment..."

        if [ ! -f ".env.prod" ]; then
            echo "❌ Error: .env.prod not found!"
            echo "   Create it from .env.example and configure your domain"
            exit 1
        fi

        cp .env.prod .env
        mkdir -p letsencrypt
        chmod 600 letsencrypt
        docker compose -f compose.yaml -f compose.prod.yaml up -d --pull always

        echo ""
        echo "✅ Production environment started!"
        echo "   Waiting for Let's Encrypt certificate..."
        echo "   Check logs: docker compose logs -f traefik"
        ;;

    *)
        show_usage
        ;;
esac