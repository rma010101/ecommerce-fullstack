import React from 'react';
import {
  Box,
  Paper,
  Typography,
  Button,
  Card,
  CardContent,
  List,
  ListItem,
  ListItemText,
  Divider,
  Chip
} from '@mui/material';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import { useNavigate } from 'react-router-dom';

const OrderSuccess = ({ order }) => {
  const navigate = useNavigate();

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
      case 'DELIVERED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  return (
    <Paper elevation={3} sx={{ p: 4, maxWidth: 600, mx: 'auto', textAlign: 'center' }}>
      <CheckCircleIcon sx={{ fontSize: 64, color: 'success.main', mb: 2 }} />
      
      <Typography variant="h4" gutterBottom color="success.main">
        Order Placed Successfully!
      </Typography>
      
      <Typography variant="h6" gutterBottom>
        Thank you for your order
      </Typography>
      
      <Card sx={{ mt: 3, mb: 3 }}>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Order Details
          </Typography>
          
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography><strong>Order Number:</strong></Typography>
            <Typography>{order.orderNumber}</Typography>
          </Box>
          
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography><strong>Order Date:</strong></Typography>
            <Typography>
              {new Date(order.orderDate).toLocaleDateString()}
            </Typography>
          </Box>
          
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography><strong>Status:</strong></Typography>
            <Chip 
              label={order.status.replace('_', ' ')} 
              color={getStatusColor(order.status)}
              size="small"
            />
          </Box>
          
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
            <Typography><strong>Total Amount:</strong></Typography>
            <Typography variant="h6" color="primary">
              ${order.finalAmount.toFixed(2)}
            </Typography>
          </Box>
          
          {order.estimatedDeliveryDate && (
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
              <Typography><strong>Estimated Delivery:</strong></Typography>
              <Typography>
                {new Date(order.estimatedDeliveryDate).toLocaleDateString()}
              </Typography>
            </Box>
          )}
        </CardContent>
      </Card>
      
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
                    secondary={`SKU: ${item.productSku}`}
                  />
                  <Box sx={{ textAlign: 'right' }}>
                    <Typography>Qty: {item.quantity}</Typography>
                    <Typography>${item.subtotal.toFixed(2)}</Typography>
                  </Box>
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
        </CardContent>
      </Card>
      
      <Box sx={{ mt: 4, display: 'flex', gap: 2, justifyContent: 'center' }}>
        <Button 
          variant="contained" 
          onClick={() => navigate('/orders')}
        >
          View My Orders
        </Button>
        <Button 
          variant="outlined" 
          onClick={() => navigate('/')}
        >
          Continue Shopping
        </Button>
      </Box>
      
      <Typography variant="body2" color="text.secondary" sx={{ mt: 3 }}>
        You will receive an email confirmation shortly with your order details and tracking information.
      </Typography>
    </Paper>
  );
};

export default OrderSuccess;
