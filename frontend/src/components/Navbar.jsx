import React, { useState } from 'react';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Box, 
  IconButton, 
  Menu, 
  MenuItem,
  Avatar,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { Link, useNavigate } from 'react-router-dom';
import { 
  Store, 
  Add, 
  Home, 
  AccountCircle, 
  Dashboard, 
  Menu as MenuIcon,
  Login as LoginIcon,
  PersonAdd,
  Logout,
  ShoppingCart
} from '@mui/icons-material';
import { useAuth } from '../hooks/useAuth';
import { CartIcon } from './Cart';
import Cart from './Cart';

const Navbar = () => {
  const navigate = useNavigate();
  const { isAuthenticated, user, logout, isAdmin } = useAuth();
  const [anchorEl, setAnchorEl] = useState(null);
  const [cartOpen, setCartOpen] = useState(false);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('md'));

  const handleMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleLogout = () => {
    logout();
    handleClose();
    setMobileMenuOpen(false);
    navigate('/');
  };

  const handleProfile = () => {
    handleClose();
    setMobileMenuOpen(false);
    navigate('/profile');
  };

  const handleCartOpen = () => {
    setCartOpen(true);
    setMobileMenuOpen(false);
  };

  const handleCartClose = () => {
    setCartOpen(false);
  };

  const handleMobileMenuToggle = () => {
    setMobileMenuOpen(!mobileMenuOpen);
  };

  const handleMobileMenuClose = () => {
    setMobileMenuOpen(false);
  };

  const navigateAndCloseMobile = (path) => {
    navigate(path);
    setMobileMenuOpen(false);
  };

  // Mobile Menu Content
  const mobileMenuContent = (
    <Box sx={{ width: 280 }} role="presentation">
      <Box sx={{ p: 2, bgcolor: 'primary.main', color: 'white' }}>
        <Typography variant="h6">Menu</Typography>
        {isAuthenticated && user && (
          <Box sx={{ mt: 1, display: 'flex', alignItems: 'center', gap: 1 }}>
            <Avatar sx={{ width: 32, height: 32, bgcolor: 'secondary.main' }}>
              {user?.firstName?.charAt(0) || <AccountCircle />}
            </Avatar>
            <Box>
              <Typography variant="subtitle2">{user?.fullName}</Typography>
              {user?.role === 'ADMIN' && (
                <Chip 
                  label="Admin" 
                  color="secondary" 
                  size="small" 
                  variant="outlined"
                />
              )}
            </Box>
          </Box>
        )}
      </Box>
      
      <List>
        <ListItem button onClick={() => navigateAndCloseMobile('/')}>
          <ListItemIcon><Home /></ListItemIcon>
          <ListItemText primary="Products" />
        </ListItem>

        {isAuthenticated ? (
          <>
            <ListItem button onClick={handleCartOpen}>
              <ListItemIcon><ShoppingCart /></ListItemIcon>
              <ListItemText primary="Shopping Cart" />
            </ListItem>

            <ListItem button onClick={handleProfile}>
              <ListItemIcon><AccountCircle /></ListItemIcon>
              <ListItemText primary="Profile" />
            </ListItem>

            {isAdmin() && (
              <>
                <Divider />
                <ListItem button onClick={() => navigateAndCloseMobile('/admin')}>
                  <ListItemIcon><Dashboard /></ListItemIcon>
                  <ListItemText primary="Admin Dashboard" />
                </ListItem>
                <ListItem button onClick={() => navigateAndCloseMobile('/products/new')}>
                  <ListItemIcon><Add /></ListItemIcon>
                  <ListItemText primary="Add Product" />
                </ListItem>
              </>
            )}

            <Divider />
            <ListItem button onClick={handleLogout}>
              <ListItemIcon><Logout /></ListItemIcon>
              <ListItemText primary="Logout" />
            </ListItem>
          </>
        ) : (
          <>
            <ListItem button onClick={() => navigateAndCloseMobile('/login')}>
              <ListItemIcon><LoginIcon /></ListItemIcon>
              <ListItemText primary="Login" />
            </ListItem>
            <ListItem button onClick={() => navigateAndCloseMobile('/register')}>
              <ListItemIcon><PersonAdd /></ListItemIcon>
              <ListItemText primary="Sign Up" />
            </ListItem>
          </>
        )}
      </List>
    </Box>
  );

  return (
    <AppBar position="static">
      <Toolbar>
        <Store sx={{ mr: 2 }} />
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          {isMobile ? 'E-Commerce' : 'E-Commerce Product Management'}
        </Typography>
        
        {/* Desktop Menu */}
        {!isMobile && (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Button 
              color="inherit" 
              startIcon={<Home />}
              component={Link} 
              to="/"
            >
              Products
            </Button>

            {isAuthenticated ? (
              <>
                <CartIcon onClick={handleCartOpen} />
                
                {isAdmin() && (
                  <>
                    <Button 
                      color="inherit" 
                      startIcon={<Dashboard />}
                      component={Link} 
                      to="/admin"
                    >
                      Dashboard
                    </Button>
                    <Button 
                      color="inherit" 
                      startIcon={<Add />}
                      component={Link} 
                      to="/products/new"
                    >
                      Add Product
                    </Button>
                  </>
                )}
                
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  {user?.role === 'ADMIN' && (
                    <Chip 
                      label="Admin" 
                      color="secondary" 
                      size="small" 
                      variant="outlined"
                    />
                  )}
                  <IconButton
                    size="large"
                    aria-label="account of current user"
                    aria-controls="menu-appbar"
                    aria-haspopup="true"
                    onClick={handleMenu}
                    color="inherit"
                  >
                    <Avatar sx={{ width: 32, height: 32, bgcolor: 'secondary.main' }}>
                      {user?.firstName?.charAt(0) || <AccountCircle />}
                    </Avatar>
                  </IconButton>
                  <Menu
                    id="menu-appbar"
                    anchorEl={anchorEl}
                    anchorOrigin={{
                      vertical: 'top',
                      horizontal: 'right',
                    }}
                    keepMounted
                    transformOrigin={{
                      vertical: 'top',
                      horizontal: 'right',
                    }}
                    open={Boolean(anchorEl)}
                    onClose={handleClose}
                  >
                    <MenuItem onClick={handleClose} disabled>
                      <Typography variant="subtitle2">
                        {user?.fullName}
                      </Typography>
                    </MenuItem>
                    <MenuItem onClick={handleProfile}>Profile</MenuItem>
                    {isAdmin() && (
                      <MenuItem component={Link} to="/admin" onClick={handleClose}>
                        Admin Dashboard
                      </MenuItem>
                    )}
                    <MenuItem onClick={handleLogout}>Logout</MenuItem>
                  </Menu>
                </Box>
              </>
            ) : (
              <>
                <Button 
                  color="inherit"
                  component={Link} 
                  to="/login"
                >
                  Login
                </Button>
                <Button 
                  color="inherit"
                  component={Link} 
                  to="/register"
                  variant="outlined"
                  sx={{ ml: 1 }}
                >
                  Sign Up
                </Button>
              </>
            )}
          </Box>
        )}

        {/* Mobile Hamburger Menu */}
        {isMobile && (
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            {isAuthenticated && <CartIcon onClick={handleCartOpen} />}
            <IconButton
              color="inherit"
              aria-label="open mobile menu"
              onClick={handleMobileMenuToggle}
              sx={{ ml: 1 }}
            >
              <MenuIcon />
            </IconButton>
          </Box>
        )}
      </Toolbar>

      {/* Mobile Drawer */}
      <Drawer
        anchor="right"
        open={mobileMenuOpen}
        onClose={handleMobileMenuClose}
      >
        {mobileMenuContent}
      </Drawer>

      {/* Cart Dialog */}
      <Dialog 
        open={cartOpen} 
        onClose={handleCartClose} 
        maxWidth="md" 
        fullWidth
      >
        <DialogTitle>Shopping Cart</DialogTitle>
        <DialogContent>
          <Cart />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCartClose}>Close</Button>
        </DialogActions>
      </Dialog>
    </AppBar>
  );
};

export default Navbar;
