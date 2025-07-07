import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { CssBaseline, Box } from '@mui/material';
import { AuthProvider } from './contexts/AuthContext';
import Navbar from './components/Navbar';
import ProductList from './components/ProductList';
import ProductForm from './components/ProductForm';
import ProductDetail from './components/ProductDetail';
import Login from './components/Login';
import Register from './components/Register';
import UserProfile from './components/UserProfile';
import AdminDashboard from './components/AdminDashboard';
import OrderList from './components/OrderList';
import OrderDetail from './components/OrderDetail';
import OrderTracking from './components/OrderTracking';
import ProtectedRoute from './components/ProtectedRoute';
import './App.css';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <AuthProvider>
        <Router>
          <div className="App">
            <Navbar />
            <Box sx={{ minHeight: '100vh', bgcolor: '#f8fafc', pt: 2 }}>
              <Routes>
                {/* Public Routes */}
                <Route path="/" element={<ProductList />} />
                <Route path="/products" element={<ProductList />} />
                <Route path="/products/:id" element={<ProductDetail />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                {/* Public Order Tracking */}
                <Route path="/track" element={<OrderTracking />} />
                <Route path="/orders/track/:trackingNum" element={<OrderTracking />} />
                
                {/* User Protected Routes */}
                <Route 
                  path="/profile" 
                  element={
                    <ProtectedRoute>
                      <UserProfile />
                    </ProtectedRoute>
                  } 
                />
                
                {/* Order Management Routes */}
                <Route 
                  path="/orders" 
                  element={
                    <ProtectedRoute>
                      <OrderList />
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/orders/:orderId" 
                  element={
                    <ProtectedRoute>
                      <OrderDetail />
                    </ProtectedRoute>
                  } 
                />
                
                {/* Admin Protected Routes */}
                <Route 
                  path="/admin" 
                  element={
                    <ProtectedRoute adminRequired={true}>
                      <AdminDashboard />
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/products/new" 
                  element={
                    <ProtectedRoute adminRequired={true}>
                      <ProductForm />
                    </ProtectedRoute>
                  } 
                />
                <Route 
                  path="/products/edit/:id" 
                  element={
                    <ProtectedRoute adminRequired={true}>
                      <ProductForm />
                    </ProtectedRoute>
                  } 
                />
              </Routes>
            </Box>
          </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
