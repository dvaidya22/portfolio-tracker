# Portfolio Tracker - API Documentation

## Base URL
- **Development**: `http://localhost:8080/api`
- **Production**: `https://your-domain.com/api`

## Authentication

All API endpoints (except public ones) require JWT authentication.

### Headers
```
Authorization: Bearer {jwt_token}
Content-Type: application/json
```

### Public Endpoints
- `POST /authenticate` - User login
- `POST /register` - User registration
- `GET /activate` - Account activation
- `POST /account/reset-password/init` - Password reset request
- `POST /account/reset-password/finish` - Complete password reset

## Authentication Endpoints

### Login
```http
POST /api/authenticate
Content-Type: application/json

{
  "username": "admin",
  "password": "admin",
  "rememberMe": false
}
```

**Response:**
```json
{
  "id_token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### Check Authentication Status
```http
GET /api/authenticate
Authorization: Bearer {token}
```

**Response:** `204 No Content` if authenticated, `401 Unauthorized` if not

### User Registration
```http
POST /api/register
Content-Type: application/json

{
  "login": "newuser",
  "email": "user@example.com",
  "password": "password123",
  "langKey": "en"
}
```

## Portfolio Management

### Get All Portfolios
```http
GET /api/portfolios?page=0&size=20&sort=id,desc
Authorization: Bearer {token}
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "name": "Tech Stocks",
      "createdDate": "2025-01-19T10:30:00Z",
      "user": {
        "id": 1,
        "login": "admin"
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### Create Portfolio
```http
POST /api/portfolios
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "My Tech Portfolio",
  "createdDate": "2025-01-19T10:30:00Z",
  "user": {
    "id": 1
  }
}
```

### Get Portfolio Details
```http
GET /api/portfolios/{id}
Authorization: Bearer {token}
```

### Update Portfolio
```http
PUT /api/portfolios/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "name": "Updated Portfolio Name",
  "createdDate": "2025-01-19T10:30:00Z",
  "user": {
    "id": 1
  }
}
```

### Delete Portfolio
```http
DELETE /api/portfolios/{id}
Authorization: Bearer {token}
```

## Asset Management

### Get All Assets
```http
GET /api/assets?page=0&size=20&sort=id,desc
Authorization: Bearer {token}
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "ticker": "AAPL",
      "quantity": 10,
      "avgPrice": 150.50,
      "portfolio": {
        "id": 1,
        "name": "Tech Stocks"
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### Create Asset
```http
POST /api/assets
Authorization: Bearer {token}
Content-Type: application/json

{
  "ticker": "AAPL",
  "quantity": 10,
  "avgPrice": 150.50,
  "portfolio": {
    "id": 1
  }
}
```

### Get Asset Details
```http
GET /api/assets/{id}
Authorization: Bearer {token}
```

### Update Asset
```http
PUT /api/assets/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "ticker": "AAPL",
  "quantity": 15,
  "avgPrice": 145.75,
  "portfolio": {
    "id": 1
  }
}
```

### Delete Asset
```http
DELETE /api/assets/{id}
Authorization: Bearer {token}
```

## Portfolio Analytics

### Get Portfolio Metrics
```http
GET /api/portfolio-analytics/portfolio/{id}/metrics
Authorization: Bearer {token}
```

**Response:**
```json
{
  "totalValue": 1505.00,
  "totalCost": 1450.00,
  "totalGainLoss": 55.00,
  "totalGainLossPercent": 3.79,
  "diversificationScore": 65.0,
  "recommendedAsset": "MSFT",
  "assets": [
    {
      "ticker": "AAPL",
      "quantity": 10,
      "avgPrice": 145.00,
      "currentPrice": 150.50,
      "currentValue": 1505.00,
      "gainLoss": 55.00,
      "gainLossPercent": 3.79
    }
  ]
}
```

### Get Current Stock Price
```http
GET /api/portfolio-analytics/stock/{ticker}/price
Authorization: Bearer {token}
```

**Response:**
```json
{
  "ticker": "AAPL",
  "price": 150.50
}
```

### Get Historical Stock Data
```http
GET /api/portfolio-analytics/stock/{ticker}/historical
Authorization: Bearer {token}
```

**Response:**
```json
{
  "ticker": "AAPL",
  "data": {
    "2025-01-19": {
      "1. open": "149.50",
      "2. high": "151.20",
      "3. low": "148.80",
      "4. close": "150.50",
      "5. volume": "45678900"
    },
    "2025-01-18": {
      "1. open": "148.20",
      "2. high": "150.10",
      "3. low": "147.90",
      "4. close": "149.50",
      "5. volume": "52341200"
    }
  }
}
```

## User Account Management

### Get All User Accounts
```http
GET /api/user-accounts?page=0&size=20&sort=id,desc
Authorization: Bearer {token}
```

### Create User Account
```http
POST /api/user-accounts
Authorization: Bearer {token}
Content-Type: application/json

{
  "login": "newuser",
  "email": "newuser@example.com",
  "password": "securepassword123"
}
```

### Get User Account Details
```http
GET /api/user-accounts/{id}
Authorization: Bearer {token}
```

### Update User Account
```http
PUT /api/user-accounts/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "id": 1,
  "login": "updateduser",
  "email": "updated@example.com",
  "password": "newpassword123"
}
```

### Delete User Account
```http
DELETE /api/user-accounts/{id}
Authorization: Bearer {token}
```

## Account Management

### Get Current User Account
```http
GET /api/account
Authorization: Bearer {token}
```

**Response:**
```json
{
  "id": 1,
  "login": "admin",
  "firstName": "Administrator",
  "lastName": "Administrator",
  "email": "admin@localhost",
  "imageUrl": null,
  "activated": true,
  "langKey": "en",
  "createdBy": "system",
  "createdDate": "2025-01-19T10:00:00Z",
  "authorities": ["ROLE_ADMIN", "ROLE_USER"]
}
```

### Update Account Settings
```http
POST /api/account
Authorization: Bearer {token}
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "langKey": "en"
}
```

### Change Password
```http
POST /api/account/change-password
Authorization: Bearer {token}
Content-Type: application/json

{
  "currentPassword": "oldpassword",
  "newPassword": "newpassword123"
}
```

## Admin Endpoints

### User Management (Admin Only)

#### Get All Users
```http
GET /api/admin/users?page=0&size=20&sort=id,desc
Authorization: Bearer {admin_token}
```

#### Create User
```http
POST /api/admin/users
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "login": "newuser",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "activated": true,
  "langKey": "en",
  "authorities": ["ROLE_USER"]
}
```

#### Update User
```http
PUT /api/admin/users
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "id": 1,
  "login": "updateduser",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "activated": true,
  "langKey": "en",
  "authorities": ["ROLE_USER"]
}
```

#### Delete User
```http
DELETE /api/admin/users/{login}
Authorization: Bearer {admin_token}
```

## Error Responses

### Standard Error Format
```json
{
  "type": "https://www.jhipster.tech/problem/problem-with-message",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "path": "/api/portfolios",
  "message": "error.validation",
  "fieldErrors": [
    {
      "objectName": "portfolioDTO",
      "field": "name",
      "message": "must not be null"
    }
  ]
}
```

### Common HTTP Status Codes
- **200 OK** - Successful GET, PUT requests
- **201 Created** - Successful POST requests
- **204 No Content** - Successful DELETE requests
- **400 Bad Request** - Validation errors, malformed requests
- **401 Unauthorized** - Authentication required
- **403 Forbidden** - Insufficient permissions
- **404 Not Found** - Resource not found
- **409 Conflict** - Resource conflict (duplicate email, etc.)
- **500 Internal Server Error** - Server-side errors

## Rate Limiting

### Alpha Vantage API Limits
- **Free Tier**: 5 API requests per minute, 500 per day
- **Premium**: Higher limits available
- **Fallback**: Application uses mock data when limits exceeded

### Application Rate Limiting
- No built-in rate limiting (can be added with Spring Cloud Gateway)
- Consider implementing for production use

## Pagination

### Standard Pagination Parameters
- `page`: Page number (0-based)
- `size`: Number of items per page (default: 20)
- `sort`: Sort criteria (e.g., `id,desc` or `name,asc`)

### Example
```http
GET /api/portfolios?page=0&size=10&sort=createdDate,desc
```

## Filtering and Search

### Basic Filtering
Currently implemented through query parameters on specific endpoints.

### Future Enhancements
- Full-text search capabilities
- Advanced filtering options
- Saved search queries

## Webhooks (Future Feature)

### Portfolio Updates
```http
POST /api/webhooks/portfolio-update
Content-Type: application/json

{
  "portfolioId": 1,
  "event": "asset_added",
  "timestamp": "2025-01-19T10:30:00Z",
  "data": {
    "assetId": 5,
    "ticker": "MSFT"
  }
}
```

## SDK and Client Libraries

### JavaScript/TypeScript Client
```typescript
import axios from 'axios';

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

// Get portfolios
const portfolios = await apiClient.get('/portfolios');
```

### Java Client
```java
// Using Spring's RestTemplate
RestTemplate restTemplate = new RestTemplate();
HttpHeaders headers = new HttpHeaders();
headers.setBearerAuth(token);
HttpEntity<String> entity = new HttpEntity<>(headers);

ResponseEntity<Portfolio[]> response = restTemplate.exchange(
    "http://localhost:8080/api/portfolios",
    HttpMethod.GET,
    entity,
    Portfolio[].class
);
```

## Testing the API

### Using curl
```bash
# Login
curl -X POST http://localhost:8080/api/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'

# Get portfolios
curl -X GET http://localhost:8080/api/portfolios \
  -H "Authorization: Bearer {token}"
```

### Using Postman
1. Import the OpenAPI specification from `/v3/api-docs`
2. Set up environment variables for base URL and token
3. Use the pre-configured requests

## Monitoring and Observability

### Health Checks
```http
GET /management/health
```

### Metrics
```http
GET /management/metrics
Authorization: Bearer {admin_token}
```

### Application Info
```http
GET /management/info
```

## Versioning

### API Versioning Strategy
- Currently using URL path versioning
- Future versions will maintain backward compatibility
- Deprecated endpoints will be marked and phased out gradually

### Version History
- **v1.0** - Initial release with core portfolio management
- **v1.1** - Added real-time stock data integration
- **v1.2** - Enhanced analytics and AI recommendations

## Support

### Documentation
- **Swagger UI**: Available at `/swagger-ui/` when running the application
- **OpenAPI Spec**: Available at `/v3/api-docs`

### Common Integration Patterns
- Use pagination for large datasets
- Implement proper error handling
- Cache frequently accessed data
- Use WebSocket for real-time updates (future feature)