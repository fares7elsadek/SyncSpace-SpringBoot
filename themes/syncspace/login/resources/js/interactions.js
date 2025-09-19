/**
 * Enhanced Form Interactions with Space Theme Integration
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
    initEnhancedAccessibilityControls();
    initErrorHandling();
    initSpaceThemeEffects();
    initEarthInteraction();
    initSoundSystem();
});

/**
 * Enhanced Space Theme Effects
 */
function initSpaceThemeEffects() {
    // Create cosmic dust effect on mouse movement
    let dustTimer;
    document.addEventListener('mousemove', function(e) {
        clearTimeout(dustTimer);
        dustTimer = setTimeout(() => createCosmicDust(e.clientX, e.clientY), 100);
    });

    // Add stellar effect to form focus
    const inputs = document.querySelectorAll('.form-input');
    inputs.forEach(input => {
        input.addEventListener('focus', () => {
            createStarBurst(input);
        });
    });

    // Enhanced card hover effects with space theme
    const authCard = document.querySelector('.auth-card');
    if (authCard) {
        authCard.addEventListener('mouseenter', () => {
            authCard.style.transform = 'translateY(-5px) scale(1.02)';
            authCard.style.boxShadow = 'var(--shadow-floating), 0 0 40px rgba(124, 58, 237, 0.3)';
        });

        authCard.addEventListener('mouseleave', () => {
            authCard.style.transform = 'translateY(0) scale(1)';
            authCard.style.boxShadow = 'var(--shadow-card)';
        });
    }
}

function createCosmicDust(x, y) {
    const dust = document.createElement('div');
    dust.style.cssText = `
        position: fixed;
        left: ${x}px;
        top: ${y}px;
        width: 4px;
        height: 4px;
        background: radial-gradient(circle, rgba(168, 85, 247, 0.8) 0%, transparent 70%);
        border-radius: 50%;
        pointer-events: none;
        z-index: 9999;
        animation: cosmic-dust-fade 2s ease-out forwards;
    `;

    document.body.appendChild(dust);

    setTimeout(() => dust.remove(), 2000);
}

function createStarBurst(element) {
    const rect = element.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;

    for (let i = 0; i < 6; i++) {
        const star = document.createElement('div');
        const angle = (i / 6) * Math.PI * 2;
        const distance = 30 + Math.random() * 20;

        star.style.cssText = `
            position: fixed;
            left: ${centerX}px;
            top: ${centerY}px;
            width: 3px;
            height: 3px;
            background: rgba(255, 255, 255, 0.9);
            border-radius: 50%;
            pointer-events: none;
            z-index: 9999;
            animation: star-burst 1s ease-out forwards;
            --end-x: ${Math.cos(angle) * distance}px;
            --end-y: ${Math.sin(angle) * distance}px;
        `;

        document.body.appendChild(star);
        setTimeout(() => star.remove(), 1000);
    }
}

/**
 * Earth Interaction Effects
 */
function initEarthInteraction() {
    const earth = document.querySelector('.earth');
    if (!earth) return;

    // Add click interaction to Earth
    earth.addEventListener('click', function() {
        createEarthPulse();
        if (window.spaceParticleSystem) {
            // Create particles from Earth
            const rect = this.getBoundingClientRect();
            const centerX = rect.left + rect.width / 2;
            const centerY = rect.top + rect.height / 2;

            // Convert to canvas coordinates
            const canvas = document.getElementById('particle-canvas');
            if (canvas) {
                const canvasRect = canvas.getBoundingClientRect();
                const canvasX = (centerX - canvasRect.left) * (canvas.width / canvasRect.width);
                const canvasY = (centerY - canvasRect.top) * (canvas.height / canvasRect.height);

                window.spaceParticleSystem.createParticleBurst(canvasX, canvasY);
            }
        }

        playSpaceSound('earth-pulse');
    });

    // Add hover effect to Earth
    earth.addEventListener('mouseenter', function() {
        this.style.transform = 'scale(1.1)';
        this.style.filter = 'brightness(1.2)';
    });

    earth.addEventListener('mouseleave', function() {
        this.style.transform = 'scale(1)';
        this.style.filter = 'brightness(1)';
    });

    // Create periodic Earth effects
    setInterval(createEarthAurora, 15000); // Every 15 seconds
}

function createEarthPulse() {
    const earth = document.querySelector('.earth');
    if (!earth) return;

    const pulse = document.createElement('div');
    pulse.style.cssText = `
        position: absolute;
        inset: -50px;
        border-radius: 50%;
        border: 2px solid rgba(74, 144, 226, 0.6);
        animation: earth-pulse-ring 2s ease-out forwards;
        pointer-events: none;
    `;

    earth.appendChild(pulse);
    setTimeout(() => pulse.remove(), 2000);
}

function createEarthAurora() {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

    const earth = document.querySelector('.earth');
    if (!earth) return;

    const aurora = document.createElement('div');
    aurora.style.cssText = `
        position: absolute;
        inset: -20px;
        border-radius: 50%;
        background: conic-gradient(
            transparent 0deg,
            rgba(0, 255, 157, 0.3) 60deg,
            rgba(255, 20, 147, 0.3) 120deg,
            rgba(74, 144, 226, 0.3) 180deg,
            transparent 240deg
        );
        animation: aurora-rotate 8s linear infinite;
        pointer-events: none;
    `;

    earth.appendChild(aurora);
    setTimeout(() => aurora.remove(), 8000);
}

/**
 * Sound System Integration
 */
function initSoundSystem() {
    window.soundEnabled = localStorage.getItem('syncspace-sound-enabled') !== 'false';

    const toggleBtn = document.getElementById('toggle-sound');
    if (toggleBtn) {
        updateSoundIcon(toggleBtn, window.soundEnabled);

        toggleBtn.addEventListener('click', function() {
            window.soundEnabled = !window.soundEnabled;
            localStorage.setItem('syncspace-sound-enabled', window.soundEnabled);
            updateSoundIcon(this, window.soundEnabled);

            showAccessibilityFeedback(window.soundEnabled ? 'Sound enabled' : 'Sound disabled');

            if (window.soundEnabled) {
                playSpaceSound('toggle-on');
            }
        });
    }
}

function updateSoundIcon(button, isEnabled) {
    button.classList.toggle('muted', !isEnabled);
    button.setAttribute('aria-label', isEnabled ? 'Disable sound effects' : 'Enable sound effects');
}

function playSpaceSound(type) {
    if (!window.soundEnabled) return;

    // Create audio context for space sounds
    const audioContext = window.audioContext || (window.audioContext = new (window.AudioContext || window.webkitAudioContext)());

    let frequency, duration, waveType;

    switch (type) {
        case 'earth-pulse':
            frequency = 150;
            duration = 0.8;
            waveType = 'sine';
            break;
        case 'button-hover':
            frequency = 800;
            duration = 0.1;
            waveType = 'triangle';
            break;
        case 'input-focus':
            frequency = 600;
            duration = 0.2;
            waveType = 'sine';
            break;
        case 'toggle-on':
            frequency = 440;
            duration = 0.3;
            waveType = 'square';
            break;
        default:
            return;
    }

    try {
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);

        oscillator.frequency.setValueAtTime(frequency, audioContext.currentTime);
        oscillator.type = waveType;

        gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + duration);

        oscillator.start();
        oscillator.stop(audioContext.currentTime + duration);
    } catch (error) {
        // Audio context might be blocked or unsupported
        console.log('Audio playback not available');
    }
}

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
                if (eyeOpen) eyeOpen.style.display = 'none';
                if (eyeClosed) eyeClosed.style.display = 'block';
                this.setAttribute('aria-label', 'Hide password');
            } else {
                input.type = 'password';
                if (eyeOpen) eyeOpen.style.display = 'block';
                if (eyeClosed) eyeClosed.style.display = 'none';
                this.setAttribute('aria-label', 'Show password');
            }

            playSpaceSound('toggle-on');
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
 * Password Strength Indicator
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
 * Enhanced Button Effects
 */
function initButtonEffects() {
    const buttons = document.querySelectorAll('.btn');

    buttons.forEach(button => {
        // Ripple effect on click
        button.addEventListener('click', function(e) {
            createRippleEffect(e, this);
            playSpaceSound('button-hover');
        });

        // Hover effects
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
            playSpaceSound('button-hover');
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
            playSpaceSound('input-focus');
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
            playSpaceSound('button-hover');
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
 * Enhanced Accessibility Controls
 */
function initEnhancedAccessibilityControls() {
    const toggleAnimationsBtn = document.getElementById('toggle-animations');
    const toggleSoundBtn = document.getElementById('toggle-sound');

    // Animation toggle
    if (toggleAnimationsBtn) {
        const savedPreference = localStorage.getItem('syncspace-reduced-motion');
        if (savedPreference === 'true') {
            document.body.classList.add('reduced-motion');
            updateToggleIcon(toggleAnimationsBtn, true);
        }

        toggleAnimationsBtn.addEventListener('click', function() {
            const isReduced = document.body.classList.toggle('reduced-motion');
            localStorage.setItem('syncspace-reduced-motion', isReduced);
            updateToggleIcon(this, isReduced);

            // Disable particle system if reducing motion
            if (window.spaceParticleSystem) {
                if (isReduced) {
                    window.spaceParticleSystem.disable();
                } else {
                    window.spaceParticleSystem.enable();
                }
            }

            showAccessibilityFeedback(isReduced ? 'Animations reduced' : 'Animations enabled');
        });
    }
}

function updateToggleIcon(button, isReduced) {
    const icon = button.querySelector('svg path');
    if (icon) {
        if (isReduced) {
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
        backdrop-filter: blur(8px);
        border: 1px solid rgba(124, 58, 237, 0.3);
    `;

    document.body.appendChild(feedback);

    setTimeout(() => {
        feedback.remove();
    }, 2000);
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

/**
 * Error Handling and Recovery
 */
function initErrorHandling() {
    const forms = document.querySelectorAll('form');

    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const submitButton = this.querySelector('button[type="submit"]');

            const timeoutId = setTimeout(() => {
                if (submitButton) {
                    restoreButton(submitButton);
                    showNetworkError();
                }
            }, 30000);

            window.addEventListener('beforeunload', () => {
                clearTimeout(timeoutId);
            });
        });
    });

    window.addEventListener('pageshow', function(event) {
        if (event.persisted) {
            document.querySelectorAll('button[type="submit"]').forEach(restoreButton);
        }
    });
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
    
    @keyframes cosmic-dust-fade {
        0% { opacity: 0; transform: scale(0); }
        50% { opacity: 1; transform: scale(1); }
        100% { opacity: 0; transform: scale(0) translate(20px, -20px); }
    }
    
    @keyframes star-burst {
        0% { opacity: 0; transform: scale(0) translate(0, 0); }
        50% { opacity: 1; transform: scale(1) translate(var(--end-x), var(--end-y)); }
        100% { opacity: 0; transform: scale(0) translate(var(--end-x), var(--end-y)); }
    }
    
    @keyframes earth-pulse-ring {
        0% { opacity: 0; transform: scale(0.8); }
        50% { opacity: 1; transform: scale(1); }
        100% { opacity: 0; transform: scale(1.5); }
    }
    
    @keyframes aurora-rotate {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    
    .form-input.has-value {
        /* Additional styling for inputs with values */
    }
    
    .input-wrapper.focused .input-icon {
        color: #58a6ff !important;
    }
`;
document.head.appendChild(style);