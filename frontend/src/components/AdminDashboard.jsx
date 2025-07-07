import React, { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Card,
  CardContent,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Chip,
  Alert,
  CircularProgress,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Container,
} from '@mui/material';
import {
  Dashboard,
  People,
  Inventory,
  Add,
  Visibility,
  Edit,
  Delete,
  ShoppingCart,
  Warning,
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import { productAPI } from '../services/api';
import { useAuth } from '../hooks/useAuth';

const AdminDashboard = () => {
  const { isAdmin } = useAuth();
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalUsers: 0,
    lowStockProducts: 0,
    totalRevenue: 0,
  });
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleteDialog, setDeleteDialog] = useState({ open: false, product: null });

  useEffect(() => {
    if (isAdmin()) {
      fetchDashboardData();
    }
  }, [isAdmin]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const response = await productAPI.getAllProducts();
      const productList = response.data;
      
      setProducts(productList);
      
      // Calculate stats
      setStats({
        totalProducts: productList.length,
        totalUsers: 0, // TODO: Implement user count API
        lowStockProducts: productList.filter(p => p.quantity < 10).length,
        totalRevenue: productList.reduce((sum, p) => sum + (p.price * (100 - p.quantity)), 0), // Mock calculation
      });
      
      setError(null);
    } catch (err) {
      setError('Failed to fetch dashboard data');
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
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
      // Refresh stats
      fetchDashboardData();
    } catch (err) {
      setError('Failed to delete product');
      console.error('Error deleting product:', err);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialog({ open: false, product: null });
  };

  if (!isAdmin()) {
    return (
      <Alert severity="error">
        Access denied. Admin privileges required.
      </Alert>
    );
  }

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
        <Button onClick={fetchDashboardData} sx={{ ml: 2 }}>
          Retry
        </Button>
      </Alert>
    );
  }

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
        <Typography variant="h4" component="h1">
          <Dashboard sx={{ mr: 1, verticalAlign: 'middle' }} />
          Admin Dashboard
        </Typography>
        <Button
          variant="contained"
          color="primary"
          startIcon={<Add />}
          component={Link}
          to="/products/new"
        >
          Add Product
        </Button>
      </Box>

      {/* Stats Cards */}
      <Grid container spacing={3} mb={4}>
        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Inventory sx={{ mr: 2, fontSize: 40, color: 'primary.main' }} />
                <Box>
                  <Typography variant="h4" component="div">
                    {stats.totalProducts}
                  </Typography>
                  <Typography color="text.secondary">
                    Total Products
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <People sx={{ mr: 2, fontSize: 40, color: 'success.main' }} />
                <Box>
                  <Typography variant="h4" component="div">
                    {stats.totalUsers}
                  </Typography>
                  <Typography color="text.secondary">
                    Total Users
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <Warning sx={{ mr: 2, fontSize: 40, color: 'warning.main' }} />
                <Box>
                  <Typography variant="h4" component="div">
                    {stats.lowStockProducts}
                  </Typography>
                  <Typography color="text.secondary">
                    Low Stock Items
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} sm={6} md={3}>
          <Card>
            <CardContent>
              <Box display="flex" alignItems="center">
                <ShoppingCart sx={{ mr: 2, fontSize: 40, color: 'info.main' }} />
                <Box>
                  <Typography variant="h4" component="div">
                    ${stats.totalRevenue.toLocaleString()}
                  </Typography>
                  <Typography color="text.secondary">
                    Est. Revenue
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Products Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" component="h2" gutterBottom>
            Product Management
          </Typography>
          
          <TableContainer component={Paper} sx={{ mt: 2 }}>
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell>Name</TableCell>
                  <TableCell>Category</TableCell>
                  <TableCell>Price</TableCell>
                  <TableCell>Stock</TableCell>
                  <TableCell>Status</TableCell>
                  <TableCell>Actions</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {products.map((product) => (
                  <TableRow key={product.id}>
                    <TableCell>
                      <Typography variant="body2" fontWeight="medium">
                        {product.name}
                      </Typography>
                      {product.sku && (
                        <Typography variant="caption" color="text.secondary">
                          SKU: {product.sku}
                        </Typography>
                      )}
                    </TableCell>
                    <TableCell>
                      {product.category && (
                        <Chip label={product.category} size="small" variant="outlined" />
                      )}
                    </TableCell>
                    <TableCell>${product.price}</TableCell>
                    <TableCell>
                      <Chip
                        label={product.quantity}
                        color={product.quantity > 10 ? 'success' : product.quantity > 0 ? 'warning' : 'error'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <Chip
                        label={product.quantity > 0 ? 'In Stock' : 'Out of Stock'}
                        color={product.quantity > 0 ? 'success' : 'error'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>
                      <IconButton
                        size="small"
                        color="primary"
                        component={Link}
                        to={`/products/${product.id}`}
                      >
                        <Visibility />
                      </IconButton>
                      <IconButton
                        size="small"
                        color="primary"
                        component={Link}
                        to={`/products/edit/${product.id}`}
                      >
                        <Edit />
                      </IconButton>
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => handleDeleteClick(product)}
                      >
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </CardContent>
      </Card>

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
    </Container>
  );
};

export default AdminDashboard;
