import React, { useState, useEffect } from 'react';
import {
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  CircularProgress,
  Grid,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { productAPI } from '../services/api';

const ProductForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = Boolean(id);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    quantity: '',
    sku: '',
    category: '',
    brand: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setLoading(true);
        const response = await productAPI.getProductById(id);
        const product = response.data;
        setFormData({
          name: product.name || '',
          description: product.description || '',
          price: product.price || '',
          quantity: product.quantity || '',
          sku: product.sku || '',
          category: product.category || '',
          brand: product.brand || '',
        });
      } catch (err) {
        setError('Failed to fetch product details');
        console.error('Error fetching product:', err);
      } finally {
        setLoading(false);
      }
    };

    if (isEdit) {
      fetchProduct();
    }
  }, [id, isEdit]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setLoading(true);
      setError(null);

      // Convert price and quantity to numbers
      const productData = {
        ...formData,
        price: parseFloat(formData.price),
        quantity: parseInt(formData.quantity, 10),
      };

      if (isEdit) {
        await productAPI.updateProduct(id, productData);
      } else {
        await productAPI.createProduct(productData);
      }

      setSuccess(true);
      setTimeout(() => {
        navigate('/products');
      }, 1500);

    } catch (err) {
      setError(
        err.response?.data?.message || 
        `Failed to ${isEdit ? 'update' : 'create'} product`
      );
      console.error('Error saving product:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/products');
  };

  if (loading && isEdit) {
    return (
      <Box display="flex" justifyContent="center" mt={4}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Paper sx={{ p: 4, mt: 2 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        {isEdit ? 'Edit Product' : 'Add New Product'}
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {success && (
        <Alert severity="success" sx={{ mb: 3 }}>
          Product {isEdit ? 'updated' : 'created'} successfully! Redirecting...
        </Alert>
      )}

      <Box component="form" onSubmit={handleSubmit}>
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Product Name"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              inputProps={{ minLength: 2, maxLength: 100 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="SKU"
              name="sku"
              value={formData.sku}
              onChange={handleChange}
              placeholder="e.g., PROD-001"
              helperText="Uppercase letters, numbers, and hyphens only"
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              multiline
              rows={3}
              required
              inputProps={{ minLength: 10, maxLength: 500 }}
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Price"
              name="price"
              type="number"
              value={formData.price}
              onChange={handleChange}
              required
              inputProps={{ 
                min: "0.01", 
                max: "999999.99", 
                step: "0.01" 
              }}
              InputProps={{
                startAdornment: <Typography sx={{ mr: 1 }}>$</Typography>,
              }}
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Quantity"
              name="quantity"
              type="number"
              value={formData.quantity}
              onChange={handleChange}
              required
              inputProps={{ 
                min: "0", 
                max: "100000" 
              }}
            />
          </Grid>

          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              label="Category"
              name="category"
              value={formData.category}
              onChange={handleChange}
              placeholder="e.g., Electronics"
              inputProps={{ maxLength: 50 }}
            />
          </Grid>

          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Brand"
              name="brand"
              value={formData.brand}
              onChange={handleChange}
              placeholder="e.g., Apple"
              inputProps={{ maxLength: 50 }}
            />
          </Grid>
        </Grid>

        <Box sx={{ mt: 4, display: 'flex', gap: 2 }}>
          <Button
            type="submit"
            variant="contained"
            disabled={loading}
            startIcon={loading && <CircularProgress size={20} />}
          >
            {loading ? 'Saving...' : (isEdit ? 'Update Product' : 'Create Product')}
          </Button>
          <Button
            variant="outlined"
            onClick={handleCancel}
            disabled={loading}
          >
            Cancel
          </Button>
        </Box>
      </Box>
    </Paper>
  );
};

export default ProductForm;
