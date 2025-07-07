import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  Card,
  CardContent,
  Button,
  Chip,
  List,
  ListItem,
  ListItemText,
  Divider,
  Alert,
  CircularProgress,
  Grid,
  TextField,
  InputAdornment,
  Pagination
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import { orderAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';

const OrderList = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await orderAPI.getMyOrders({
          page,
          size: 10,
          sortBy: 'orderDate',
          sortDir: 'desc'
        });
        setOrders(response.data.content || []);
        setTotalPages(response.data.totalPages || 0);
      } catch (err) {
        setError('Failed to fetch orders');
        console.error('Error fetching orders:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [page]);

  const refreshOrders = async () => {
    try {
      const response = await orderAPI.getMyOrders({
        page,
        size: 10,
        sortBy: 'orderDate',
        sortDir: 'desc'
      });
      setOrders(response.data.content || []);
      setTotalPages(response.data.totalPages || 0);
    } catch (err) {
      setError('Failed to fetch orders');
      console.error('Error fetching orders:', err);
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!window.confirm('Are you sure you want to cancel this order?')) {
      return;
    }

    try {
      await orderAPI.cancelOrder(orderId);
      // Refresh orders
      refreshOrders();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to cancel order');
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'CONFIRMED':
        return 'info';
      case 'PROCESSING':
        return 'primary';
      case 'SHIPPED':
        return 'secondary';
      case 'OUT_FOR_DELIVERY':
        return 'secondary';
      case 'DELIVERED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      case 'RETURNED':
        return 'error';
      case 'REFUNDED':
        return 'error';
      default:
        return 'default';
    }
  };

  const canCancelOrder = (status) => {
    return ['PENDING', 'CONFIRMED', 'PROCESSING'].includes(status);
  };

  const filteredOrders = orders.filter(order =>
    order.orderNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
    order.items.some(item => 
      item.productName.toLowerCase().includes(searchTerm.toLowerCase())
    )
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 1200, mx: 'auto', p: 2 }}>
      <Typography variant="h4" gutterBottom>
        My Orders
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}

      <Paper sx={{ p: 2, mb: 3 }}>
        <TextField
          fullWidth
          placeholder="Search orders by order number or product name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <SearchIcon />
              </InputAdornment>
            ),
          }}
        />
      </Paper>

      {filteredOrders.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" gutterBottom>
            {orders.length === 0 ? 'No orders found' : 'No orders match your search'}
          </Typography>
          <Typography color="text.secondary" gutterBottom>
            {orders.length === 0 
              ? 'You haven\'t placed any orders yet.'
              : 'Try adjusting your search terms.'
            }
          </Typography>
          <Button 
            variant="contained" 
            onClick={() => navigate('/')}
            sx={{ mt: 2 }}
          >
            Start Shopping
          </Button>
        </Paper>
      ) : (
        <>
          <Grid container spacing={2}>
            {filteredOrders.map((order) => (
              <Grid item xs={12} key={order.id}>
                <Card>
                  <CardContent>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                      <Box>
                        <Typography variant="h6" gutterBottom>
                          Order #{order.orderNumber}
                        </Typography>
                        <Typography color="text.secondary" gutterBottom>
                          Placed on {new Date(order.orderDate).toLocaleDateString()}
                        </Typography>
                        <Chip 
                          label={order.status.replace('_', ' ')} 
                          color={getStatusColor(order.status)}
                          size="small"
                          sx={{ mb: 1 }}
                        />
                      </Box>
                      <Box sx={{ textAlign: 'right' }}>
                        <Typography variant="h6" color="primary">
                          ${order.finalAmount.toFixed(2)}
                        </Typography>
                        {order.trackingNumber && (
                          <Typography variant="body2" color="text.secondary">
                            <LocalShippingIcon sx={{ fontSize: 16, mr: 0.5 }} />
                            {order.trackingNumber}
                          </Typography>
                        )}
                      </Box>
                    </Box>

                    <List sx={{ py: 0 }}>
                      {order.items.map((item, index) => (
                        <React.Fragment key={index}>
                          <ListItem sx={{ px: 0 }}>
                            <ListItemText
                              primary={item.productName}
                              secondary={`Quantity: ${item.quantity} × $${item.price.toFixed(2)}`}
                            />
                            <Typography variant="body2">
                              ${item.subtotal.toFixed(2)}
                            </Typography>
                          </ListItem>
                          {index < order.items.length - 1 && <Divider />}
                        </React.Fragment>
                      ))}
                    </List>

                    <Box sx={{ display: 'flex', gap: 1, mt: 2 }}>
                      <Button
                        variant="outlined"
                        size="small"
                        onClick={() => navigate(`/orders/${order.id}`)}
                      >
                        View Details
                      </Button>
                      
                      {order.trackingNumber && (
                        <Button
                          variant="outlined"
                          size="small"
                          onClick={() => navigate(`/orders/track/${order.trackingNumber}`)}
                        >
                          Track Order
                        </Button>
                      )}
                      
                      {canCancelOrder(order.status) && (
                        <Button
                          variant="outlined"
                          color="error"
                          size="small"
                          onClick={() => handleCancelOrder(order.id)}
                        >
                          Cancel Order
                        </Button>
                      )}
                    </Box>

                    {order.estimatedDeliveryDate && order.status !== 'DELIVERED' && (
                      <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                        Estimated delivery: {new Date(order.estimatedDeliveryDate).toLocaleDateString()}
                      </Typography>
                    )}
                  </CardContent>
                </Card>
              </Grid>
            ))}
          </Grid>

          {totalPages > 1 && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
              <Pagination
                count={totalPages}
                page={page + 1}
                onChange={(e, value) => setPage(value - 1)}
                color="primary"
              />
            </Box>
          )}
        </>
      )}
    </Box>
  );
};

export default OrderList;
