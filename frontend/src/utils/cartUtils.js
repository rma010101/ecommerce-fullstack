// Cart utility functions

export const addToCart = (product, quantity = 1) => {
  const savedCart = localStorage.getItem('cart');
  let cartItems = savedCart ? JSON.parse(savedCart) : [];
  
  const existingItem = cartItems.find(item => item.id === product.id);
  
  if (existingItem) {
    existingItem.quantity += quantity;
  } else {
    cartItems.push({
      id: product.id,
      name: product.name,
      price: product.price,
      quantity: quantity,
    });
  }
  
  localStorage.setItem('cart', JSON.stringify(cartItems));
  
  // Trigger cart update event
  window.dispatchEvent(new Event('cartUpdated'));
  
  return cartItems;
};

export const getCartItems = () => {
  const savedCart = localStorage.getItem('cart');
  return savedCart ? JSON.parse(savedCart) : [];
};

export const getCartItemCount = () => {
  const items = getCartItems();
  return items.reduce((sum, item) => sum + item.quantity, 0);
};

export const clearCart = () => {
  localStorage.removeItem('cart');
  window.dispatchEvent(new Event('cartUpdated'));
};
