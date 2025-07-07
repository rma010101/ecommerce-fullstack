import React, { useState, useEffect, useCallback } from 'react';
import {
  Paper,
  Typography,
  Box,
  Chip,
  Button,
  CircularProgress,
  Alert,
  Grid,
  Divider,
  TextField,
  Snackbar,
} from '@mui/material';
import { Edit, Delete, ArrowBack, ShoppingCart } from '@mui/icons-material';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { productAPI } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import { addToCart } from '../utils/cartUtils';

const ProductDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAdmin } = useAuth();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [snackbar, setSnackbar] = useState({ open: false, message: '' });

  const fetchProduct = useCallback(async () => {
    try {
      setLoading(true);
      const response = await productAPI.getProductById(id);
      setProduct(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch product details');
      console.error('Error fetching product:', err);
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchProduct();
  }, [fetchProduct]);

  const handleDelete = async () => {
    if (window.confirm(`Are you sure you want to delete "${product.name}"?`)) {
      try {
        await productAPI.deleteProduct(id);
        navigate('/products');
      } catch (err) {
        setError('Failed to delete product');
        console.error('Error deleting product:', err);
      }
    }
  };

  const handleAddToCart = () => {
    if (quantity > product.quantity) {
      setSnackbar({
        open: true,
        message: `Only ${product.quantity} items available in stock`
      });
      return;
    }

    addToCart(product, quantity);
    setSnackbar({
      open: true,
      message: `Added ${quantity} item(s) to cart!`
    });
  };

  const handleQuantityChange = (event) => {
    const value = parseInt(event.target.value) || 1;
    setQuantity(Math.max(1, value));
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ mt: 2 }}>
        {error}
        <Button onClick={fetchProduct} sx={{ ml: 2 }}>
          Retry
        </Button>
      </Alert>
    );
  }

  if (!product) {
    return (
      <Alert severity="warning" sx={{ mt: 2 }}>
        Product not found
      </Alert>
    );
  }

  return (
    <div>
      <Box display="flex" alignItems="center" mb={3}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate('/products')}
          sx={{ mr: 2 }}
        >
          Back to Products
        </Button>
        <Typography variant="h4" component="h1">
          Product Details
        </Typography>
      </Box>

      <Paper sx={{ p: 4 }}>
        <Grid container spacing={3}>
          <Grid item xs={12}>
            <Box display="flex" justifyContent="space-between" alignItems="start">
              <Typography variant="h5" component="h2" gutterBottom>
                {product.name}
              </Typography>
              {isAdmin() && (
                <Box>
                  <Button
                    variant="outlined"
                    startIcon={<Edit />}
                    component={Link}
                    to={`/products/edit/${product.id}`}
                    sx={{ mr: 1 }}
                  >
                    Edit
                  </Button>
                  <Button
                    variant="outlined"
                    color="error"
                    startIcon={<Delete />}
                    onClick={handleDelete}
                  >
                    Delete
                  </Button>
                </Box>
              )}
            </Box>
          </Grid>

          <Grid item xs={12}>
            <Box display="flex" alignItems="center" gap={2}>
              <Typography variant="h6" color="primary">
                ${product.price}
              </Typography>
              {product.quantity > 0 && (
                <>
                  <TextField
                    type="number"
                    label="Quantity"
                    value={quantity}
                    onChange={handleQuantityChange}
                    size="small"
                    sx={{ width: '100px' }}
                    inputProps={{ min: 1, max: product.quantity }}
                  />
                  <Button
                    variant="contained"
                    startIcon={<ShoppingCart />}
                    onClick={handleAddToCart}
                    disabled={product.quantity === 0}
                  >
                    Add to Cart
                  </Button>
                </>
              )}
              {product.quantity === 0 && (
                <Chip label="Out of Stock" color="error" />
              )}
            </Box>
          </Grid>

          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Description
            </Typography>
            <Typography variant="body1" paragraph>
              {product.description}
            </Typography>
          </Grid>

          <Grid item xs={12} md={6}>
            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
              Product Information
            </Typography>
            <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
              <Box display="flex" justifyContent="space-between">
                <Typography variant="body2" color="text.secondary">
                  Stock Quantity:
                </Typography>
                <Chip 
                  label={product.quantity} 
                  color={product.quantity > 10 ? 'success' : product.quantity > 0 ? 'warning' : 'error'}
                  size="small"
                />
              </Box>
              
              {product.sku && (
                <Box display="flex" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    SKU:
                  </Typography>
                  <Typography variant="body2">
                    {product.sku}
                  </Typography>
                </Box>
              )}
              
              {product.category && (
                <Box display="flex" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Category:
                  </Typography>
                  <Chip label={product.category} variant="outlined" size="small" />
                </Box>
              )}
              
              {product.brand && (
                <Box display="flex" justifyContent="space-between">
                  <Typography variant="body2" color="text.secondary">
                    Brand:
                  </Typography>
                  <Chip label={product.brand} variant="outlined" size="small" />
                </Box>
              )}
            </Box>
          </Grid>

          {(product.createdAt || product.updatedAt) && (
            <Grid item xs={12}>
              <Divider sx={{ my: 2 }} />
              <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                Timestamps
              </Typography>
              <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                {product.createdAt && (
                  <Box display="flex" justifyContent="space-between">
                    <Typography variant="body2" color="text.secondary">
                      Created:
                    </Typography>
                    <Typography variant="body2">
                      {new Date(product.createdAt).toLocaleString()}
                    </Typography>
                  </Box>
                )}
                {product.updatedAt && (
                  <Box display="flex" justifyContent="space-between">
                    <Typography variant="body2" color="text.secondary">
                      Last Updated:
                    </Typography>
                    <Typography variant="body2">
                      {new Date(product.updatedAt).toLocaleString()}
                    </Typography>
                  </Box>
                )}
              </Box>
            </Grid>
          )}
        </Grid>
      </Paper>
      
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ open: false, message: '' })}
        message={snackbar.message}
      />
    </div>
  );
};

export default ProductDetail;
