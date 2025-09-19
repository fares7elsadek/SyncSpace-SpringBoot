/**
 * Enhanced Particle System for SyncSpace Theme
 * Creates animated floating particles and connecting lines
 */

class EnhancedParticleSystem {
    constructor() {
        this.canvas = null;
        this.ctx = null;
        this.particles = [];
        this.connections = [];
        this.mouse = { x: 0, y: 0 };
        this.isReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        this.isDisabled = false;
        this.animationId = null;

        // Configuration
        this.config = {
            particleCount: this.calculateParticleCount(),
            maxDistance: 120,
            mouseRadius: 150,
            colors: [
                'rgba(139, 92, 246, 0.8)', // Purple
                'rgba(236, 72, 153, 0.6)', // Pink
                'rgba(6, 182, 212, 0.7)',  // Cyan
                'rgba(248, 250, 252, 0.9)' // White
            ],
            connectionColor: 'rgba(139, 92, 246, 0.2)',
            mouseConnectionColor: 'rgba(139, 92, 246, 0.4)'
        };

        if (!this.isReducedMotion) {
            this.init();
        }
    }

    calculateParticleCount() {
        const area = window.innerWidth * window.innerHeight;
        const density = 0.000015; // particles per pixel
        return Math.min(Math.max(Math.floor(area * density), 20), 60);
    }

    init() {
        this.createCanvas();
        this.createParticles();
        this.setupEventListeners();
        this.animate();
    }

    createCanvas() {
        const container = document.getElementById('particles-background');
        if (!container) return;

        this.canvas = document.createElement('canvas');
        this.canvas.style.cssText = `
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      z-index: 1;
    `;

        container.appendChild(this.canvas);
        this.ctx = this.canvas.getContext('2d');
        this.resize();
    }

    createParticles() {
        this.particles = [];
        const count = this.config.particleCount;

        for (let i = 0; i < count; i++) {
            this.particles.push(this.createParticle());
        }
    }

    createParticle() {
        return {
            x: Math.random() * this.canvas.width,
            y: Math.random() * this.canvas.height,
            vx: (Math.random() - 0.5) * 0.8,
            vy: (Math.random() - 0.5) * 0.8,
            radius: Math.random() * 2.5 + 0.5,
            opacity: Math.random() * 0.6 + 0.2,
            color: this.config.colors[Math.floor(Math.random() * this.config.colors.length)],
            pulseSpeed: Math.random() * 0.02 + 0.01,
            pulseOffset: Math.random() * Math.PI * 2,
            trail: []
        };
    }

    setupEventListeners() {
        // Mouse tracking
        document.addEventListener('mousemove', (e) => {
            const rect = this.canvas.getBoundingClientRect();
            this.mouse.x = e.clientX - rect.left;
            this.mouse.y = e.clientY - rect.top;
        });

        // Resize handling
        window.addEventListener('resize', () => {
            this.resize();
            this.createParticles(); // Recreate particles for new canvas size
        });

        // Visibility change handling
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.pause();
            } else {
                this.resume();
            }
        });
    }

    resize() {
        if (!this.canvas) return;

        const rect = this.canvas.getBoundingClientRect();
        this.canvas.width = rect.width * window.devicePixelRatio;
        this.canvas.height = rect.height * window.devicePixelRatio;
        this.ctx.scale(window.devicePixelRatio, window.devicePixelRatio);
        this.canvas.style.width = rect.width + 'px';
        this.canvas.style.height = rect.height + 'px';
    }

    animate() {
        if (this.isDisabled) return;

        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        this.updateParticles();
        this.drawConnections();
        this.drawParticles();

        this.animationId = requestAnimationFrame(() => this.animate());
    }

    updateParticles() {
        const currentTime = Date.now() * 0.001;

        this.particles.forEach(particle => {
            // Update position
            particle.x += particle.vx;
            particle.y += particle.vy;

            // Boundary collision with smooth wrapping
            if (particle.x < -10) particle.x = this.canvas.width + 10;
            if (particle.x > this.canvas.width + 10) particle.x = -10;
            if (particle.y < -10) particle.y = this.canvas.height + 10;
            if (particle.y > this.canvas.height + 10) particle.y = -10;

            // Mouse interaction
            const dx = this.mouse.x - particle.x;
            const dy = this.mouse.y - particle.y;
            const distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < this.config.mouseRadius) {
                const force = (this.config.mouseRadius - distance) / this.config.mouseRadius;
                const angle = Math.atan2(dy, dx);
                particle.vx -= Math.cos(angle) * force * 0.03;
                particle.vy -= Math.sin(angle) * force * 0.03;
            }

            // Apply velocity damping
            particle.vx *= 0.99;
            particle.vy *= 0.99;

            // Pulse animation
            particle.opacity = 0.3 + 0.3 * Math.sin(currentTime * particle.pulseSpeed + particle.pulseOffset);

            // Trail effect
            particle.trail.push({ x: particle.x, y: particle.y, opacity: particle.opacity });
            if (particle.trail.length > 5) {
                particle.trail.shift();
            }
        });
    }

    drawConnections() {
        const maxDistance = this.config.maxDistance;

        // Particle to particle connections
        for (let i = 0; i < this.particles.length; i++) {
            for (let j = i + 1; j < this.particles.length; j++) {
                const dx = this.particles[i].x - this.particles[j].x;
                const dy = this.particles[i].y - this.particles[j].y;
                const distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < maxDistance) {
                    const opacity = (1 - distance / maxDistance) * 0.3;
                    this.ctx.strokeStyle = this.config.connectionColor.replace('0.2', opacity.toString());
                    this.ctx.lineWidth = 0.5;
                    this.ctx.beginPath();
                    this.ctx.moveTo(this.particles[i].x, this.particles[i].y);
                    this.ctx.lineTo(this.particles[j].x, this.particles[j].y);
                    this.ctx.stroke();
                }
            }

            // Mouse to particle connections
            const dx = this.mouse.x - this.particles[i].x;
            const dy = this.mouse.y - this.particles[i].y;
            const distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < this.config.mouseRadius) {
                const opacity = (1 - distance / this.config.mouseRadius) * 0.4;
                this.ctx.strokeStyle = this.config.mouseConnectionColor.replace('0.4', opacity.toString());
                this.ctx.lineWidth = 1;
                this.ctx.beginPath();
                this.ctx.moveTo(this.mouse.x, this.mouse.y);
                this.ctx.lineTo(this.particles[i].x, this.particles[i].y);
                this.ctx.stroke();
            }
        }
    }

    drawParticles() {
        this.particles.forEach(particle => {
            // Draw particle trail
            if (particle.trail.length > 1) {
                for (let i = 1; i < particle.trail.length; i++) {
                    const trailOpacity = (i / particle.trail.length) * particle.opacity * 0.5;
                    this.ctx.save();
                    this.ctx.globalAlpha = trailOpacity;
                    this.ctx.fillStyle = particle.color;
                    this.ctx.beginPath();
                    this.ctx.arc(particle.trail[i].x, particle.trail[i].y, particle.radius * 0.5, 0, Math.PI * 2);
                    this.ctx.fill();
                    this.ctx.restore();
                }
            }

            // Draw main particle
            this.ctx.save();
            this.ctx.globalAlpha = particle.opacity;

            // Glow effect
            this.ctx.shadowColor = particle.color;
            this.ctx.shadowBlur = 10;
            this.ctx.fillStyle = particle.color;

            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2);
            this.ctx.fill();

            // Inner bright core
            this.ctx.shadowBlur = 0;
            this.ctx.globalAlpha = particle.opacity * 1.5;
            this.ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.radius * 0.3, 0, Math.PI * 2);
            this.ctx.fill();

            this.ctx.restore();
        });
    }

    disable() {
        this.isDisabled = true;
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
        if (this.canvas) {
            this.canvas.style.display = 'none';
        }
    }

    enable() {
        this.isDisabled = false;
        if (this.canvas) {
            this.canvas.style.display = 'block';
            this.animate();
        }
    }

    pause() {
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
    }

    resume() {
        if (!this.isDisabled) {
            this.animate();
        }
    }

    destroy() {
        this.disable();
        if (this.canvas && this.canvas.parentNode) {
            this.canvas.parentNode.removeChild(this.canvas);
        }
        document.removeEventListener('mousemove', this.setupEventListeners);
        window.removeEventListener('resize', this.setupEventListeners);
    }
}

// Initialize particle system when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    // Create floating orbs in the background
    const createFloatingOrbs = () => {
        if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

        const orbContainer = document.createElement('div');
        orbContainer.className = 'floating-orbs';
        document.body.appendChild(orbContainer);

        for (let i = 0; i < 3; i++) {
            const orb = document.createElement('div');
            orb.className = 'floating-orb';
            orbContainer.appendChild(orb);
        }
    };

    // Create stars background
    const createStarsBackground = () => {
        if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return;

        const bgContainer = document.createElement('div');
        bgContainer.className = 'background-container';
        document.body.appendChild(bgContainer);

        // Create multiple star layers
        for (let i = 0; i < 2; i++) {
            const starField = document.createElement('div');
            starField.className = 'stars-field';
            bgContainer.appendChild(starField);
        }
    };

    createStarsBackground();
    createFloatingOrbs();

    // Initialize particle system
    window.particleSystem = new EnhancedParticleSystem();
});

// Handle visibility changes for performance
document.addEventListener('visibilitychange', () => {
    if (window.particleSystem) {
        if (document.hidden) {
            window.particleSystem.pause();
        } else {
            window.particleSystem.resume();
        }
    }
});