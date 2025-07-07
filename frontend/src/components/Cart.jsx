import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Card,
  CardContent,
  Grid,
  Button,
  IconButton,
  TextField,
  Divider,
  Alert,
  Badge,
} from '@mui/material';
import {
  Add,
  Remove,
  Delete,
  ShoppingCart,
  ShoppingCartOutlined,
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { useNavigate } from 'react-router-dom';
import Checkout from './Checkout';
import OrderSuccess from './OrderSuccess';

const Cart = () => {
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [cartItems, setCartItems] = useState([]);
  const [total, setTotal] = useState(0);
  const [showCheckout, setShowCheckout] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(null);

  useEffect(() => {
    // Load cart from localStorage
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      const items = JSON.parse(savedCart);
      setCartItems(items);
      calculateTotal(items);
    }
  }, []);

  const calculateTotal = (items) => {
    const totalAmount = items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
    setTotal(totalAmount);
  };

  const updateQuantity = (productId, newQuantity) => {
    if (newQuantity <= 0) {
      removeFromCart(productId);
      return;
    }

    const updatedItems = cartItems.map(item =>
      item.id === productId ? { ...item, quantity: newQuantity } : item
    );
    setCartItems(updatedItems);
    calculateTotal(updatedItems);
    localStorage.setItem('cart', JSON.stringify(updatedItems));
  };

  const removeFromCart = (productId) => {
    const updatedItems = cartItems.filter(item => item.id !== productId);
    setCartItems(updatedItems);
    calculateTotal(updatedItems);
    localStorage.setItem('cart', JSON.stringify(updatedItems));
  };

  const clearCart = () => {
    setCartItems([]);
    setTotal(0);
    localStorage.removeItem('cart');
  };

  const handleCheckout = () => {
    if (!isAuthenticated) {
      alert('Please log in to proceed with checkout');
      return;
    }
    
    setShowCheckout(true);
  };

  const handleOrderSuccess = (order) => {
    setOrderSuccess(order);
    setShowCheckout(false);
    clearCart();
  };

  const handleBackToCart = () => {
    setShowCheckout(false);
  };

  // If order was successful, show success page
  if (orderSuccess) {
    return <OrderSuccess order={orderSuccess} />;
  }

  // If in checkout mode, show checkout component
  if (showCheckout) {
    return (
      <Checkout
        cartItems={cartItems}
        onOrderSuccess={handleOrderSuccess}
        onBack={handleBackToCart}
      />
    );
  }

  if (cartItems.length === 0) {
    return (
      <Box textAlign="center" py={4}>
        <ShoppingCartOutlined sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
        <Typography variant="h6" color="text.secondary" gutterBottom>
          Your cart is empty
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Add some products to get started!
        </Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h6" gutterBottom>
        Shopping Cart ({cartItems.length} items)
      </Typography>

      <Grid container spacing={2}>
        {cartItems.map((item) => (
          <Grid item xs={12} key={item.id}>
            <Card variant="outlined">
              <CardContent>
                <Grid container spacing={2} alignItems="center">
                  <Grid item xs={12} sm={6}>
                    <Typography variant="subtitle1" fontWeight="medium">
                      {item.name}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      ${item.price} each
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12} sm={3}>
                    <Box display="flex" alignItems="center">
                      <IconButton
                        size="small"
                        onClick={() => updateQuantity(item.id, item.quantity - 1)}
                      >
                        <Remove />
                      </IconButton>
                      <TextField
                        size="small"
                        value={item.quantity}
                        onChange={(e) => updateQuantity(item.id, parseInt(e.target.value) || 0)}
                        sx={{ width: '60px', mx: 1 }}
                        inputProps={{ style: { textAlign: 'center' } }}
                      />
                      <IconButton
                        size="small"
                        onClick={() => updateQuantity(item.id, item.quantity + 1)}
                      >
                        <Add />
                      </IconButton>
                    </Box>
                  </Grid>
                  
                  <Grid item xs={12} sm={2}>
                    <Typography variant="subtitle1" fontWeight="medium">
                      ${(item.price * item.quantity).toFixed(2)}
                    </Typography>
                  </Grid>
                  
                  <Grid item xs={12} sm={1}>
                    <IconButton
                      size="small"
                      color="error"
                      onClick={() => removeFromCart(item.id)}
                    >
                      <Delete />
                    </IconButton>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Divider sx={{ my: 2 }} />

      <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
        <Typography variant="h6">
          Total: ${total.toFixed(2)}
        </Typography>
        <Button
          variant="outlined"
          color="error"
          onClick={clearCart}
          size="small"
        >
          Clear Cart
        </Button>
      </Box>

      {!isAuthenticated && (
        <Alert severity="warning" sx={{ mb: 2 }}>
          Please log in to proceed with checkout
        </Alert>
      )}

      <Button
        variant="contained"
        color="primary"
        fullWidth
        size="large"
        onClick={handleCheckout}
        disabled={!isAuthenticated}
      >
        Proceed to Checkout
      </Button>
    </Box>
  );
};

// Cart Icon Component for Navbar
export const CartIcon = ({ onClick }) => {
  const [itemCount, setItemCount] = useState(0);

  useEffect(() => {
    const updateCartCount = () => {
      const savedCart = localStorage.getItem('cart');
      if (savedCart) {
        const items = JSON.parse(savedCart);
        const count = items.reduce((sum, item) => sum + item.quantity, 0);
        setItemCount(count);
      } else {
        setItemCount(0);
      }
    };

    updateCartCount();
    
    // Listen for cart updates
    window.addEventListener('cartUpdated', updateCartCount);
    
    return () => {
      window.removeEventListener('cartUpdated', updateCartCount);
    };
  }, []);

  return (
    <IconButton color="inherit" onClick={onClick}>
      <Badge badgeContent={itemCount} color="error">
        <ShoppingCart />
      </Badge>
    </IconButton>
  );
};

export default Cart;
