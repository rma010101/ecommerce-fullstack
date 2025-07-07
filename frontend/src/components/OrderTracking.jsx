import React, { useState, useEffect } from 'react';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Card,
  CardContent,
  Chip,
  Alert,
  CircularProgress,
  Stepper,
  Step,
  StepLabel,
  StepContent
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import { orderAPI } from '../services/api';
import { useParams } from 'react-router-dom';

const trackingSteps = [
  {
    label: 'Order Placed',
    description: 'Your order has been placed and is being processed.'
  },
  {
    label: 'Order Confirmed',
    description: 'Your order has been confirmed and payment processed.'
  },
  {
    label: 'Processing',
    description: 'Your order is being prepared for shipment.'
  },
  {
    label: 'Shipped',
    description: 'Your order has been shipped and is on its way.'
  },
  {
    label: 'Out for Delivery',
    description: 'Your package is out for delivery and will arrive soon.'
  },
  {
    label: 'Delivered',
    description: 'Your order has been delivered successfully.'
  }
];

const OrderTracking = () => {
  const [trackingNumber, setTrackingNumber] = useState('');
  const [orderInfo, setOrderInfo] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const { trackingNum } = useParams();

  useEffect(() => {
    const handleTrackFromParam = async (trackingNumberToSearch) => {
      if (!trackingNumberToSearch.trim()) {
        setError('Please enter a tracking number');
        return;
      }

      try {
        setLoading(true);
        setError('');
        const response = await orderAPI.trackOrder(trackingNumberToSearch);
        setOrderInfo(response.data);
      } catch (error) {
        setError('Tracking number not found or invalid');
        setOrderInfo(null);
        console.error('Tracking error:', error);
      } finally {
        setLoading(false);
      }
    };

    if (trackingNum) {
      setTrackingNumber(trackingNum);
      handleTrackFromParam(trackingNum);
    }
  }, [trackingNum]);

  const handleTrack = async (trackingNumberToSearch = trackingNumber) => {
    if (!trackingNumberToSearch.trim()) {
      setError('Please enter a tracking number');
      return;
    }

    try {
      setLoading(true);
      setError('');
      const response = await orderAPI.trackOrder(trackingNumberToSearch);
      setOrderInfo(response.data);
    } catch (error) {
      setError('Tracking number not found or invalid');
      setOrderInfo(null);
      console.error('Tracking error:', error);
    } finally {
      setLoading(false);
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
      default:
        return 'default';
    }
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

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', p: 2 }}>
      <Typography variant="h4" gutterBottom sx={{ textAlign: 'center' }}>
        Track Your Order
      </Typography>

      <Paper sx={{ p: 3, mb: 3 }}>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'flex-start' }}>
          <TextField
            fullWidth
            label="Tracking Number"
            placeholder="Enter your tracking number"
            value={trackingNumber}
            onChange={(e) => setTrackingNumber(e.target.value)}
            error={!!error && !orderInfo}
            helperText={error && !orderInfo ? error : 'Enter the tracking number from your order confirmation email'}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleTrack();
              }
            }}
          />
          <Button
            variant="contained"
            onClick={() => handleTrack()}
            disabled={loading}
            startIcon={loading ? <CircularProgress size={20} /> : <SearchIcon />}
            sx={{ minWidth: 120, height: 56 }}
          >
            {loading ? 'Tracking...' : 'Track'}
          </Button>
        </Box>
      </Paper>

      {error && orderInfo === null && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {orderInfo && (
        <Card>
          <CardContent>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
              <Box>
                <Typography variant="h6" gutterBottom>
                  Order #{orderInfo.orderNumber}
                </Typography>
                <Typography color="text.secondary" gutterBottom>
                  <LocalShippingIcon sx={{ fontSize: 16, mr: 0.5 }} />
                  Tracking: {orderInfo.trackingNumber}
                </Typography>
              </Box>
              <Chip 
                label={orderInfo.status.replace('_', ' ')} 
                color={getStatusColor(orderInfo.status)}
                size="large"
              />
            </Box>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
              <Box>
                <Typography variant="body2" color="text.secondary">
                  Order Date
                </Typography>
                <Typography>
                  {new Date(orderInfo.orderDate).toLocaleDateString()}
                </Typography>
              </Box>
              {orderInfo.estimatedDeliveryDate && (
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Estimated Delivery
                  </Typography>
                  <Typography>
                    {new Date(orderInfo.estimatedDeliveryDate).toLocaleDateString()}
                  </Typography>
                </Box>
              )}
              {orderInfo.deliveredDate && (
                <Box>
                  <Typography variant="body2" color="text.secondary">
                    Delivered On
                  </Typography>
                  <Typography color="success.main">
                    {new Date(orderInfo.deliveredDate).toLocaleDateString()}
                  </Typography>
                </Box>
              )}
            </Box>

            {orderInfo.status !== 'CANCELLED' && (
              <Box sx={{ mt: 4 }}>
                <Typography variant="h6" gutterBottom>
                  Tracking Progress
                </Typography>
                <Stepper activeStep={getActiveStep(orderInfo.status)} orientation="vertical">
                  {trackingSteps.map((step, index) => (
                    <Step key={step.label}>
                      <StepLabel>
                        <Typography variant="subtitle1">
                          {step.label}
                        </Typography>
                      </StepLabel>
                      <StepContent>
                        <Typography variant="body2" color="text.secondary">
                          {step.description}
                        </Typography>
                        {index === getActiveStep(orderInfo.status) && (
                          <Typography variant="body2" color="primary" sx={{ mt: 1, fontWeight: 'medium' }}>
                            Current Status
                          </Typography>
                        )}
                      </StepContent>
                    </Step>
                  ))}
                </Stepper>
              </Box>
            )}

            {orderInfo.status === 'CANCELLED' && (
              <Alert severity="warning" sx={{ mt: 3 }}>
                This order has been cancelled. If you have any questions, please contact customer support.
              </Alert>
            )}

            <Box sx={{ mt: 4, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
              <Typography variant="body2" color="text.secondary">
                <strong>Need help?</strong> If you have any questions about your order or delivery, 
                please contact our customer support team.
              </Typography>
            </Box>
          </CardContent>
        </Card>
      )}

      {!orderInfo && !loading && (
        <Card>
          <CardContent sx={{ textAlign: 'center', py: 6 }}>
            <LocalShippingIcon sx={{ fontSize: 64, color: 'grey.400', mb: 2 }} />
            <Typography variant="h6" gutterBottom>
              Enter your tracking number above
            </Typography>
            <Typography color="text.secondary">
              You can find your tracking number in your order confirmation email
            </Typography>
          </CardContent>
        </Card>
      )}
    </Box>
  );
};

export default OrderTracking;
