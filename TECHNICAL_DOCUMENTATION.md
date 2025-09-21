# Portfolio Tracker - Technical Documentation

## Project Overview

The Portfolio Tracker is a full-stack web application designed for investment portfolio management with real-time market data integration and AI-powered analytics.

## Architecture Overview

### Frontend Architecture
```
src/main/webapp/app/
├── config/           # Application configuration
├── entities/         # Entity management components
├── modules/          # Feature modules (auth, admin, etc.)
├── shared/           # Shared components and utilities
└── routes.tsx        # Application routing
```

### Backend Architecture
```
src/main/java/com/example/portfolio/
├── config/           # Spring configuration
├── domain/           # JPA entities
├── repository/       # Data access layer
├── service/          # Business logic layer
├── web/rest/         # REST controllers
└── security/         # Security configuration
```

## Key Features

### 1. Portfolio Management
- Create and manage multiple investment portfolios
- Real-time portfolio valuation
- Gain/loss calculations with percentage tracking
- Portfolio diversification analysis

### 2. Asset Tracking
- Add stocks and ETFs with real-time price validation
- Automatic price updates from Alpha Vantage API
- Historical price chart visualization
- Asset allocation pie charts

### 3. Analytics Dashboard
- Interactive Chart.js visualizations
- Real-time portfolio metrics
- AI-powered investment recommendations
- Diversification scoring algorithm

### 4. User Management
- JWT-based authentication
- Role-based access control (Admin/User)
- User registration and profile management
- Session management

## API Endpoints

### Portfolio Management
```
GET    /api/portfolios              # Get all portfolios
POST   /api/portfolios              # Create new portfolio
GET    /api/portfolios/{id}         # Get portfolio details
PUT    /api/portfolios/{id}         # Update portfolio
DELETE /api/portfolios/{id}         # Delete portfolio
```

### Asset Management
```
GET    /api/assets                  # Get all assets
POST   /api/assets                  # Create new asset
GET    /api/assets/{id}             # Get asset details
PUT    /api/assets/{id}             # Update asset
DELETE /api/assets/{id}             # Delete asset
```

### Portfolio Analytics
```
GET    /api/portfolio-analytics/portfolio/{id}/metrics    # Get portfolio metrics
GET    /api/portfolio-analytics/stock/{ticker}/price      # Get current stock price
GET    /api/portfolio-analytics/stock/{ticker}/historical # Get historical data
```

### Authentication
```
POST   /api/authenticate            # User login
GET    /api/authenticate            # Check authentication status
POST   /api/register               # User registration
POST   /api/account/reset-password/init   # Password reset request
POST   /api/account/reset-password/finish # Complete password reset
```

## Database Schema

### Core Entities

#### User (JHipster Default)
```sql
CREATE TABLE jhi_user (
    id BIGINT PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(191) UNIQUE,
    activated BOOLEAN DEFAULT FALSE,
    lang_key VARCHAR(10),
    created_date TIMESTAMP,
    -- Additional audit fields
);
```

#### UserAccount (Custom Entity)
```sql
CREATE TABLE user_account (
    id BIGINT PRIMARY KEY,
    login VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(100) NOT NULL
);
```

#### Portfolio
```sql
CREATE TABLE portfolio (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    user_id BIGINT REFERENCES user_account(id)
);
```

#### Asset
```sql
CREATE TABLE asset (
    id BIGINT PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity >= 1),
    avg_price DECIMAL(21,2) NOT NULL,
    portfolio_id BIGINT REFERENCES portfolio(id)
);
```

## Security Implementation

### JWT Configuration
- HS512 algorithm for token signing
- Configurable token validity periods
- Automatic token refresh mechanism
- Secure password encoding with BCrypt

### Authorization
- Role-based access control (ADMIN, USER)
- Method-level security annotations
- API endpoint protection
- CORS configuration for development

## Frontend State Management

### Redux Store Structure
```typescript
interface RootState {
  authentication: AuthenticationState;
  portfolio: EntityState<IPortfolio>;
  asset: EntityState<IAsset>;
  userAccount: EntityState<IUserAccount>;
  applicationProfile: ApplicationProfileState;
  locale: LocaleState;
  // Additional reducers...
}
```

### Key Reducers
- **Authentication**: User login/logout, session management
- **Portfolio**: CRUD operations, portfolio metrics
- **Asset**: Asset management, real-time price updates
- **UserAccount**: User profile management

## External API Integration

### Alpha Vantage API
- Real-time stock quotes
- Historical price data
- Company information lookup
- Rate limiting and error handling

### Mock Data Fallback
- Consistent mock price generation
- Historical data simulation
- Development environment support

## Build & Deployment

### Development Setup
```bash
# Backend (Spring Boot)
./mvnw spring-boot:run

# Frontend (React)
./npmw start
```

### Production Build
```bash
# Full production build
./mvnw -Pprod clean verify

# Docker deployment
docker-compose -f src/main/docker/app.yml up
```

## Testing Strategy

### Backend Testing
- Unit tests with JUnit 5
- Integration tests with TestContainers
- Repository tests with @DataJpaTest
- Web layer tests with @WebMvcTest

### Frontend Testing
- Component tests with Jest and React Testing Library
- Redux reducer tests
- Integration tests for user flows
- E2E tests with Cypress (configurable)

## Performance Optimizations

### Frontend
- Code splitting with React.lazy()
- Memoization with React.memo()
- Efficient re-rendering with useCallback/useMemo
- Bundle optimization with Webpack

### Backend
- JPA query optimization
- Database connection pooling
- Caching with Ehcache
- Async processing for non-critical operations

## Monitoring & Observability

### Application Metrics
- Spring Boot Actuator endpoints
- Custom business metrics
- Performance monitoring
- Health checks

### Logging
- Structured logging with Logback
- Environment-specific log levels
- Security event logging
- Performance tracking

## Development Best Practices

### Code Quality
- TypeScript for type safety
- ESLint and Prettier for code formatting
- SonarQube integration for code analysis
- Comprehensive error handling

### Architecture Patterns
- Clean Architecture principles
- Separation of concerns
- Dependency injection
- Repository pattern for data access

### Security Best Practices
- Input validation and sanitization
- SQL injection prevention
- XSS protection
- CSRF protection
- Secure headers configuration

## Deployment Architecture

### Development Environment
- Local PostgreSQL database
- Hot reload for frontend development
- Automatic backend restart
- Development-specific configurations

### Production Environment
- Containerized deployment with Docker
- PostgreSQL database with connection pooling
- Nginx reverse proxy (configurable)
- Environment-specific configurations

## API Documentation

The application includes comprehensive API documentation accessible at:
- Development: `http://localhost:8080/swagger-ui/`
- Production: `{domain}/swagger-ui/`

## Environment Configuration

### Required Environment Variables
```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/PortfolioTracker
SPRING_DATASOURCE_USERNAME=PortfolioTracker
SPRING_DATASOURCE_PASSWORD=

# JWT Configuration
JHIPSTER_SECURITY_AUTHENTICATION_JWT_BASE64_SECRET={base64-secret}

# External API
ALPHA_VANTAGE_API_KEY={your-api-key}
```

## Troubleshooting Guide

### Common Issues
1. **Database Connection**: Ensure PostgreSQL is running and accessible
2. **API Rate Limits**: Application falls back to mock data when API limits are reached
3. **Build Issues**: Clear node_modules and target directories, then rebuild
4. **Authentication Issues**: Check JWT secret configuration

### Debug Mode
Enable debug logging by setting:
```properties
logging.level.com.example.portfolio=DEBUG
```

## Future Roadmap

### Phase 1 (Short-term)
- Enhanced portfolio analytics
- Additional chart types
- Mobile responsiveness improvements
- Performance optimizations

### Phase 2 (Medium-term)
- Real-time WebSocket integration
- Advanced AI recommendations
- Social features and portfolio sharing
- Mobile application development

### Phase 3 (Long-term)
- Microservices architecture migration
- Advanced machine learning integration
- Multi-currency support
- Enterprise features and scaling