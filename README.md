# E-Commerce Product Management - Full Stack Application

## 🏗️ Project Structure

```
ecommerce-fullstack/
├── backend/                    # Spring Boot Application
│   ├── src/
│   ├── pom.xml
│   ├── README.md
│   └── [Spring Boot files]
├── frontend/                   # React Vite Application
│   ├── src/
│   ├── package.json
│   ├── vite.config.js
│   └── [React files]
├── docs/                       # Documentation
├── docker-compose.yml          # Docker setup (optional)
└── README.md                   # This file
```

## 🚀 Quick Start

### Backend (Spring Boot)
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
- Runs on: http://localhost:8080
- API Docs: http://localhost:8080/actuator/health

### Frontend (React Vite)
```bash
cd frontend
npm install
npm run dev
```
- Runs on: http://localhost:5173
- Connects to backend API at http://localhost:8080

## 🔗 Integration

- **Backend**: REST API with CRUD operations for products
- **Frontend**: React app consuming the REST API
- **Database**: MongoDB for data persistence
- **CORS**: Configured for cross-origin requests

## 📋 Features

### Backend
- ✅ Product CRUD operations
- ✅ Search and filtering
- ✅ Bulk operations
- ✅ Input validation
- ✅ Error handling
- ✅ Rate limiting
- ✅ Caching
- ✅ Monitoring

### Frontend (To be implemented)
- 📝 Product listing
- 📝 Add/Edit products
- 📝 Delete products
- 📝 Search functionality
- 📝 Simple UI for testing

## 🛠️ Technologies

**Backend:**
- Spring Boot 3.5.3
- Spring Data MongoDB
- Maven
- Java 23

**Frontend:**
- React 18
- Vite
- Axios
- Modern CSS

## 📝 Next Steps

1. Move existing backend to `backend/` folder
2. Create React Vite frontend in `frontend/` folder
3. Configure CORS for integration
4. Build basic CRUD interface
5. Test full-stack functionality
