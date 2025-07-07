import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Grid,
  Chip,
  Button,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Alert,
  Container,
} from '@mui/material';
import { Edit, Person, Email, Badge, Schedule } from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';

const UserProfile = () => {
  const { user } = useAuth();
  const [editDialog, setEditDialog] = useState(false);
  const [editData, setEditData] = useState({
    firstName: user?.firstName || '',
    lastName: user?.lastName || '',
    email: user?.email || '',
  });
  const [updateMessage, setUpdateMessage] = useState('');

  const handleEditClick = () => {
    setEditData({
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
    });
    setEditDialog(true);
  };

  const handleEditChange = (field, value) => {
    setEditData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleEditSave = async () => {
    try {
      // TODO: Implement user update API call
      setUpdateMessage('Profile update functionality coming soon!');
      setEditDialog(false);
    } catch (err) {
      console.error('Failed to update profile:', err);
      setUpdateMessage('Failed to update profile');
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  if (!user) {
    return (
      <Alert severity="error">
        User not found. Please log in again.
      </Alert>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Typography variant="h4" component="h1" gutterBottom>
        User Profile
      </Typography>
      
      {updateMessage && (
        <Alert severity="info" sx={{ mb: 2 }} onClose={() => setUpdateMessage('')}>
          {updateMessage}
        </Alert>
      )}

      <Grid container spacing={3}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                <Typography variant="h6" component="h2">
                  Personal Information
                </Typography>
                <Button
                  startIcon={<Edit />}
                  onClick={handleEditClick}
                  variant="outlined"
                  size="small"
                >
                  Edit
                </Button>
              </Box>

              <Grid container spacing={2}>
                <Grid item xs={12} sm={6}>
                  <Box display="flex" alignItems="center" mb={2}>
                    <Person sx={{ mr: 1, color: 'text.secondary' }} />
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        Full Name
                      </Typography>
                      <Typography variant="body1">
                        {user.firstName} {user.lastName}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box display="flex" alignItems="center" mb={2}>
                    <Email sx={{ mr: 1, color: 'text.secondary' }} />
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        Email
                      </Typography>
                      <Typography variant="body1">
                        {user.email}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box display="flex" alignItems="center" mb={2}>
                    <Badge sx={{ mr: 1, color: 'text.secondary' }} />
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        Username
                      </Typography>
                      <Typography variant="body1">
                        {user.username}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>

                <Grid item xs={12} sm={6}>
                  <Box display="flex" alignItems="center" mb={2}>
                    <Schedule sx={{ mr: 1, color: 'text.secondary' }} />
                    <Box>
                      <Typography variant="body2" color="text.secondary">
                        Member Since
                      </Typography>
                      <Typography variant="body1">
                        {formatDate(user.createdAt)}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>
              </Grid>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" component="h2" gutterBottom>
                Account Status
              </Typography>

              <Box mb={2}>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Role
                </Typography>
                <Chip
                  label={user.role}
                  color={user.role === 'ADMIN' ? 'primary' : 'default'}
                  variant="outlined"
                />
              </Box>

              <Box mb={2}>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Account Status
                </Typography>
                <Chip
                  label={user.enabled ? 'Active' : 'Inactive'}
                  color={user.enabled ? 'success' : 'error'}
                  variant="outlined"
                />
              </Box>

              <Box>
                <Typography variant="body2" color="text.secondary" gutterBottom>
                  Last Updated
                </Typography>
                <Typography variant="body2">
                  {formatDate(user.updatedAt)}
                </Typography>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Edit Profile Dialog */}
      <Dialog open={editDialog} onClose={() => setEditDialog(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Profile</DialogTitle>
        <DialogContent>
          <Grid container spacing={2} sx={{ mt: 1 }}>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="First Name"
                value={editData.firstName}
                onChange={(e) => handleEditChange('firstName', e.target.value)}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                fullWidth
                label="Last Name"
                value={editData.lastName}
                onChange={(e) => handleEditChange('lastName', e.target.value)}
              />
            </Grid>
            <Grid item xs={12}>
              <TextField
                fullWidth
                label="Email"
                type="email"
                value={editData.email}
                onChange={(e) => handleEditChange('email', e.target.value)}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setEditDialog(false)}>Cancel</Button>
          <Button onClick={handleEditSave} variant="contained">
            Save Changes
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default UserProfile;
