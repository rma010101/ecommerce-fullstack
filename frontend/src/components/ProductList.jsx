import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  CardActions,
  CardMedia,
  Typography,
  Button,
  CircularProgress,
  Alert,
  Box,
  Chip,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Container,
  Divider,
  Rating,
  Tooltip,
  Fab,
  Fade,
  Paper,
  TextField,
  InputAdornment,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Snackbar,
} from '@mui/material';
import { 
  Edit, 
  Delete, 
  Visibility, 
  Add, 
  ShoppingCart,
  Search,
  FilterList,
  GridView,
  ViewList,
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { productAPI } from '../services/api';
import { useAuth } from '../hooks/useAuth';
import { addToCart } from '../utils/cartUtils';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteDialog, setDeleteDialog] = useState({ open: false, product: null });
  const [searchTerm, setSearchTerm] = useState('');
  const [sortBy, setSortBy] = useState('name');
  const [filterCategory, setFilterCategory] = useState('all');
  const [viewMode, setViewMode] = useState('grid'); // 'grid' or 'list'
  const [snackbar, setSnackbar] = useState({ open: false, message: '' }); // Add snackbar state
  const { isAdmin } = useAuth();

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const response = await productAPI.getAllProducts();
      setProducts(response.data);
      setFilteredProducts(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to fetch products. Make sure the backend is running.');
      console.error('Error fetching products:', err);
    } finally {
      setLoading(false);
    }
  };

  // Filter and sort products
  useEffect(() => {
    let filtered = [...products];

    // Search filter
    if (searchTerm) {
      filtered = filtered.filter(product =>
        product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        product.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        product.category?.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Category filter
    if (filterCategory !== 'all') {
      filtered = filtered.filter(product => product.category === filterCategory);
    }

    // Sort
    filtered.sort((a, b) => {
      switch (sortBy) {
        case 'price-low':
          return a.price - b.price;
        case 'price-high':
          return b.price - a.price;
        case 'name':
          return a.name.localeCompare(b.name);
        case 'stock':
          return b.quantity - a.quantity;
        default:
          return 0;
      }
    });

    setFilteredProducts(filtered);
  }, [products, searchTerm, sortBy, filterCategory]);

  // Get unique categories
  const categories = [...new Set(products.map(p => p.category).filter(Boolean))];

  const handleQuickAddToCart = (product, event) => {
    event.stopPropagation();
    event.preventDefault();
    if (product.quantity > 0) {
      addToCart(product, 1);
      setSnackbar({ 
        open: true, 
        message: `${product.name} added to cart!` 
      });
    }
  };

  const handleDeleteClick = (product) => {
    setDeleteDialog({ open: true, product });
  };

  const handleDeleteConfirm = async () => {
    try {
      await productAPI.deleteProduct(deleteDialog.product.id);
      setProducts(products.filter(p => p.id !== deleteDialog.product.id));
      setDeleteDialog({ open: false, product: null });
    } catch (err) {
      setError('Failed to delete product');
      console.error('Error deleting product:', err);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialog({ open: false, product: null });
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
        <Button onClick={fetchProducts} sx={{ ml: 2 }}>
          Retry
        </Button>
      </Alert>
    );
  }

  return (
    <Container maxWidth="xl">
      {/* Hero Section */}
      <Paper 
        elevation={0}
        sx={{ 
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          color: 'white',
          p: { xs: 4, md: 6 },
          mb: 4,
          borderRadius: 3,
          textAlign: 'center'
        }}
      >
        <Typography variant="h3" component="h1" gutterBottom fontWeight="bold">
          Welcome to Our Store
        </Typography>
        <Typography variant="h6" sx={{ opacity: 0.9, mb: 3 }}>
          Discover amazing products at great prices
        </Typography>
        {isAdmin() && (
          <Button
            variant="contained"
            size="large"
            startIcon={<Add />}
            component={Link}
            to="/products/new"
            sx={{ 
              bgcolor: 'rgba(255,255,255,0.2)', 
              '&:hover': { bgcolor: 'rgba(255,255,255,0.3)' },
              backdropFilter: 'blur(10px)'
            }}
          >
            Add New Product
          </Button>
        )}
      </Paper>

      {/* Search and Filter Bar */}
      <Paper elevation={1} sx={{ p: 3, mb: 4, borderRadius: 2 }}>
        <Grid container spacing={3} alignItems="center">
          <Grid item xs={12} md={4}>
            <TextField
              fullWidth
              placeholder="Search products..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search color="action" />
                  </InputAdornment>
                ),
              }}
              sx={{ bgcolor: 'background.paper' }}
            />
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth>
              <InputLabel>Category</InputLabel>
              <Select
                value={filterCategory}
                label="Category"
                onChange={(e) => setFilterCategory(e.target.value)}
              >
                <MenuItem value="all">All Categories</MenuItem>
                {categories.map((category) => (
                  <MenuItem key={category} value={category}>
                    {category}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} sm={6} md={3}>
            <FormControl fullWidth>
              <InputLabel>Sort By</InputLabel>
              <Select
                value={sortBy}
                label="Sort By"
                onChange={(e) => setSortBy(e.target.value)}
              >
                <MenuItem value="name">Name A-Z</MenuItem>
                <MenuItem value="price-low">Price: Low to High</MenuItem>
                <MenuItem value="price-high">Price: High to Low</MenuItem>
                <MenuItem value="stock">Stock Level</MenuItem>
              </Select>
            </FormControl>
          </Grid>
          
          <Grid item xs={12} md={2}>
            <Box display="flex" justifyContent="center">
              <IconButton 
                onClick={() => setViewMode('grid')}
                color={viewMode === 'grid' ? 'primary' : 'default'}
              >
                <GridView />
              </IconButton>
              <IconButton 
                onClick={() => setViewMode('list')}
                color={viewMode === 'list' ? 'primary' : 'default'}
              >
                <ViewList />
              </IconButton>
            </Box>
          </Grid>
        </Grid>
      </Paper>

      {/* Results Summary */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h5" component="h2" fontWeight="600">
          Products ({filteredProducts.length})
        </Typography>
        <Box display="flex" alignItems="center" gap={1}>
          <FilterList color="action" />
          <Typography variant="body2" color="text.secondary">
            {filteredProducts.length} of {products.length} products
          </Typography>
        </Box>
      </Box>

      {loading && (
        <Box display="flex" justifyContent="center" mt={4}>
          <CircularProgress size={60} />
        </Box>
      )}

      {error && (
        <Alert severity="error" sx={{ mt: 2 }}>
          {error}
          <Button onClick={fetchProducts} sx={{ ml: 2 }}>
            Retry
          </Button>
        </Alert>
      )}

      {!loading && !error && filteredProducts.length === 0 && (
        <Paper elevation={1} sx={{ p: 6, textAlign: 'center', mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            {searchTerm || filterCategory !== 'all' ? 'No products found matching your criteria' : 'No products available'}
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            {searchTerm || filterCategory !== 'all' ? 'Try adjusting your search or filters' : 'Check back later for new products'}
          </Typography>
          {isAdmin() && (
            <Button
              variant="contained"
              startIcon={<Add />}
              component={Link}
              to="/products/new"
              sx={{ mt: 2 }}
            >
              Add First Product
            </Button>
          )}
        </Paper>
      )}

      {/* Products Grid/List */}
      {!loading && !error && filteredProducts.length > 0 && (
        <Fade in={true}>
          <Box
            sx={{
              display: viewMode === 'grid' ? 'grid' : 'flex',
              flexDirection: viewMode === 'list' ? 'column' : undefined,
              gridTemplateColumns: viewMode === 'grid' ? {
                xs: 'repeat(1, 1fr)', // 1 column on mobile
                sm: 'repeat(2, 1fr)', // 2 columns on small tablets
                md: 'repeat(3, 1fr)', // 3 columns on medium screens
                lg: 'repeat(4, 1fr)', // 4 columns on large screens
                xl: 'repeat(5, 1fr)'  // 5 columns on extra large screens
              } : undefined,
              gap: { xs: 2, sm: 2.5, md: 3 }, // Smaller gaps on mobile
              alignItems: 'stretch', // Ensures perfect alignment
              width: '100%'
            }}
          >
            {filteredProducts.map((product) => (
              <Card 
                key={product.id}
                sx={{ 
                  height: 'auto', // Allow dynamic height
                  minHeight: viewMode === 'grid' ? { xs: 640, sm: 680, md: 720 } : 'auto', // Reduced minimum heights
                  maxHeight: viewMode === 'grid' ? { xs: 800, sm: 860, md: 920 } : 'auto', // Set maximum heights to prevent excessive growth
                  display: 'flex', 
                  flexDirection: viewMode === 'grid' ? 'column' : { xs: 'column', md: 'row' },
                  transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
                  '&:hover': { 
                    transform: { xs: 'translateY(-4px)', md: 'translateY(-8px)' }, 
                    boxShadow: { xs: '0 8px 25px rgba(0,0,0,0.12)', md: '0 12px 40px rgba(0,0,0,0.15)' }
                  },
                  borderRadius: 3,
                  overflow: 'hidden',
                  border: '1px solid',
                  borderColor: 'divider',
                  background: viewMode === 'grid' 
                    ? 'linear-gradient(145deg, #ffffff 0%, #f8fafc 100%)'
                    : 'linear-gradient(145deg, #f8fafc 0%, #e2e8f0 100%)'
                }}
              >
                  {/* Product Image Container - Simple and Reliable */}
                  <Box
                    sx={{
                      height: viewMode === 'grid' 
                        ? { xs: 280, sm: 300, md: 320 } // Much larger image area in grid view
                        : { xs: 180, md: 160 },
                      width: viewMode === 'list' ? { md: 200 } : '100%', // Full width in grid view
                      backgroundColor: '#667eea', // Simple solid color that always works
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      position: 'relative',
                      flexShrink: 0,
                      border: '1px solid #e0e0e0'
                    }}
                  >
                    {/* Large Category Icon */}
                    <Typography sx={{ 
                      fontSize: viewMode === 'grid' 
                        ? { xs: '6rem', md: '7rem' } // Much larger icons in grid view
                        : { xs: '3rem', md: '4rem' },
                      color: 'white',
                      textShadow: '0 2px 4px rgba(0,0,0,0.3)',
                      zIndex: 2
                    }}>
                      📦
                    </Typography>

                    {/* Brand Badge */}
                    <Box sx={{
                      position: 'absolute',
                      bottom: 8,
                      left: 8,
                      right: 8,
                      backgroundColor: 'rgba(0,0,0,0.7)',
                      color: 'white',
                      textAlign: 'center',
                      py: 0.5,
                      borderRadius: 1,
                      fontSize: '0.75rem',
                      fontWeight: 'bold'
                    }}>
                      {product.brand}
                    </Box>

                    {/* Stock Badge */}
                    {product.quantity <= 5 && product.quantity > 0 && (
                      <Chip 
                        label="Low Stock" 
                        color="warning" 
                        size="small" 
                        sx={{ 
                          position: 'absolute',
                          top: 8,
                          right: 8,
                          fontWeight: 600
                        }}
                      />
                    )}
                    
                    {product.quantity === 0 && (
                      <Chip 
                        label="Out of Stock" 
                        color="error" 
                        size="small" 
                        sx={{ 
                          position: 'absolute',
                          top: 8,
                          right: 8,
                          fontWeight: 600
                        }}
                      />
                    )}
                  </Box>

                  {/* Content Container */}
                  <Box sx={{ 
                    flexGrow: 1, 
                    display: 'flex', 
                    flexDirection: 'column',
                    p: 0
                  }}>                  <CardContent sx={{ 
                    minHeight: viewMode === 'grid' 
                      ? { xs: 180, sm: 200, md: 220 } // Reduced minimum content area to save space
                      : 'auto',
                    flex: '1 1 auto', // Allow content to grow as needed
                    p: { xs: 1.5, sm: 2 }, // Reduced padding to save space
                    display: 'flex',
                    flexDirection: viewMode === 'list' && { md: 'row' },
                    justifyContent: 'space-between',
                    alignItems: viewMode === 'list' && { md: 'flex-start' },
                    gap: viewMode === 'list' && { md: 3 }
                  }}>
                    {/* Main Content Section */}
                    <Box sx={{ 
                      flexGrow: 1,
                      display: 'flex',
                      flexDirection: 'column'
                    }}>
                      {/* Product Title */}
                      <Typography 
                        variant="h6" 
                        component="h3" 
                        sx={{ 
                          fontWeight: 700,
                          mb: 0.5, // Reduced margin
                          color: 'text.primary',
                          lineHeight: 1.2,
                          height: viewMode === 'grid' ? '2.4em' : 'auto',
                          maxHeight: '2.4em', // Ensure title doesn't grow beyond this
                          display: '-webkit-box', // Always use webkit-box for consistent truncation
                          WebkitLineClamp: 2, // Always limit to 2 lines
                          WebkitBoxOrient: 'vertical',
                          overflow: 'hidden', // Always hide overflow
                          fontSize: { xs: '0.9rem', sm: '0.95rem', md: '1rem' } // Responsive font size
                        }}
                      >
                        {product.name}
                      </Typography>

                      {/* Description */}
                      <Typography 
                        variant="body2" 
                        color="text.secondary" 
                        sx={{ 
                          mb: 1, // Reduced margin
                          lineHeight: 1.3, // Reduced line height for compactness
                          height: viewMode === 'grid' ? '2.6em' : 'auto',
                          maxHeight: '2.6em', // Prevent description from growing too much
                          display: '-webkit-box', // Always use webkit-box for consistent truncation
                          WebkitLineClamp: 2, // Limit to 2 lines for both grid and list
                          WebkitBoxOrient: 'vertical',
                          overflow: 'hidden',
                          fontSize: { xs: '0.8rem', sm: '0.85rem', md: '0.875rem' } // Responsive font size
                        }}
                      >
                        {product.description || 'No description available'}
                      </Typography>

                      {/* Category */}
                      <Box sx={{ mb: 1, height: viewMode === 'grid' ? '28px' : 'auto', display: 'flex', alignItems: 'flex-start' }}>
                        {product.category && (
                          <Chip 
                            label={product.category} 
                            variant="outlined" 
                            size="small" 
                            sx={{ 
                              borderRadius: 2,
                              fontSize: '0.7rem', // Slightly smaller font
                              fontWeight: 500,
                              height: '24px' // Fixed height for consistency
                            }}
                          />
                        )}
                      </Box>

                      {/* Rating */}
                      <Box display="flex" alignItems="center" gap={0.5} mb={1} sx={{ height: viewMode === 'grid' ? '20px' : 'auto' }}>
                        <Rating value={4.5} readOnly size="small" precision={0.5} />
                        <Typography variant="caption" color="text.secondary" fontWeight={500} sx={{ fontSize: '0.7rem' }}>
                          (24)
                        </Typography>
                      </Box>
                    </Box>

                    {/* Price and Actions Section - Only for List View */}
                    {viewMode === 'list' && (
                      <Box sx={{
                        display: 'flex',
                        flexDirection: { md: 'column' },
                        justifyContent: 'space-between',
                        alignItems: { md: 'flex-end' },
                        minWidth: { md: 200 },
                        gap: 2
                      }}>
                        {/* Price and Stock */}
                        <Box 
                          display="flex" 
                          flexDirection={{ md: 'column' }}
                          justifyContent="space-between" 
                          alignItems={{ xs: 'center', md: 'flex-end' }}
                        >
                          <Typography 
                            variant="h4" 
                            color="primary" 
                            sx={{ 
                              fontWeight: 700,
                              fontSize: { xs: '1.5rem', md: '1.8rem' },
                              mb: 0.5
                            }}
                          >
                            ${product.price}
                          </Typography>
                          <Typography 
                            variant="body2" 
                            color="text.secondary"
                            sx={{ fontWeight: 500 }}
                          >
                            {product.quantity} in stock
                          </Typography>
                        </Box>
                      </Box>
                    )}
                  </CardContent>

                  {/* Separate Price Section for Grid View */}
                  {viewMode === 'grid' && (
                    <Box sx={{
                      p: { xs: 1.5, sm: 2 }, // Reduced padding to save space
                      pb: 1,
                      borderTop: '1px solid',
                      borderColor: 'divider',
                      backgroundColor: 'rgba(0,0,0,0.02)',
                      minHeight: '80px', // Reduced from 100px to give more space for actions
                      height: 'auto', // Allow flexible height
                      display: 'flex',
                      flexDirection: 'column',
                      justifyContent: 'center',
                      alignItems: 'center',
                      gap: 0.5, // Reduced gap
                      flexShrink: 0 // Prevent this section from shrinking
                    }}>
                      {/* Price */}
                      <Typography 
                        variant="h4" 
                        color="primary" 
                        sx={{ 
                          fontWeight: 700,
                          fontSize: { xs: '1.5rem', md: '1.8rem' },
                          textAlign: 'center'
                        }}
                      >
                        ${product.price}
                      </Typography>
                      {/* Stock */}
                      <Typography 
                        variant="body2" 
                        color="text.secondary"
                        sx={{ 
                          fontWeight: 500,
                          textAlign: 'center'
                        }}
                      >
                        {product.quantity} in stock
                      </Typography>
                    </Box>
                  )}

                    {/* Actions */}
                    <Box sx={{ 
                      p: { xs: 1.5, sm: 2 }, // Reduced padding for better space utilization
                      pt: 1,
                      display: 'flex', 
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      gap: 1,
                      minHeight: { xs: '80px', md: '90px' }, // Reduced minimum height
                      height: 'auto', // Allow flexible height
                      flexShrink: 0, // Prevent this section from shrinking
                      borderTop: '1px solid',
                      borderColor: 'divider',
                      backgroundColor: 'rgba(0,0,0,0.02)', // Subtle background to separate actions
                      mt: 'auto' // Push to bottom of card
                    }}>
                      {/* Action Buttons */}
                      <Box display="flex" gap={0.5}>
                        <Tooltip title="View Details">
                          <IconButton 
                            size="small" 
                            color="primary"
                            component={Link}
                            to={`/products/${product.id}`}
                            sx={{ 
                              bgcolor: 'action.hover',
                              '&:hover': { bgcolor: 'primary.main', color: 'white' }
                            }}
                          >
                            <Visibility fontSize="small" />
                          </IconButton>
                        </Tooltip>
                        
                        {isAdmin() && (
                          <>
                            <Tooltip title="Edit Product">
                              <IconButton 
                                size="small" 
                                color="primary"
                                component={Link}
                                to={`/products/edit/${product.id}`}
                                sx={{ 
                                  bgcolor: 'action.hover',
                                  '&:hover': { bgcolor: 'primary.main', color: 'white' }
                                }}
                              >
                                <Edit fontSize="small" />
                              </IconButton>
                            </Tooltip>
                            <Tooltip title="Delete Product">
                              <IconButton 
                                size="small" 
                                color="error"
                                onClick={(e) => {
                                  e.preventDefault();
                                  e.stopPropagation();
                                  handleDeleteClick(product);
                                }}
                                sx={{ 
                                  bgcolor: 'action.hover',
                                  '&:hover': { bgcolor: 'error.main', color: 'white' }
                                }}
                              >
                                <Delete fontSize="small" />
                              </IconButton>
                            </Tooltip>
                          </>
                        )}
                      </Box>

                      {/* Add to Cart Button */}
                      <Button
                        variant="contained"
                        size="medium"
                        startIcon={<ShoppingCart />}
                        disabled={product.quantity === 0}
                        onClick={(e) => handleQuickAddToCart(product, e)}
                        sx={{ 
                          borderRadius: 1.5, // Slightly more rounded
                          textTransform: 'none',
                          fontWeight: 600,
                          px: { xs: 1.5, sm: 2, md: 2.5 }, // Responsive horizontal padding
                          py: { xs: 1, sm: 1.2, md: 1.3 }, // Responsive vertical padding
                          minWidth: { xs: '110px', sm: '120px', md: '130px' }, // Slightly smaller minimum width
                          height: { xs: '36px', sm: '38px', md: '40px' }, // Slightly smaller fixed height
                          fontSize: { xs: '0.8rem', sm: '0.85rem', md: '0.9rem' }, // Responsive font size
                          background: 'linear-gradient(45deg, #667eea 30%, #764ba2 90%)',
                          '&:hover': {
                            background: 'linear-gradient(45deg, #5a67d8 30%, #6b46c1 90%)',
                            transform: 'translateY(-1px)',
                            boxShadow: '0 4px 12px rgba(102, 126, 234, 0.4)'
                          },
                          '&:disabled': {
                            background: 'rgba(0,0,0,0.12)',
                            color: 'rgba(0,0,0,0.26)'
                          }
                        }}
                      >
                        {product.quantity === 0 ? 'Out of Stock' : 'Add to Cart'}
                      </Button>
                    </Box>
                  </Box>
                </Card>
            ))}
          </Box>
        </Fade>
      )}

      {/* Floating Action Button for Admin */}
      {isAdmin() && (
        <Fab
          color="primary"
          aria-label="add product"
          sx={{ position: 'fixed', bottom: 24, right: 24 }}
          component={Link}
          to="/products/new"
        >
          <Add />
        </Fab>
      )}

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialog.open} onClose={handleDeleteCancel}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <Typography>
            Are you sure you want to delete "{deleteDialog.product?.name}"?
            This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel}>Cancel</Button>
          <Button onClick={handleDeleteConfirm} color="error" variant="contained">
            Delete
          </Button>
        </DialogActions>
      </Dialog>

      {/* Success Snackbar */}
      <Snackbar
        open={snackbar.open}
        autoHideDuration={3000}
        onClose={() => setSnackbar({ open: false, message: '' })}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert 
          onClose={() => setSnackbar({ open: false, message: '' })} 
          severity="success" 
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Container>
  );
};

export default ProductList;
