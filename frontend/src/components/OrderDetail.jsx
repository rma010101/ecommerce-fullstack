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
  Stepper,
  Step,
  StepLabel
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import { orderAPI } from '../services/api';
import { useParams, useNavigate } from 'react-router-dom';

const orderSteps = [
  'Order Placed',
  'Confirmed',
  'Processing',
  'Shipped',
  'Out for Delivery',
  'Delivered'
];

const OrderDetail = () => {
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { orderId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrder = async () => {
      try {
        setLoading(true);
        const response = await orderAPI.getOrderById(orderId);
        setOrder(response.data);
      } catch (err) {
        setError(err.response?.data?.error || 'Failed to fetch order details');
      } finally {
        setLoading(false);
      }
    };

    if (orderId) {
      fetchOrder();
    }
  }, [orderId]);

  const handleCancelOrder = async () => {
    if (!window.confirm('Are you sure you want to cancel this order?')) {
      return;
    }

    try {
      const response = await orderAPI.cancelOrder(orderId);
      setOrder(response.data);
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

  const getActiveStep = (status) => {
    switch (status) {
      case 'PENDING':
        return 0;
      case 'CONFIRMED':
        return 1;
      case 'PROCESSING':
        return 2;
      case 'SHIPPED':
        return 3;
      case 'OUT_FOR_DELIVERY':
        return 4;
      case 'DELIVERED':
        return 5;
      default:
        return 0;
    }
  };

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
        <Alert severity="error">{error}</Alert>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/orders')}
          sx={{ mt: 2 }}
        >
          Back to Orders
        </Button>
      </Box>
    );
  }

  if (!order) {
    return (
      <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
        <Alert severity="info">Order not found</Alert>
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate('/orders')}
          sx={{ mt: 2 }}
        >
          Back to Orders
        </Button>
      </Box>
    );
  }

  return (
    <Box sx={{ maxWidth: 1000, mx: 'auto', p: 2 }}>
      <Button
        startIcon={<ArrowBackIcon />}
        onClick={() => navigate('/orders')}
        sx={{ mb: 2 }}
      >
        Back to Orders
      </Button>

      <Typography variant="h4" gutterBottom>
        Order Details
      </Typography>

      <Grid container spacing={3}>
        {/* Order Summary */}
        <Grid item xs={12} md={8}>
          <Card sx={{ mb: 3 }}>
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

              {canCancelOrder(order.status) && (
                <Button
                  variant="outlined"
                  color="error"
                  size="small"
                  onClick={handleCancelOrder}
                  sx={{ mb: 2 }}
                >
                  Cancel Order
                </Button>
              )}

              {/* Order Progress */}
              {!['CANCELLED', 'RETURNED', 'REFUNDED'].includes(order.status) && (
                <Box sx={{ mt: 3 }}>
                  <Typography variant="h6" gutterBottom>
                    Order Progress
                  </Typography>
                  <Stepper activeStep={getActiveStep(order.status)} alternativeLabel>
                    {orderSteps.map((label) => (
                      <Step key={label}>
                        <StepLabel>{label}</StepLabel>
                      </Step>
                    ))}
                  </Stepper>
                </Box>
              )}
            </CardContent>
          </Card>

          {/* Order Items */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Order Items
              </Typography>
              
              <List>
                {order.items.map((item, index) => (
                  <React.Fragment key={index}>
                    <ListItem sx={{ px: 0 }}>
                      <ListItemText
                        primary={item.productName}
                        secondary={`SKU: ${item.productSku} | Quantity: ${item.quantity} × $${item.price.toFixed(2)}`}
                      />
                      <Typography variant="body1" sx={{ fontWeight: 'medium' }}>
                        ${item.subtotal.toFixed(2)}
                      </Typography>
                    </ListItem>
                    {index < order.items.length - 1 && <Divider />}
                  </React.Fragment>
                ))}
              </List>
              
              <Divider sx={{ my: 2 }} />
              
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Typography>Subtotal:</Typography>
                <Typography>${order.totalAmount.toFixed(2)}</Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Typography>Shipping:</Typography>
                <Typography>
                  {order.shippingCost === 0 ? 'FREE' : `$${order.shippingCost.toFixed(2)}`}
                </Typography>
              </Box>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                <Typography>Tax:</Typography>
                <Typography>${order.taxAmount.toFixed(2)}</Typography>
              </Box>
              <Divider sx={{ my: 1 }} />
              <Box sx={{ display: 'flex', justifyContent: 'space-between', fontWeight: 'bold' }}>
                <Typography variant="h6">Total:</Typography>
                <Typography variant="h6">${order.finalAmount.toFixed(2)}</Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Sidebar */}
        <Grid item xs={12} md={4}>
          {/* Shipping Address */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Shipping Address
              </Typography>
              <Typography>
                {order.shippingAddress.firstName} {order.shippingAddress.lastName}<br />
                {order.shippingAddress.company && `${order.shippingAddress.company}\n`}
                {order.shippingAddress.addressLine1}<br />
                {order.shippingAddress.addressLine2 && `${order.shippingAddress.addressLine2}\n`}
                {order.shippingAddress.city}, {order.shippingAddress.state} {order.shippingAddress.postalCode}<br />
                {order.shippingAddress.country}
              </Typography>
              {order.shippingAddress.phoneNumber && (
                <Typography sx={{ mt: 1 }}>
                  Phone: {order.shippingAddress.phoneNumber}
                </Typography>
              )}
            </CardContent>
          </Card>

          {/* Payment Information */}
          <Card sx={{ mb: 3 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Payment Information
              </Typography>
              <Typography>
                Method: {order.paymentInfo?.paymentMethod?.replace('_', ' ') || 'N/A'}
              </Typography>
              <Typography>
                Status: {order.paymentInfo?.paymentStatus?.replace('_', ' ') || 'N/A'}
              </Typography>
              {order.paymentInfo?.transactionId && (
                <Typography variant="body2" color="text.secondary">
                  Transaction ID: {order.paymentInfo.transactionId}
                </Typography>
              )}
            </CardContent>
          </Card>

          {/* Delivery Information */}
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Delivery Information
              </Typography>
              {order.estimatedDeliveryDate && (
                <Typography gutterBottom>
                  <strong>Estimated Delivery:</strong><br />
                  {new Date(order.estimatedDeliveryDate).toLocaleDateString()}
                </Typography>
              )}
              {order.deliveredDate && (
                <Typography gutterBottom>
                  <strong>Delivered On:</strong><br />
                  {new Date(order.deliveredDate).toLocaleDateString()}
                </Typography>
              )}
              {order.trackingNumber && (
                <Button
                  variant="outlined"
                  size="small"
                  onClick={() => navigate(`/orders/track/${order.trackingNumber}`)}
                  sx={{ mt: 1 }}
                >
                  Track Package
                </Button>
              )}
            </CardContent>
          </Card>

          {order.notes && (
            <Card sx={{ mt: 3 }}>
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Order Notes
                </Typography>
                <Typography>{order.notes}</Typography>
              </CardContent>
            </Card>
          )}
        </Grid>
      </Grid>
    </Box>
  );
};

export default OrderDetail;
