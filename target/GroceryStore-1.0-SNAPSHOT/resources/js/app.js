document.addEventListener('DOMContentLoaded', function() {
  const scrollToTopButton = document.getElementById('scrollToTopButton');

  const toggleVisibility = () => {
    if (window.pageYOffset > 300) {
      scrollToTopButton.classList.add('show');
    } else {
      scrollToTopButton.classList.remove('show');
    }
  };

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  };

  window.addEventListener('scroll', toggleVisibility);
  scrollToTopButton.addEventListener('click', scrollToTop);

  // Cleanup event listener on page unload
  window.addEventListener('unload', () => {
    window.removeEventListener('scroll', toggleVisibility);
  });
});

// Loader
window.addEventListener('load', () => {
    setTimeout(() => {
        const loader = document.getElementById('loader');
        const pageContent = document.getElementById('page-content');
        loader.classList.add('hidden');
        if (pageContent) {
            pageContent.classList.add('visible');
        }
        setTimeout(() => {
            loader.style.display = 'none';
        }, 500);
    }, 1000);
});

// Dynamic Dropdown
document.getElementById('hamburger').addEventListener('click', function() {
  this.classList.toggle('change');
});

// Set the current year
document.getElementById('year').textContent = new Date().getFullYear();

// Add to cart success
document.querySelectorAll('.add-to-cart').forEach(button => {
  button.addEventListener('click', function(event) {
    event.preventDefault();
    Swal.fire({
      icon: 'success',
      title: 'Added to Cart',
      text: 'Product has been added to your cart!',
      showConfirmButton: true,
      timer: 3000
    });
  });
});

// Initialize Bootstrap tooltips
const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));