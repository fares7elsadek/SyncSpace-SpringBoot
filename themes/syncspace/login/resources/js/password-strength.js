/**
 * Password Strength Indicator
 * Provides visual feedback for password strength
 */
class PasswordStrength {
  constructor() {
    this.init();
  }
  
  init() {
    const passwordInputs = document.querySelectorAll('input[name="password"]:not([name="password-confirm"])');
    
    passwordInputs.forEach(input => {
      if (this.shouldShowStrengthMeter(input)) {
        this.addStrengthMeter(input);
        this.bindEvents(input);
      }
    });
  }
  
  shouldShowStrengthMeter(input) {
    // Only show on registration and password update forms
    const form = input.closest('form');
    return form && (
      form.id === 'kc-register-form' ||
      form.id === 'kc-passwd-update-form' ||
      form.action.includes('registration') ||
      form.action.includes('update-password')
    );
  }
  
  addStrengthMeter(input) {
    const wrapper = input.closest('.syncspace-form-group');
    if (!wrapper || wrapper.querySelector('.password-strength')) return;
    
    const strengthContainer = document.createElement('div');
    strengthContainer.className = 'password-strength';
    
    strengthContainer.innerHTML = `
      <div class="strength-meter">
        <div class="strength-fill"></div>
      </div>
      <span class="strength-text">Password strength</span>
    `;
    
    wrapper.appendChild(strengthContainer);
  }
  
  bindEvents(input) {
    input.addEventListener('input', (e) => {
      this.updateStrength(e.target);
    });
  }
  
  updateStrength(input) {
    const password = input.value;
    const strength = this.calculateStrength(password);
    const wrapper = input.closest('.syncspace-form-group');
    const strengthFill = wrapper.querySelector('.strength-fill');
    const strengthText = wrapper.querySelector('.strength-text');
    
    if (!strengthFill || !strengthText) return;
    
    // Remove existing classes
    strengthFill.className = 'strength-fill';
    
    if (password.length === 0) {
      strengthText.textContent = 'Password strength';
      return;
    }
    
    if (strength.score < 2) {
      strengthFill.classList.add('weak');
      strengthText.textContent = 'Weak password';
      strengthText.style.color = 'var(--error)';
    } else if (strength.score < 3) {
      strengthFill.classList.add('fair');
      strengthText.textContent = 'Fair password';
      strengthText.style.color = 'var(--warning)';
    } else if (strength.score < 4) {
      strengthFill.classList.add('good');
      strengthText.textContent = 'Good password';
      strengthText.style.color = 'var(--info)';
    } else {
      strengthFill.classList.add('strong');
      strengthText.textContent = 'Strong password';
      strengthText.style.color = 'var(--success)';
    }
  }
  
  calculateStrength(password) {
    let score = 0;
    const feedback = [];
    
    // Length check
    if (password.length >= 8) score++;
    else feedback.push('Use at least 8 characters');
    
    if (password.length >= 12) score++;
    
    // Character variety
    if (/[a-z]/.test(password)) score++;
    else feedback.push('Add lowercase letters');
    
    if (/[A-Z]/.test(password)) score++;
    else feedback.push('Add uppercase letters');
    
    if (/[0-9]/.test(password)) score++;
    else feedback.push('Add numbers');
    
    if (/[^a-zA-Z0-9]/.test(password)) score++;
    else feedback.push('Add symbols');
    
    // Common patterns (reduce score)
    if (/(.)\1{2,}/.test(password)) score--; // Repeated characters
    if (/123|234|345|456|567|678|789|890|abc|bcd|cde/.test(password.toLowerCase())) score--; // Sequential
    if (/password|123456|qwerty|admin|login/.test(password.toLowerCase())) score = 0; // Common passwords
    
    return {
      score: Math.max(0, Math.min(4, score)),
      feedback
    };
  }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
  new PasswordStrength();
});
