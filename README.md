# 🚀 AI Help Desk With AI Advisor

## Overview
AI Help Desk with AI Advisor is a comprehensive ticketing system that leverages artificial intelligence to provide intelligent responses and automate support workflows. The system is built with Spring Boot and integrates with Ollama's Llama3 model for natural language processing.

## Key Features
- 🤖 **AI-Powered Responses**: Integration with Ollama's Llama3 model
- 📧 **Email Integration**: Automatic email notifications and responses
- 🔐 **JWT Authentication**: Secure API access with JWT tokens
- 🚀 **Redis Caching**: Performance optimization with caching
- 📊 **Ticket Management**: Complete CRUD operations for support tickets
- 🎯 **Context-Aware AI**: Intelligent responses based on ticket context
- 📝 **OpenAPI Documentation**: Interactive API documentation with Swagger UI

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|---|---|---|
| Spring Boot | 4.0.6 | Core framework |
| Java | 17 | Programming language |
| Spring AI | 2.0.0-RC2 | AI integration |
| Ollama | Latest | Local LLM model serving |
| MySQL | Latest | Primary database |
| Redis | Latest | Caching layer |
| JWT | 0.12.5 | Authentication |
| Lombok | Latest | Reduce boilerplate code |
| SpringDoc OpenAPI | 2.5.0 | API documentation |

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

```bash
# Java 17 or higher
java -version

# Maven 3.6+
mvn -version

# MySQL 5.7+
mysql --version

# Redis 6.0+
redis-server --version

# Ollama with Llama3 model
ollama pull llama3
```

### Installing Ollama

```bash
# MacOS
brew install ollama

# Linux
curl -fsSL https://ollama.com/install.sh | sh

# Windows (via WSL2 recommended)
# Follow instructions at https://ollama.com/download/windows

# Start Ollama service
ollama serve

# Pull Llama3 model
ollama pull llama3
```

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/AI-Help-Desk.git
cd AI-Help-Desk
```

### 2. Configure Application Properties

Replace the content in `src/main/resources/application.yml` with:

```yaml
server:
  port: 4000
  servlet:
    context-path: /api/v1/hele-desk

spring:
  application:
    name: AI-Help-Desk

  # Email Configuration
  mail:
    host: smtp.gmail.com
    port: 587
    username: "your-email@gmail.com"
    password: "your-app-password"
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

  # Redis Configuration
  redis:
    host: localhost
    port: 6379

  # AI Configuration
  ai:
    ollama:
      base-url: http://localhost:11434
      model: llama3
      options:
        temperature: 0.7
        top_p: 0.9
        num-predict: 500
    embeddings:
      model: sentence-transformers/all-MiniLM-L6-v2
    max-token: 2000
    timeout: 30000

  # Database Configuration
  datasource:
    url: jdbc:mysql://localhost:3306/ai_help_desk
    username: root
    password: your-db-password
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true

jwt:
  secret: your-secure-jwt-secret-key-change-this-in-production

logging:
  file:
    name: ai-help-desk.log
  level:
    root: INFO
    org.springframework.web: DEBUG
    com.help.desk: DEBUG
```

### 3. Initialize Database

```sql
-- Create database
CREATE DATABASE IF NOT EXISTS ai_help_desk
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Create user if needed
CREATE USER IF NOT EXISTS 'helpdesk_user'@'localhost'
IDENTIFIED BY 'secure_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON ai_help_desk.*
TO 'helpdesk_user'@'localhost';

FLUSH PRIVILEGES;
```

### 4. Build the Application

```bash
mvn clean install -DskipTests
```

## ⚙️ Configuration

### Database Schema

The application automatically creates the following tables:

- `users` - User accounts and authentication
- `tickets` - Support tickets with status, priority, and AI responses
- `ticket_comments` - Comments and interactions on tickets
- `categories` - Ticket categorization
- `attachments` - File attachments
- `audit_logs` - System audit trail
- `cache_entries` - Cached AI responses

### Email Configuration

For production email setup:

**Gmail (Recommended for development):**
1. Enable 2-Factor Authentication on your Google account
2. Generate App Password: https://myaccount.google.com/apppasswords
3. Use the app password in `spring.mail.password`

**Custom SMTP Server:**

```yaml
spring:
  mail:
    host: smtp.yourdomain.com
    port: 465
    username: support@yourdomain.com
    password: your-smtp-password
    properties:
      mail.smtp.auth: true
      mail.smtp.ssl.enable: true
```

### AI Model Configuration

The application uses Ollama's Llama3 model:

```bash
# Check Ollama service
curl http://localhost:11434/api/generate -d '{
  "model": "llama3",
  "prompt": "Hello"
}'

# Available models
ollama list

# Pull alternative models
ollama pull codellama
ollama pull mistral
```

### Caching Strategy

Redis cache configuration (add to `application.yml`):

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour
      cache-null-values: false

# Custom cache TTLs
app:
  cache:
    ticket-ttl: 300000  # 5 minutes
    ai-response-ttl: 3600000  # 1 hour
    user-session-ttl: 86400000  # 24 hours
```

## 🚀 Running the Application

### Development Mode

```bash
mvn spring-boot:run
```

### Production Mode

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/AI-Help-Desk-0.0.1-SNAPSHOT.jar
```

### Running as a Background Service (Linux)

**Using nohup:**

```bash
nohup java -jar target/AI-Help-Desk-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
```

**Using Systemd Service:**

Create `/etc/systemd/system/ai-help-desk.service`:

```ini
[Unit]
Description=AI Help Desk Application
After=network.target mysql.service redis.service

[Service]
Type=simple
User=your-user
WorkingDirectory=/opt/ai-help-desk
ExecStart=/usr/bin/java -jar /opt/ai-help-desk/AI-Help-Desk-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10
StandardOutput=append:/var/log/ai-help-desk/app.log
StandardError=append:/var/log/ai-help-desk/error.log
EnvironmentFile=/opt/ai-help-desk/application.properties

[Install]
WantedBy=multi-user.target
```

Then:

```bash
sudo systemctl daemon-reload
sudo systemctl enable ai-help-desk
sudo systemctl start ai-help-desk
sudo systemctl status ai-help-desk
```

### Running on Windows as a Service

**Using WinSW (Windows Service Wrapper):**

1. Download WinSW from https://github.com/winsw/winsw/releases
2. Create `ai-help-desk.xml`:

```xml
<service>
  <id>ai-help-desk</id>
  <name>AI Help Desk</name>
  <description>AI Help Desk Service</description>
  <executable>java</executable>
  <arguments>-jar "C:\path\to\AI-Help-Desk-0.0.1-SNAPSHOT.jar"</arguments>
  <logmode>rotate</logmode>
</service>
```

3. Install the service:

```bash
winsw install ai-help-desk.xml
winsw start ai-help-desk
```

## 📚 API Documentation

Once running, access Swagger UI at:

```
http://localhost:4000/api/v1/hele-desk/swagger-ui.html
```

### Key Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/login` | User authentication |
| POST | `/auth/register` | User registration |
| GET | `/tickets` | Get all tickets |
| POST | `/tickets` | Create new ticket |
| GET | `/tickets/{id}` | Get ticket by ID |
| PUT | `/tickets/{id}` | Update ticket |
| POST | `/tickets/{id}/ai-response` | Generate AI response |
| GET | `/categories` | Get all categories |
| POST | `/categories` | Create new category |

### Authentication

All API endpoints (except `/auth/*`) require JWT authentication:

```bash
# Login to get token
curl -X POST http://localhost:4000/api/v1/hele-desk/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Use token for subsequent requests
curl -X GET http://localhost:4000/api/v1/hele-desk/tickets \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Example API Requests

**Create a Ticket:**

```bash
curl -X POST http://localhost:4000/api/v1/hele-desk/tickets \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Cannot access my account",
    "description": "I am unable to login to my account since yesterday",
    "priority": "HIGH",
    "category": "ACCOUNT_ISSUES"
  }'
```

**Get AI Response:**

```bash
curl -X POST http://localhost:4000/api/v1/hele-desk/tickets/1/ai-response \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 🔐 Security

### JWT Configuration

```yaml
jwt:
  secret: your-secure-jwt-secret-key-at-least-64-characters-long
  expiration: 86400000  # 24 hours in milliseconds
  refresh-expiration: 604800000  # 7 days
```

### Security Headers

Add to your `application.yml`:

```yaml
server:
  servlet:
    session:
      cookie:
        http-only: true
        secure: true
        same-site: strict
```

### Password Encoding

The application uses BCrypt password encoding:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

## 🗄️ Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'USER',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email)
);
```

### Tickets Table

```sql
CREATE TABLE tickets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) DEFAULT 'OPEN',
    priority VARCHAR(50) DEFAULT 'MEDIUM',
    category_id BIGINT,
    user_id BIGINT NOT NULL,
    assigned_to BIGINT,
    ai_response TEXT,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (assigned_to) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_at (created_at)
);
```

### Categories Table

```sql
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🚦 Monitoring & Logging

### Log Configuration

```yaml
logging:
  file:
    name: logs/ai-help-desk.log
    max-size: 10MB
    max-history: 30
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  level:
    com.help.desk: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

### Log Rotation with Logrotate (Linux)

Create `/etc/logrotate.d/ai-help-desk`:

```
/var/log/ai-help-desk/*.log {
    daily
    rotate 30
    compress
    delaycompress
    missingok
    notifempty
    create 0640 helpdesk helpdesk
    postrotate
        systemctl restart ai-help-desk
    endscript
}
```

### Health Check Endpoints

Add Spring Boot Actuator dependency to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Then access:

```bash
# Application health
curl http://localhost:4000/api/v1/hele-desk/actuator/health

# Detailed health info
curl http://localhost:4000/api/v1/hele-desk/actuator/health/details

# Metrics
curl http://localhost:4000/api/v1/hele-desk/actuator/metrics
```

## 📊 Performance Optimization

### Database Optimization

```sql
-- Add indexes for frequently queried columns
CREATE INDEX idx_tickets_user_id ON tickets(user_id);
CREATE INDEX idx_tickets_status_priority ON tickets(status, priority);
CREATE INDEX idx_tickets_created_at ON tickets(created_at DESC);
CREATE INDEX idx_users_email ON users(email);

-- Optimize queries with EXPLAIN
EXPLAIN SELECT * FROM tickets WHERE user_id = 1 AND status = 'OPEN';
```

### Redis Caching Implementation

```java
@Service
public class TicketService {

    @Cacheable(value = "tickets", key = "#id", unless = "#result == null")
    public Ticket getTicket(Long id) {
        return ticketRepository.findById(id).orElse(null);
    }

    @CacheEvict(value = "tickets", key = "#id")
    public void updateTicket(Long id, Ticket ticket) {
        // Update logic
    }

    @CachePut(value = "tickets", key = "#result.id")
    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}
```

### JVM Performance Tuning

```bash
# Start with optimized JVM settings
java -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:ParallelGCThreads=4 \
     -XX:ConcGCThreads=2 \
     -Xms2g -Xmx2g \
     -jar AI-Help-Desk-0.0.1-SNAPSHOT.jar
```

## 🔧 Troubleshooting

### Common Issues and Solutions

**Ollama Connection Issues:**

```bash
# Check if Ollama is running
curl http://localhost:11434/api/health

# If not running, start it
ollama serve

# Check if model is available
ollama list

# Pull model if missing
ollama pull llama3

# Check logs
tail -f ~/.ollama/logs/server.log
```

**Redis Connection Issues:**

```bash
# Check Redis status
redis-cli ping

# If not running, start Redis
sudo systemctl start redis  # Linux
brew services start redis   # MacOS

# View Redis logs
redis-cli monitor

# Flush cache (if needed, be careful in production)
redis-cli FLUSHALL
```

**Database Connection Issues:**

```sql
-- Check MySQL service
sudo systemctl status mysql

-- Check connection
mysql -u root -p -e "SELECT 1;"

-- View active connections
SHOW PROCESSLIST;

-- Kill hanging connections
KILL [connection_id];

-- Check database size
SELECT
    table_schema AS 'Database',
    ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'ai_help_desk';
```

**Application Won't Start:**

```bash
# Check if port 4000 is in use
sudo lsof -i :4000  # Linux/Mac
netstat -ano | findstr :4000  # Windows

# Kill process using the port
kill -9 [PID]  # Linux/Mac
taskkill /PID [PID] /F  # Windows

# Check application logs
tail -f ai-help-desk.log
```

**Email Sending Issues:**

```bash
# Test email configuration
curl -X POST http://localhost:4000/api/v1/hele-desk/test-email \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"to":"test@example.com","subject":"Test","body":"Test message"}'

# Check Gmail App Password settings
# 1. Go to https://myaccount.google.com/apppasswords
# 2. Generate a new app password
# 3. Update password in application.yml
```

**Memory Issues:**

```bash
# Monitor JVM memory usage
jstat -gcutil [PID] 1000

# Check heap dump
jmap -heap [PID]

# Increase heap size
java -Xmx4g -Xms4g -jar AI-Help-Desk-0.0.1-SNAPSHOT.jar
```

## 📝 Production Deployment Checklist

- [ ] Change default database passwords
- [ ] Generate strong JWT secret key (minimum 64 characters)
- [ ] Enable HTTPS with SSL certificate
- [ ] Configure proper SMTP with app passwords
- [ ] Set up database backup strategy
- [ ] Configure firewall rules (allow only necessary ports)
- [ ] Set up monitoring and alerting
- [ ] Enable audit logging
- [ ] Configure proper log rotation
- [ ] Set up rate limiting
- [ ] Regular security updates
- [ ] Implement disaster recovery plan
- [ ] Load test the application
- [ ] Set up health check monitoring
- [ ] Configure proper environment variables

## 📁 Directory Structure

```
AI-Help-Desk/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── help/
│   │   │           └── desk/
│   │   │               ├── controller/
│   │   │               ├── service/
│   │   │               ├── repository/
│   │   │               ├── model/
│   │   │               ├── config/
│   │   │               ├── security/
│   │   │               └── util/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── static/
│   │       └── templates/
│   └── test/
├── logs/
│   └── ai-help-desk.log
├── pom.xml
└── README.md
```

## 🔄 Backup and Recovery

### Database Backup

```bash
# Daily backup script
#!/bin/bash
BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p'password' ai_help_desk > $BACKUP_DIR/ai_help_desk_$DATE.sql
gzip $BACKUP_DIR/ai_help_desk_$DATE.sql

# Keep only last 7 days of backups
find $BACKUP_DIR -name "*.sql.gz" -mtime +7 -delete
```

### Application Backup

```bash
# Backup application JAR and configuration
tar -czf ai-help-desk-backup.tar.gz \
    /opt/ai-help-desk/AI-Help-Desk-*.jar \
    /opt/ai-help-desk/application.yml \
    /opt/ai-help-desk/application.properties
```

## 📈 Performance Metrics to Monitor

- **Response Time**: Average API response time
- **Throughput**: Requests per second
- **Error Rate**: Percentage of failed requests
- **Database Connections**: Active database connections
- **Cache Hit Ratio**: Redis cache effectiveness
- **JVM Memory Usage**: Heap and non-heap memory
- **CPU Usage**: Application CPU utilization
- **AI Response Time**: Time taken for AI responses

## 🤝 Contributing

Please read `CONTRIBUTING.md` for details on our code of conduct and the process for submitting pull requests.

## 📄 License

This project is licensed under the MIT License - see the `LICENSE.md` file for details.

## 🆘 Support

For support:

- Email: support@aihelpdesk.com
- Create an issue in the GitHub repository
- Check documentation: https://github.com/GovindaEkabote/IT-Help-Desk-With-AI-Advisor
