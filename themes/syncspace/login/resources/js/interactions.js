/**
 * Enhanced Form Interactions with Working Features
 * SyncSpace Keycloak Theme
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize all interactive features
    initPasswordToggle();
    initFormValidation();
    initPasswordStrength();
    initButtonEffects();
    initInputEffects();
    initSocialButtons();
    initAccessibilityControls();
    initErrorHandling();
});

/**
 * Password Toggle Functionality
 */
function initPasswordToggle() {
    const toggleButtons = document.querySelectorAll('.toggle-password');

    toggleButtons.forEach(button => {
        button.addEventListener('click', function() {
            const input = this.parentElement.querySelector('input[type="password"], input[type="text"]');
            const eyeOpen = this.querySelector('.eye-open');
            const eyeClosed = this.querySelector('.eye-closed');

            if (input.type === 'password') {
                input.type = 'text';
                eyeOpen.style.display = 'none';
                eyeClosed.style.display = 'block';
                this.setAttribute('aria-label', 'Hide password');
            } else {
                input.type = 'password';
                eyeOpen.style.display = 'block';
                eyeClosed.style.display = 'none';
                this.setAttribute('aria-label', 'Show password');
            }
        });
    });
}

/**
 * Enhanced Form Validation
 */
function initFormValidation() {
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        // Handle form submission
        form.addEventListener('submit', function(e) {
            const submitBtn = this.querySelector('button[type="submit"]');
            if (submitBtn && !submitBtn.disabled) {
                showButtonLoading(submitBtn);
            }
        });

        // Real-time validation for inputs
        const inputs = form.querySelectorAll('.form-input');
        inputs.forEach(input => {
            input.addEventListener('blur', () => validateInput(input));
            input.addEventListener('input', () => {
                if (input.classList.contains('error')) {
                    validateInput(input);
                }
            });
        });
    });
}

function validateInput(input) {
    const wrapper = input.closest('.form-field');
    const existingError = wrapper.querySelector('.field-error');

    // Remove existing error
    if (existingError) {
        existingError.remove();
        input.classList.remove('error');
    }

    let isValid = true;
    let errorMessage = '';

    // Required field validation
    if (input.hasAttribute('required') && !input.value.trim()) {
        isValid = false;
        errorMessage = 'This field is required';
    }

    // Email validation
    if (input.type === 'email' && input.value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(input.value)) {
            isValid = false;
            errorMessage = 'Please enter a valid email address';
        }
    }

    // Password validation
    if (input.type === 'password' && input.name === 'password' && input.value) {
        if (input.value.length < 6) {
            isValid = false;
            errorMessage = 'Password must be at least 6 characters long';
        }
    }

    // Confirm password validation
    if (input.name === 'password-confirm' && input.value) {
        const passwordField = input.form.querySelector('input[name="password"]');
        if (passwordField && input.value !== passwordField.value) {
            isValid = false;
            errorMessage = 'Passwords do not match';
        }
    }

    if (!isValid) {
        input.classList.add('error');
        showError(wrapper, errorMessage);
    }

    return isValid;
}

function showError(wrapper, message) {
    const error = document.createElement('span');
    error.className = 'field-error';
    error.textContent = message;
    error.setAttribute('aria-live', 'polite');
    wrapper.appendChild(error);
}

/**
 * Password Strength Indicator (Working Version)
 */
function initPasswordStrength() {
    const passwordInputs = document.querySelectorAll('input[type="password"][name="password"]');

    passwordInputs.forEach(input => {
        const strengthContainer = input.closest('.form-field').querySelector('.password-strength');

        if (strengthContainer) {
            const strengthMeter = strengthContainer.querySelector('.strength-meter-fill');
            const strengthText = strengthContainer.querySelector('.strength-text');

            if (strengthMeter && strengthText) {
                input.addEventListener('input', function() {
                    updatePasswordStrength(this.value, strengthMeter, strengthText);
                });
            }
        }
    });
}

function updatePasswordStrength(password, meterEl, textEl) {
    const strength = calculatePasswordStrength(password);
    const percentage = (strength.score / 4) * 100;

    // Animate the meter fill
    meterEl.style.width = percentage + '%';
    meterEl.style.background = strength.color;

    // Update text
    textEl.textContent = strength.text;
    textEl.style.color = strength.color;

    // Add glow effect for strong passwords
    if (strength.score >= 3) {
        meterEl.style.boxShadow = `0 0 10px ${strength.color}40`;
    } else {
        meterEl.style.boxShadow = 'none';
    }
}

function calculatePasswordStrength(password) {
    if (!password) {
        return { score: 0, color: '#6e7681', text: 'Enter password' };
    }

    let score = 0;
    let feedback = [];

    // Length check
    if (password.length >= 8) score++;
    if (password.length >= 12) score++;

    // Character variety
    if (/[a-z]/.test(password)) score++;
    if (/[A-Z]/.test(password)) score++;
    if (/\d/.test(password)) score++;
    if (/[^A-Za-z0-9]/.test(password)) score++;

    // Determine color and text based on score
    if (score <= 1) {
        return { score: 1, color: '#f85149', text: 'Very weak' };
    } else if (score <= 2) {
        return { score: 2, color: '#d29922', text: 'Weak' };
    } else if (score <= 3) {
        return { score: 3, color: '#58a6ff', text: 'Fair' };
    } else if (score <= 4) {
        return { score: 4, color: '#3fb950', text: 'Good' };
    } else {
        return { score: 4, color: '#7c3aed', text: 'Strong' };
    }
}

/**
 * Button Loading States
 */
function showButtonLoading(button) {
    const btnText = button.querySelector('.btn-text');
    const btnLoader = button.querySelector('.btn-loader');

    if (btnText && btnLoader) {
        btnText.style.display = 'none';
        btnLoader.style.display = 'flex';
        button.disabled = true;
        button.style.opacity = '0.8';
    }
}

/**
 * Enhanced Button Effects
 */
function initButtonEffects() {
    const buttons = document.querySelectorAll('.btn');

    buttons.forEach(button => {
        // Ripple effect on click
        button.addEventListener('click', function(e) {
            createRippleEffect(e, this);
        });

        // Hover effects
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });

        button.addEventListener('mouseleave', function() {
            if (!this.disabled) {
                this.style.transform = 'translateY(0)';
            }
        });
    });
}

function createRippleEffect(event, element) {
    const ripple = document.createElement('span');
    const rect = element.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;

    ripple.style.cssText = `
        position: absolute;
        width: ${size}px;
        height: ${size}px;
        left: ${x}px;
        top: ${y}px;
        background: rgba(255, 255, 255, 0.3);
        border-radius: 50%;
        transform: scale(0);
        animation: ripple-animation 0.6s ease-out;
        pointer-events: none;
        z-index: 1;
    `;

    element.style.position = 'relative';
    element.style.overflow = 'hidden';
    element.appendChild(ripple);

    setTimeout(() => {
        ripple.remove();
    }, 600);
}

/**
 * Enhanced Input Effects
 */
function initInputEffects() {
    const inputs = document.querySelectorAll('.form-input');

    inputs.forEach(input => {
        const wrapper = input.closest('.input-wrapper');
        const icon = wrapper?.querySelector('.input-icon');

        input.addEventListener('focus', function() {
            wrapper?.classList.add('focused');
            if (icon) icon.style.color = '#58a6ff';
            this.style.transform = 'translateY(-1px)';
        });

        input.addEventListener('blur', function() {
            wrapper?.classList.remove('focused');
            if (icon) icon.style.color = '#6e7681';
            this.style.transform = 'translateY(0)';
        });

        // Floating label effect
        input.addEventListener('input', function() {
            if (this.value) {
                this.classList.add('has-value');
            } else {
                this.classList.remove('has-value');
            }
        });

        // Initialize state
        if (input.value) {
            input.classList.add('has-value');
        }
    });
}

/**
 * Social Button Animations
 */
function initSocialButtons() {
    const socialBtns = document.querySelectorAll('.social-btn');

    socialBtns.forEach(btn => {
        btn.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px) scale(1.02)';
        });

        btn.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });

        btn.addEventListener('click', function() {
            this.style.transform = 'translateY(0) scale(0.98)';
            setTimeout(() => {
                this.style.transform = 'translateY(-2px) scale(1.02)';
            }, 100);
        });
    });
}

/**
 * Accessibility Controls
 */
function initAccessibilityControls() {
    const toggleBtn = document.getElementById('toggle-animations');

    if (toggleBtn) {
        // Load saved preference
        const savedPreference = localStorage.getItem('syncspace-reduced-motion');
        if (savedPreference === 'true') {
            document.body.classList.add('reduced-motion');
            updateToggleIcon(toggleBtn, true);
        }

        toggleBtn.addEventListener('click', function() {
            const isReduced = document.body.classList.toggle('reduced-motion');
            localStorage.setItem('syncspace-reduced-motion', isReduced);
            updateToggleIcon(this, isReduced);

            // Show feedback
            showAccessibilityFeedback(isReduced ? 'Animations reduced' : 'Animations enabled');
        });
    }
}

function updateToggleIcon(button, isReduced) {
    const icon = button.querySelector('svg path');
    if (icon) {
        if (isReduced) {
            icon.setAttribute('d', 'M13 2L3 14h9l-1 8 10-12h-9l1-8z');
            button.setAttribute('aria-label', 'Enable animations');
        } else {
            button.setAttribute('aria-label', 'Reduce animations');
        }
    }
}

function showAccessibilityFeedback(message) {
    const feedback = document.createElement('div');
    feedback.textContent = message;
    feedback.style.cssText = `
        position: fixed;
        bottom: 100px;
        right: 24px;
        background: rgba(22, 27, 34, 0.95);
        color: #f0f6fc;
        padding: 12px 16px;
        border-radius: 8px;
        font-size: 14px;
        z-index: 1000;
        animation: fade-in-out 2s ease-out;
        pointer-events: none;
    `;

    document.body.appendChild(feedback);

    setTimeout(() => {
        feedback.remove();
    }, 2000);
}

/**
 * Error Handling and Recovery
 */
function initErrorHandling() {
    // Handle network errors during form submission
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitButton = this.querySelector('button[type="submit"]');

            // Set a timeout to restore button if request takes too long
            const timeoutId = setTimeout(() => {
                if (submitButton) {
                    restoreButton(submitButton);
                    showNetworkError();
                }
            }, 30000); // 30 seconds timeout

            // Clear timeout if page unloads (successful submission)
            window.addEventListener('beforeunload', () => {
                clearTimeout(timeoutId);
            });
        });
    });

    // Handle browser back/forward with form state
    window.addEventListener('pageshow', function(event) {
        if (event.persisted) {
            // Restore all buttons that might be in loading state
            document.querySelectorAll('button[type="submit"]').forEach(restoreButton);
        }
    });
}

function restoreButton(button) {
    const btnText = button.querySelector('.btn-text');
    const btnLoader = button.querySelector('.btn-loader');

    if (btnText && btnLoader) {
        btnText.style.display = 'block';
        btnLoader.style.display = 'none';
        button.disabled = false;
        button.style.opacity = '1';
    }
}

function showNetworkError() {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'alert alert-error';
    errorDiv.innerHTML = `
        <div class="alert-icon">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
            </svg>
        </div>
        <span class="alert-text">Network error. Please check your connection and try again.</span>
    `;

    const formContainer = document.querySelector('.auth-content');
    if (formContainer) {
        formContainer.insertBefore(errorDiv, formContainer.firstChild);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            errorDiv.remove();
        }, 5000);
    }
}

/**
 * Add required CSS animations
 */
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple-animation {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
    
    @keyframes fade-in-out {
        0% { opacity: 0; transform: translateY(10px); }
        20%, 80% { opacity: 1; transform: translateY(0); }
        100% { opacity: 0; transform: translateY(-10px); }
    }
    
    .form-input.has-value {
        /* Additional styling for inputs with values */
    }
    
    .input-wrapper.focused .input-icon {
        color: #58a6ff !important;
    }
`;
document.head.appendChild(style);