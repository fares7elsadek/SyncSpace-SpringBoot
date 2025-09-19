/**
 * Form Interactions
 * Handles form animations, validation, and user interactions
 */
class FormInteractions {
  constructor() {
    this.init();
  }
  
  init() {
    this.setupPasswordToggle();
    this.setupFormValidation();
    this.setupInputAnimations();
    this.setupButtonEffects();
    this.setupSocialButtons();
  }
  
  setupPasswordToggle() {
    const toggleButtons = document.querySelectorAll('.password-toggle');
    
    toggleButtons.forEach(button => {
      button.addEventListener('click', (e) => {
        e.preventDefault();
        const input = button.previousElementSibling;
        
        if (input && input.type === 'password') {
          input.type = 'text';
          button.innerHTML = '<svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor"><path fill-rule="evenodd" d="M3.707 2.293a1 1 0 00-1.414 1.414l14 14a1 1 0 001.414-1.414l-1.473-1.473A10.014 10.014 0 0019.542 10C18.268 5.943 14.478 3 10 3a9.958 9.958 0 00-4.512 1.074l-1.78-1.781zm4.261 4.26l1.514 1.515a2.003 2.003 0 012.45 2.45l1.514 1.514a4 4 0 00-5.478-5.478z" clip-rule="evenodd"/><path d="M12.454 16.697L9.75 13.992a4 4 0 01-4.404-4.404l-2.172-2.172A9.961 9.961 0 00.458 10c1.274 4.057 5.065 7 9.542 7a9.959 9.959 0 002.454-.303z"/></svg>';
          button.setAttribute('aria-label', 'Hide password');
        } else if (input) {
          input.type = 'password';
          button.innerHTML = '<svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor"><path d="M10 12a2 2 0 100-4 2 2 0 000 4z"/><path fill-rule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clip-rule="evenodd"/></svg>';
          button.setAttribute('aria-label', 'Show password');
        }
      });
    });
  }
  
  setupFormValidation() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
      form.addEventListener('submit', (e) => {
        const submitButton = form.querySelector('button[type="submit"]');
        if (submitButton && !submitButton.disabled) {
          this.showButtonLoading(submitButton);
        }
      });
      
      const inputs = form.querySelectorAll('.syncspace-input');
      inputs.forEach(input => {
        input.addEventListener('blur', () => this.validateInput(input));
        input.addEventListener('input', () => {
          if (input.classList.contains('error')) {
            this.validateInput(input);
          }
        });
      });
    });
  }
  
  validateInput(input) {
    const wrapper = input.closest('.syncspace-form-group');
    const existingError = wrapper.querySelector('.kc-field-error');
    
    if (existingError) {
      existingError.remove();
      input.classList.remove('error');
    }
    
    // Basic validation
    if (input.hasAttribute('required') && !input.value.trim()) {
      this.showError(wrapper, input, 'This field is required');
      return false;
    }
    
    // Email validation
    if (input.type === 'email' && input.value) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(input.value)) {
        this.showError(wrapper, input, 'Please enter a valid email address');
        return false;
      }
    }
    
    // Password confirmation
    if (input.name === 'password-confirm') {
      const passwordInput = input.form.querySelector('input[name="password"]');
      if (passwordInput && input.value !== passwordInput.value) {
        this.showError(wrapper, input, 'Passwords do not match');
        return false;
      }
    }
    
    return true;
  }
  
  showError(wrapper, input, message) {
    input.classList.add('error');
    const error = document.createElement('span');
    error.className = 'kc-field-error';
    error.textContent = message;
    error.setAttribute('aria-live', 'polite');
    wrapper.appendChild(error);
  }
  
  showButtonLoading(button) {
    const buttonText = button.querySelector('.button-text') || button.childNodes[0];
    const loader = button.querySelector('.button-loader');
    
    if (!loader) {
      button.innerHTML = '<span class="button-loader"><svg class="spinner" width="20" height="20" viewBox="0 0 20 20" fill="none"><circle cx="10" cy="10" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-dasharray="50.265" stroke-dashoffset="37.699"/></svg></span>';
    } else {
      if (buttonText) buttonText.style.display = 'none';
      loader.style.display = 'flex';
    }
    
    button.disabled = true;
  }
  
  setupInputAnimations() {
    const inputs = document.querySelectorAll('.syncspace-input');
    
    inputs.forEach(input => {
      const wrapper = input.closest('.input-wrapper');
      const icon = wrapper?.querySelector('.input-icon');
      
      input.addEventListener('focus', () => {
        wrapper?.classList.add('focused');
        if (icon) {
          icon.style.color = 'var(--border-focus)';
          icon.style.transform = 'scale(1.1)';
        }
      });
      
      input.addEventListener('blur', () => {
        wrapper?.classList.remove('focused');
        if (icon) {
          icon.style.color = 'var(--text-muted)';
          icon.style.transform = 'scale(1)';
        }
      });
    });
  }
  
  setupButtonEffects() {
    const buttons = document.querySelectorAll('.syncspace-button');
    
    buttons.forEach(button => {
      button.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-1px)';
      });
      
      button.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
      });
      
      button.addEventListener('mousedown', function() {
        this.style.transform = 'translateY(1px)';
      });
      
      button.addEventListener('mouseup', function() {
        this.style.transform = 'translateY(-1px)';
      });
    });
  }
  
  setupSocialButtons() {
    const socialButtons = document.querySelectorAll('.social-button');
    
    socialButtons.forEach(button => {
      button.addEventListener('mouseenter', function() {
        this.style.transform = 'translateY(-2px)';
        this.style.boxShadow = '0 4px 12px rgba(124, 58, 237, 0.2)';
      });
      
      button.addEventListener('mouseleave', function() {
        this.style.transform = 'translateY(0)';
        this.style.boxShadow = 'none';
      });
    });
  }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  new FormInteractions();
});
