/**
 * Enhanced Space Background with Particle System
 * Creates GitHub-like animated star field with nebula effects
 */
class SpaceBackground {
    constructor() {
        this.canvas = document.getElementById('particle-canvas');
        if (!this.canvas) return;

        this.ctx = this.canvas.getContext('2d');
        this.particles = [];
        this.stars = [];
        this.animationId = null;
        this.mouse = { x: 0, y: 0 };

        this.init();
        this.bindEvents();
        this.animate();
    }

    init() {
        this.resize();
        this.createStaticStars();
        this.createParticles();
    }

    resize() {
        this.canvas.width = window.innerWidth;
        this.canvas.height = window.innerHeight;
    }

    createStaticStars() {
        const starCount = Math.floor((this.canvas.width * this.canvas.height) / 8000);
        this.stars = [];

        for (let i = 0; i < starCount; i++) {
            this.stars.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                size: Math.random() * 2 + 0.5,
                opacity: Math.random() * 0.8 + 0.2,
                twinkleSpeed: Math.random() * 0.02 + 0.005,
                phase: Math.random() * Math.PI * 2
            });
        }
    }

    createParticles() {
        const particleCount = Math.floor((this.canvas.width * this.canvas.height) / 15000);
        this.particles = [];

        for (let i = 0; i < particleCount; i++) {
            this.particles.push({
                x: Math.random() * this.canvas.width,
                y: Math.random() * this.canvas.height,
                vx: (Math.random() - 0.5) * 0.5,
                vy: (Math.random() - 0.5) * 0.5,
                size: Math.random() * 1.5 + 0.5,
                opacity: Math.random() * 0.6 + 0.2,
                color: this.getRandomColor(),
                life: Math.random() * 200 + 100
            });
        }
    }

    getRandomColor() {
        const colors = [
            'rgba(124, 58, 237, 0.6)',  // Purple
            'rgba(168, 85, 247, 0.5)',  // Light purple
            'rgba(139, 92, 246, 0.4)',  // Medium purple
            'rgba(255, 255, 255, 0.8)'  // White
        ];
        return colors[Math.floor(Math.random() * colors.length)];
    }

    drawStars() {
        this.stars.forEach(star => {
            // Twinkling effect
            star.phase += star.twinkleSpeed;
            const twinkle = Math.sin(star.phase) * 0.3 + 0.7;
            const currentOpacity = star.opacity * twinkle;

            this.ctx.save();
            this.ctx.globalAlpha = currentOpacity;
            this.ctx.fillStyle = '#ffffff';
            this.ctx.beginPath();
            this.ctx.arc(star.x, star.y, star.size, 0, Math.PI * 2);
            this.ctx.fill();

            // Add subtle glow for larger stars
            if (star.size > 1.5) {
                this.ctx.globalAlpha = currentOpacity * 0.3;
                this.ctx.beginPath();
                this.ctx.arc(star.x, star.y, star.size * 3, 0, Math.PI * 2);
                this.ctx.fill();
            }

            this.ctx.restore();
        });
    }

    drawParticles() {
        this.particles.forEach((particle, index) => {
            // Update position
            particle.x += particle.vx;
            particle.y += particle.vy;

            // Wrap around screen
            if (particle.x < 0) particle.x = this.canvas.width;
            if (particle.x > this.canvas.width) particle.x = 0;
            if (particle.y < 0) particle.y = this.canvas.height;
            if (particle.y > this.canvas.height) particle.y = 0;

            // Update life
            particle.life -= 0.2;
            if (particle.life <= 0) {
                this.particles[index] = {
                    x: Math.random() * this.canvas.width,
                    y: Math.random() * this.canvas.height,
                    vx: (Math.random() - 0.5) * 0.5,
                    vy: (Math.random() - 0.5) * 0.5,
                    size: Math.random() * 1.5 + 0.5,
                    opacity: Math.random() * 0.6 + 0.2,
                    color: this.getRandomColor(),
                    life: Math.random() * 200 + 100
                };
                return;
            }

            // Draw particle
            const lifeRatio = particle.life / 200;
            this.ctx.save();
            this.ctx.globalAlpha = particle.opacity * lifeRatio;
            this.ctx.fillStyle = particle.color;
            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.size, 0, Math.PI * 2);
            this.ctx.fill();
            this.ctx.restore();
        });
    }

    drawNebula() {
        const time = Date.now() * 0.0005;
        const centerX = this.canvas.width / 2;
        const centerY = this.canvas.height / 2;

        // Create gradient for nebula effect
        const gradient = this.ctx.createRadialGradient(
            centerX + Math.sin(time) * 100,
            centerY + Math.cos(time) * 50,
            0,
            centerX,
            centerY,
            Math.max(this.canvas.width, this.canvas.height) / 2
        );

        gradient.addColorStop(0, 'rgba(124, 58, 237, 0.03)');
        gradient.addColorStop(0.3, 'rgba(168, 85, 247, 0.02)');
        gradient.addColorStop(1, 'rgba(139, 92, 246, 0.01)');

        this.ctx.save();
        this.ctx.fillStyle = gradient;
        this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.restore();
    }

    animate() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        // Draw background layers
        this.drawNebula();
        this.drawStars();
        this.drawParticles();

        this.animationId = requestAnimationFrame(() => this.animate());
    }

    bindEvents() {
        window.addEventListener('resize', () => {
            this.resize();
            this.createStaticStars();
        });

        this.canvas.addEventListener('mousemove', (e) => {
            this.mouse.x = e.clientX;
            this.mouse.y = e.clientY;
        });
    }

    destroy() {
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
    }
}

/**
 * CSS Stars Animation (fallback)
 */
class CSSStarsAnimation {
    constructor() {
        this.createStarLayers();
    }

    createStarLayers() {
        const layers = document.querySelectorAll('[class^="stars-layer"]');

        layers.forEach((layer, index) => {
            this.populateStarLayer(layer, index + 1);
        });
    }

    populateStarLayer(layer, layerIndex) {
        const starsCount = Math.floor(100 / layerIndex);
        let boxShadow = '';

        for (let i = 0; i < starsCount; i++) {
            const x = Math.floor(Math.random() * 2000);
            const y = Math.floor(Math.random() * 2000);
            const size = Math.floor(Math.random() * 3) + 1;
            const opacity = Math.random() * 0.8 + 0.2;

            boxShadow += `${x}px ${y}px rgba(255, 255, 255, ${opacity})`;
            if (i < starsCount - 1) boxShadow += ', ';
        }

        layer.style.boxShadow = boxShadow;
        layer.style.width = '2px';
        layer.style.height = '2px';
        layer.style.background = 'transparent';
        layer.style.borderRadius = '50%';
        layer.style.position = 'absolute';
        layer.style.top = '0';
        layer.style.left = '0';
    }
}

/**
 * Initialize background system
 */
document.addEventListener('DOMContentLoaded', () => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (!prefersReducedMotion) {
        // Try canvas animation first
        if (document.getElementById('particle-canvas')) {
            new SpaceBackground();
        } else {
            // Fallback to CSS animations
            new CSSStarsAnimation();
        }
    }

    // Handle reduced motion toggle
    const toggleBtn = document.getElementById('toggle-animations');
    if (toggleBtn) {
        toggleBtn.addEventListener('click', () => {
            document.body.classList.toggle('reduced-motion');

            // Store preference
            localStorage.setItem('reducedMotion',
                document.body.classList.contains('reduced-motion') ? 'true' : 'false'
            );
        });

        // Apply saved preference
        if (localStorage.getItem('reducedMotion') === 'true') {
            document.body.classList.add('reduced-motion');
        }
    }
});

/**
 * Password Strength Checker
 */
class PasswordStrengthChecker {
    constructor() {
        this.init();
    }

    init() {
        const passwordInputs = document.querySelectorAll('input[type="password"][name="password"]');
        passwordInputs.forEach(input => {
            const strengthMeter = input.closest('.form-field')?.querySelector('.strength-meter-fill');
            const strengthText = input.closest('.form-field')?.querySelector('.strength-text');

            if (strengthMeter && strengthText) {
                input.addEventListener('input', () => {
                    this.updateStrength(input.value, strengthMeter, strengthText);
                });
            }
        });
    }

    updateStrength(password, meterEl, textEl) {
        const score = this.calculateStrength(password);
        const percentage = (score / 5) * 100;

        meterEl.style.width = `${percentage}%`;

        // Update color based on strength
        if (score <= 1) {
            meterEl.style.background = '#f85149'; // Red
            textEl.textContent = 'Very weak';
            textEl.style.color = '#f85149';
        } else if (score <= 2) {
            meterEl.style.background = '#d29922'; // Orange
            textEl.textContent = 'Weak';
            textEl.style.color = '#d29922';
        } else if (score <= 3) {
            meterEl.style.background = '#58a6ff'; // Blue
            textEl.textContent = 'Fair';
            textEl.style.color = '#58a6ff';
        } else if (score <= 4) {
            meterEl.style.background = '#3fb950'; // Green
            textEl.textContent = 'Good';
            textEl.style.color = '#3fb950';
        } else {
            meterEl.style.background = '#7c3aed'; // Purple
            textEl.textContent = 'Strong';
            textEl.style.color = '#7c3aed';
        }
    }

    calculateStrength(password) {
        let score = 0;

        // Length check
        if (password.length >= 8) score++;
        if (password.length >= 12) score++;

        // Character variety
        if (/[a-z]/.test(password)) score++;
        if (/[A-Z]/.test(password)) score++;
        if (/\d/.test(password)) score++;
        if (/[^A-Za-z0-9]/.test(password)) score++;

        return Math.min(score, 5);
    }
}

// Initialize password strength checker
document.addEventListener('DOMContentLoaded', () => {
    new PasswordStrengthChecker();
});