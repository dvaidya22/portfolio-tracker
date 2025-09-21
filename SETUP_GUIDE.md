# Portfolio Tracker - Setup Guide

## Prerequisites

### Required Software
- **Java 17+** (OpenJDK or Oracle JDK)
- **Node.js 22.15.0+** (LTS version recommended)
- **PostgreSQL 15+** (or Docker for containerized setup)
- **Git** (for version control)

### Optional Tools
- **Docker & Docker Compose** (for containerized development)
- **IDE**: IntelliJ IDEA, VS Code, or Eclipse
- **Postman** (for API testing)

## Installation Steps

### 1. Clone the Repository
```bash
git clone <repository-url>
cd portfolio-tracker
```

### 2. Database Setup

#### Option A: Local PostgreSQL
```bash
# Install PostgreSQL (Ubuntu/Debian)
sudo apt update
sudo apt install postgresql postgresql-contrib

# Create database and user
sudo -u postgres psql
CREATE DATABASE PortfolioTracker;
CREATE USER PortfolioTracker WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE PortfolioTracker TO PortfolioTracker;
\q
```

#### Option B: Docker Setup
```bash
# Start PostgreSQL with Docker Compose
docker-compose -f src/main/docker/postgresql.yml up -d
```

### 3. Backend Configuration

#### Configure Application Properties
```bash
# Copy example configuration
cp src/main/resources/config/application-dev.yml.example src/main/resources/config/application-dev.yml

# Edit database configuration
nano src/main/resources/config/application-dev.yml
```

#### Update Database Settings
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/PortfolioTracker
    username: PortfolioTracker
    password: your_password
```

### 4. External API Configuration

#### Alpha Vantage API Setup
1. Register at [Alpha Vantage](https://www.alphavantage.co/support/#api-key)
2. Get your free API key
3. Add to application configuration:

```yaml
app:
  alpha-vantage:
    api-key: YOUR_API_KEY_HERE
```

### 5. Build and Run

#### Backend (Spring Boot)
```bash
# Install dependencies and run
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend (React)
```bash
# Install dependencies
./npmw install

# Start development server
./npmw start
```

## Development Workflow

### 1. Full Development Setup
```bash
# Terminal 1: Start backend
./mvnw spring-boot:run

# Terminal 2: Start frontend
./npmw start

# Terminal 3: Start database (if using Docker)
docker-compose -f src/main/docker/postgresql.yml up
```

### 2. Access Points
- **Frontend**: http://localhost:9000
- **Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui/
- **Database**: localhost:5432

### 3. Default Accounts
```
Admin Account:
- Username: admin
- Password: admin

User Account:
- Username: user
- Password: user
```

## Production Deployment

### 1. Build for Production
```bash
# Create production build
./mvnw -Pprod clean verify

# The JAR file will be created in target/
java -jar target/*.jar
```

### 2. Docker Deployment
```bash
# Build Docker image
./mvnw -Pprod verify jib:dockerBuild

# Run with Docker Compose
docker-compose -f src/main/docker/app.yml up
```

### 3. Environment Variables
```bash
# Required for production
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/PortfolioTracker
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET=your_jwt_secret
export ALPHA_VANTAGE_API_KEY=your_api_key
```

## Testing

### Run All Tests
```bash
# Backend tests
./mvnw test

# Frontend tests
./npmw test

# Integration tests
./mvnw verify
```

### Test Coverage
```bash
# Generate coverage reports
./mvnw test jacoco:report
./npmw test -- --coverage
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Test connection
psql -h localhost -U PortfolioTracker -d PortfolioTracker
```

#### 2. Port Conflicts
```bash
# Check if ports are in use
lsof -i :8080  # Backend
lsof -i :9000  # Frontend
lsof -i :5432  # Database
```

#### 3. Node.js Issues
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
./npmw install
```

#### 4. Maven Issues
```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository

# Clean and rebuild
./mvnw clean compile
```

### Debug Mode

#### Enable Debug Logging
```yaml
logging:
  level:
    com.example.portfolio: DEBUG
    org.springframework.web: DEBUG
```

#### Frontend Debug
```bash
# Start with debug mode
REACT_APP_DEBUG=true ./npmw start
```

## IDE Configuration

### IntelliJ IDEA
1. Import as Maven project
2. Enable annotation processing
3. Install recommended plugins:
   - Spring Boot
   - TypeScript
   - React

### VS Code
1. Install recommended extensions:
   - Java Extension Pack
   - Spring Boot Extension Pack
   - TypeScript and JavaScript
   - React snippets

## Performance Monitoring

### Application Metrics
- Access metrics at: http://localhost:8080/management/metrics
- Health checks: http://localhost:8080/management/health
- Application info: http://localhost:8080/management/info

### Database Monitoring
```sql
-- Check active connections
SELECT * FROM pg_stat_activity WHERE datname = 'PortfolioTracker';

-- Monitor query performance
SELECT * FROM pg_stat_statements ORDER BY total_time DESC LIMIT 10;
```

## Backup and Maintenance

### Database Backup
```bash
# Create backup
pg_dump -h localhost -U PortfolioTracker PortfolioTracker > backup.sql

# Restore backup
psql -h localhost -U PortfolioTracker PortfolioTracker < backup.sql
```

### Log Management
```bash
# View application logs
tail -f logs/spring.log

# Rotate logs (production)
logrotate /etc/logrotate.d/portfolio-tracker
```

## Security Considerations

### Development
- Use HTTPS in production
- Secure JWT secret generation
- Regular dependency updates
- Input validation and sanitization

### Production Checklist
- [ ] Change default passwords
- [ ] Configure proper CORS settings
- [ ] Set up SSL/TLS certificates
- [ ] Configure firewall rules
- [ ] Set up monitoring and alerting
- [ ] Regular security updates

## Support and Maintenance

### Regular Tasks
- Monitor application logs
- Update dependencies monthly
- Backup database weekly
- Review security configurations
- Monitor API usage and limits

### Contact Information
For technical support or questions about the implementation, please refer to the project documentation or contact the development team.