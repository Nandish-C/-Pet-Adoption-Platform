# 🐾 Pet Adoption Platform

A comprehensive full-stack web application for pet adoption, featuring user management, pet listings, adoption processes, donations, real-time chat, and administrative controls.

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## ✨ Features

### 🏠 User Features
- **Pet Browsing**: View available pets with images, descriptions, and pricing
- **Advanced Search**: Filter pets by species, breed, age, and location
- **Pet Matching**: AI-powered pet matching based on user preferences
- **Adoption Process**: Complete adoption workflow with application forms
- **Donation System**: Support shelters with secure Stripe payments
- **Real-time Chat**: Communicate with other users and administrators
- **Payment History**: Track all transactions and donations

### 👨‍💼 Admin Features
- **Dashboard**: Comprehensive admin panel with statistics
- **Pet Management**: Add, edit, delete pet listings
- **User Management**: View and manage all users
- **Adoption Oversight**: Review and approve adoption applications
- **Chat Moderation**: Monitor and participate in user conversations
- **Donation Tracking**: Monitor all donations and payments
- **Shelter Management**: Manage shelter information and contacts

### 🔐 Security Features
- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: Admin and user role separation
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Cross-origin resource sharing setup

## 🛠️ Technologies Used

### Backend
- **Java 17**: Programming language
- **Spring Boot 3.5.5**: Framework for building REST APIs
- **Spring Security**: Authentication and authorization
- **Spring Data MongoDB**: MongoDB integration
- **JWT (JJWT 0.11.5)**: JSON Web Token implementation
- **Stripe Java SDK**: Payment processing
- **Spring WebSocket**: Real-time communication
- **Spring Mail**: Email functionality

### Frontend
- **Angular 20**: Modern web framework
- **TypeScript 5.9**: Programming language
- **Angular Material**: UI component library
- **Bootstrap 5.3.8**: CSS framework
- **RxJS 7.8**: Reactive programming
- **STOMP.js**: WebSocket communication
- **SockJS**: WebSocket fallback

### Database
- **MongoDB**: NoSQL database
- **MongoDB Compass**: Database management GUI (optional)

### Development Tools
- **Maven**: Java dependency management
- **npm**: Node.js package management
- **Angular CLI**: Angular development tools

## 📋 Prerequisites

### System Requirements
- **Operating System**: Windows 10/11, macOS, or Linux
- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: 5GB free space

### Software Dependencies

#### Required Software
1. **Java 17** or higher
   - Download from: https://adoptium.net/
   - Verify: `java -version`

2. **Node.js 18+ and npm**
   - Download from: https://nodejs.org/
   - Verify: `node -v` and `npm -v`

3. **MongoDB Community Server**
   - Download from: https://www.mongodb.com/try/download/community
   - Install and start MongoDB service
   - Default port: 27017

4. **Angular CLI**
   - Install globally: `npm install -g @angular/cli`
   - Verify: `ng version`

#### Optional Software
- **MongoDB Compass**: GUI for MongoDB management
- **Visual Studio Code**: Recommended IDE
- **Git**: Version control

## 🚀 Installation

### Step 1: Clone the Repository
```bash
git clone https://github.com/Nandish-C/-Pet-Adoption-Platform
cd pet-adoption-project
```

### Step 2: Install Backend Dependencies
```bash
cd backend/backend
# Dependencies are automatically downloaded by Maven during build
```

### Step 3: Install Frontend Dependencies
```bash
cd ../../frontend
npm install
```

### Step 4: Install Root Dependencies (for database scripts)
```bash
cd ..
npm install
```

## 🗄️ Database Setup

### Option 1: Using Sample Data (Recommended)
```bash
# Load sample data into MongoDB
node load_sample_data.js
```

### Option 2: Manual Setup
1. Start MongoDB service
2. Create database named `pet_adoption`
3. Import sample data from `database/` folder

### Database Structure
- **users**: User accounts and profiles
- **pets**: Pet listings with images and details
- **shelters**: Shelter information
- **adoptions**: Adoption applications
- **donations**: Donation records
- **payments**: Payment transactions
- **chats**: General chat messages
- **user_chats**: User-to-user chat sessions
- **user_messages**: Individual chat messages
- **inquiries**: User inquiries

## ⚙️ Configuration

### Backend Configuration (`backend/backend/src/main/resources/application.properties`)
```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/pet_adoption

# Server Configuration
server.port=8080

# JWT Configuration (update with your secret)
jwt.secret=your-jwt-secret-key-here

# Stripe Configuration (update with your keys)
stripe.api.key=sk_test_your_stripe_secret_key
stripe.secret.key=sk_test_your_stripe_secret_key

# Email Configuration (update with your Gmail credentials)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# File Upload Configuration
file.upload-dir=uploads/pets
```

### Frontend Configuration
The frontend automatically connects to `http://localhost:8080` for API calls.

## ▶️ Running the Application

### Method 1: Using Batch Scripts (Windows)
```bash
# Start backend
run_backend.bat

# In another terminal, start frontend
run_frontend.bat
```

### Method 2: Manual Startup

#### Terminal 1: socket-server 
```bash 
cd socket-server
node index.js
```

#### Terminal 2: Start Backend
```bash
cd backend/backend
mvn spring-boot:run
```
Backend will start on: http://localhost:8080

#### Terminal 3: Start Frontend
```bash
cd frontend
npm start
```
Frontend will start on: http://localhost:4200

### Method 3: Using npm scripts

#### Root Directory Scripts
```bash
# Install all dependencies
npm run install:all

# Start both frontend and backend
npm run start:all
```

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `GET /api/auth/users-for-chat` - Get users for chat

### Pets
- `GET /api/pets` - Get all pets
- `GET /api/pets/{id}` - Get pet by ID
- `POST /api/admin/pets` - Create pet (Admin)
- `PUT /api/admin/pets/{id}` - Update pet (Admin)
- `DELETE /api/admin/pets/{id}` - Delete pet (Admin)

### Users
- `GET /api/admin/users` - Get all users (Admin)
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update user profile

### Admin Endpoints
- `GET /api/admin/dashboard` - Admin dashboard stats
- `GET /api/admin/adoptions` - All adoption applications
- `GET /api/admin/donations` - All donations
- `GET /api/admin/payments` - All payments
- `GET /api/admin/chats` - All chats

### Chat System
- `GET /api/user-chats/user/{userId}` - Get user's chats
- `POST /api/user-chats/start` - Start new chat
- `GET /api/user-chats/{chatId}/messages` - Get chat messages
- `POST /api/user-chats/{chatId}/messages` - Send message

### Payments & Donations
- `POST /api/payments/create` - Create payment intent
- `POST /api/donations` - Make donation
- `GET /api/payments/user/{userId}` - Get user payments

## 📁 Project Structure

```
pet-adoption-project/
├── backend/                          # Spring Boot backend
│   └── backend/
│       ├── src/main/java/com/petadoption/backend/
│       │   ├── config/               # Security, CORS, JWT config
│       │   ├── controller/           # REST controllers
│       │   ├── model/                # Entity models
│       │   ├── repository/           # MongoDB repositories
│       │   ├── services/             # Business logic
│       │   └── BackendApplication.java
│       ├── src/main/resources/
│       │   └── application.properties
│       └── pom.xml
├── frontend/                         # Angular frontend
│   ├── src/app/
│   │   ├── components/               # Angular components
│   │   ├── services/                 # Angular services
│   │   ├── guards/                   # Route guards
│   │   ├── interceptors/             # HTTP interceptors
│   │   └── app.config.ts
│   ├── angular.json
│   └── package.json
├── database/                         # Database files
│   ├── *.json                        # Exported collections
│   ├── *.sql                         # SQL scripts
│   └── export_summary.json
├── package.json                      # Root package.json
├── README.md                         # This file
└── .vscode/                          # VS Code settings
```

## 🔧 Troubleshooting

### Common Issues and Solutions

#### 1. **Port 4200 Already in Use**
```bash
# Kill process using port 4200
npx kill-port 4200

# Or run on different port
npm start -- --port 4201
```

#### 2. **MongoDB Connection Failed**
```bash
# Check if MongoDB is running
net start MongoDB

# Or on Linux/Mac
sudo systemctl start mongod

# Verify connection
mongosh --eval "db.runCommand('ping')"
```

#### 3. **Java Version Issues**
```bash
# Check Java version
java -version

# Set JAVA_HOME if needed
set JAVA_HOME="C:\Program Files\Java\jdk-17"
```

#### 4. **npm Install Fails**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### 5. **CORS Errors**
- Ensure backend is running on port 8080
- Check CORS configuration in `SecurityConfig.java`
- Verify frontend proxy settings

#### 6. **JWT Authentication Issues**
- Check JWT secret in `application.properties`
- Ensure tokens aren't expired (default: 24 hours)
- Verify user roles and permissions

#### 7. **File Upload Issues**
- Check `file.upload-dir` in `application.properties`
- Ensure upload directory exists and has write permissions
- Verify file size limits

#### 8. **Email Configuration Issues**
- Use Gmail app password instead of regular password
- Enable 2FA on Gmail account
- Check firewall settings for SMTP port 587

#### 9. **Stripe Payment Issues**
- Verify Stripe keys in `application.properties`
- Check Stripe dashboard for test mode
- Ensure webhook endpoints are configured

#### 10. **WebSocket Connection Issues**
- Check if backend WebSocket is enabled
- Verify STOMP client configuration
- Check browser console for connection errors

### Debug Mode

#### Enable Debug Logging
```properties
# Add to application.properties
logging.level.com.petadoption=DEBUG
logging.level.org.springframework.security=DEBUG
```

#### Frontend Debug Mode
```bash
# Run Angular in development mode with detailed logging
npm start
```

### Performance Issues

#### Database Optimization
- Ensure MongoDB indexes are created
- Check database connection pooling
- Monitor query performance

#### Frontend Optimization
```bash
# Build for production
npm run build --prod

# Analyze bundle size
npm install -g webpack-bundle-analyzer
npx webpack-bundle-analyzer dist/frontend/stats.json
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- Create an issue in the repository
- Check the troubleshooting section above
- Review the API documentation

---
github id : Nandish-C


**Happy Coding! 🎉**


Built with ❤️ for pet lovers everywhere
