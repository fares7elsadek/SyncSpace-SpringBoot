/**
 * Enhanced Particle System for SyncSpace Theme with Earth Integration
 * Creates animated floating particles, connecting lines, and interactive effects
 */

class EnhancedSpaceParticleSystem {
    constructor() {
        this.canvas = null;
        this.ctx = null;
        this.particles = [];
        this.connections = [];
        this.mouse = { x: 0, y: 0, isActive: false };
        this.earthPosition = { x: 0, y: 0 };
        this.isReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        this.isDisabled = false;
        this.animationId = null;
        this.soundEnabled = true;

        // Enhanced Configuration
        this.config = {
            particleCount: this.calculateParticleCount(),
            maxDistance: 150,
            mouseRadius: 180,
            earthRadius: 100,
            colors: [
                'rgba(124, 58, 237, 0.9)', // Purple
                'rgba(168, 85, 247, 0.8)', // Light Purple
                'rgba(74, 144, 226, 0.7)',  // Blue
                'rgba(255, 255, 255, 0.9)', // White
                'rgba(139, 92, 246, 0.6)',  // Violet
                'rgba(30, 144, 255, 0.5)'   // Sky Blue
            ],
            connectionColor: 'rgba(124, 58, 237, 0.3)',
            mouseConnectionColor: 'rgba(168, 85, 247, 0.5)',
            earthConnectionColor: 'rgba(74, 144, 226, 0.4)'
        };

        if (!this.isReducedMotion) {
            this.init();
        }
    }

    calculateParticleCount() {
        const area = window.innerWidth * window.innerHeight;
        const density = window.innerWidth > 768 ? 0.000020 : 0.000015;
        return Math.min(Math.max(Math.floor(area * density), 25), 80);
    }

    init() {
        this.createCanvas();
        this.createParticles();
        this.setupEventListeners();
        this.trackEarthPosition();
        this.animate();
        this.createCosmicEvents();
    }

    createCanvas() {
        // Find or create canvas
        this.canvas = document.getElementById('particle-canvas');
        if (!this.canvas) {
            this.canvas = document.createElement('canvas');
            this.canvas.id = 'particle-canvas';
            document.querySelector('.space-bg').appendChild(this.canvas);
        }

        this.canvas.style.cssText = `
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 10;
            mix-blend-mode: screen;
        `;

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
            vx: (Math.random() - 0.5) * 1.2,
            vy: (Math.random() - 0.5) * 1.2,
            radius: Math.random() * 3 + 0.8,
            opacity: Math.random() * 0.8 + 0.2,
            color: this.config.colors[Math.floor(Math.random() * this.config.colors.length)],
            pulseSpeed: Math.random() * 0.03 + 0.01,
            pulseOffset: Math.random() * Math.PI * 2,
            trail: [],
            life: 1,
            maxLife: Math.random() * 200 + 100,
            age: 0,
            attractedToEarth: Math.random() < 0.3, // 30% chance to be attracted to Earth
            type: Math.random() < 0.8 ? 'normal' : 'special' // Special particles have unique behaviors
        };
    }

    trackEarthPosition() {
        const earthElement = document.querySelector('.earth');
        if (earthElement) {
            const updateEarthPosition = () => {
                const rect = earthElement.getBoundingClientRect();
                this.earthPosition.x = rect.left + rect.width / 2;
                this.earthPosition.y = rect.top + rect.height / 2;
            };

            updateEarthPosition();
            // Update position periodically for orbit animation
            setInterval(updateEarthPosition, 100);
        }
    }

    setupEventListeners() {
        // Enhanced mouse tracking with interaction states
        document.addEventListener('mousemove', (e) => {
            const rect = this.canvas.getBoundingClientRect();
            this.mouse.x = (e.clientX - rect.left) * (this.canvas.width / rect.width);
            this.mouse.y = (e.clientY - rect.top) * (this.canvas.height / rect.height);
            this.mouse.isActive = true;
        });

        document.addEventListener('mouseleave', () => {
            this.mouse.isActive = false;
        });

        // Click to create particle burst
        document.addEventListener('click', (e) => {
            if (this.isDisabled) return;
            const rect = this.canvas.getBoundingClientRect();
            const clickX = (e.clientX - rect.left) * (this.canvas.width / rect.width);
            const clickY = (e.clientY - rect.top) * (this.canvas.height / rect.height);
            this.createParticleBurst(clickX, clickY);
        });

        // Resize handling
        window.addEventListener('resize', () => {
            this.resize();
            this.createParticles();
        });

        // Visibility and performance
        document.addEventListener('visibilitychange', () => {
            if (document.hidden) {
                this.pause();
            } else {
                this.resume();
            }
        });

        // Touch support for mobile
        document.addEventListener('touchmove', (e) => {
            if (e.touches.length > 0) {
                const rect = this.canvas.getBoundingClientRect();
                this.mouse.x = (e.touches[0].clientX - rect.left) * (this.canvas.width / rect.width);
                this.mouse.y = (e.touches[0].clientY - rect.top) * (this.canvas.height / rect.height);
                this.mouse.isActive = true;
            }
        });

        document.addEventListener('touchend', () => {
            this.mouse.isActive = false;
        });
    }

    resize() {
        if (!this.canvas) return;

        const container = this.canvas.parentElement;
        const rect = container.getBoundingClientRect();

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
        this.drawSpecialEffects();

        this.animationId = requestAnimationFrame(() => this.animate());
    }

    updateParticles() {
        const currentTime = Date.now() * 0.001;

        this.particles.forEach((particle, index) => {
            // Age particle
            particle.age++;
            particle.life = Math.max(0, 1 - (particle.age / particle.maxLife));

            // Update position
            particle.x += particle.vx;
            particle.y += particle.vy;

            // Earth attraction for certain particles
            if (particle.attractedToEarth && this.earthPosition.x && this.earthPosition.y) {
                const dx = (this.earthPosition.x * window.devicePixelRatio) - particle.x;
                const dy = (this.earthPosition.y * window.devicePixelRatio) - particle.y;
                const distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < this.config.earthRadius * 2) {
                    const force = 0.0005;
                    particle.vx += (dx / distance) * force;
                    particle.vy += (dy / distance) * force;
                }
            }

            // Boundary wrapping with smooth transitions
            const margin = 20;
            if (particle.x < -margin) particle.x = this.canvas.width + margin;
            if (particle.x > this.canvas.width + margin) particle.x = -margin;
            if (particle.y < -margin) particle.y = this.canvas.height + margin;
            if (particle.y > this.canvas.height + margin) particle.y = -margin;

            // Enhanced mouse interaction
            if (this.mouse.isActive) {
                const dx = this.mouse.x - particle.x;
                const dy = this.mouse.y - particle.y;
                const distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < this.config.mouseRadius) {
                    const force = (this.config.mouseRadius - distance) / this.config.mouseRadius;
                    const angle = Math.atan2(dy, dx);
                    const strength = particle.type === 'special' ? 0.05 : 0.03;
                    particle.vx -= Math.cos(angle) * force * strength;
                    particle.vy -= Math.sin(angle) * force * strength;

                    // Increase particle glow near mouse
                    particle.opacity = Math.min(1, particle.opacity + force * 0.5);
                }
            }

            // Velocity damping
            particle.vx *= 0.995;
            particle.vy *= 0.995;

            // Enhanced pulse animation
            const pulseIntensity = particle.type === 'special' ? 0.5 : 0.3;
            particle.opacity = (0.4 + pulseIntensity * Math.sin(currentTime * particle.pulseSpeed + particle.pulseOffset)) * particle.life;

            // Trail effect for special particles
            if (particle.type === 'special') {
                particle.trail.push({
                    x: particle.x,
                    y: particle.y,
                    opacity: particle.opacity * 0.5,
                    time: currentTime
                });

                // Remove old trail points
                particle.trail = particle.trail.filter(point => currentTime - point.time < 2);
                if (particle.trail.length > 8) {
                    particle.trail.shift();
                }
            }

            // Respawn particles that have died
            if (particle.life <= 0) {
                this.particles[index] = this.createParticle();
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
                    const opacity = (1 - distance / maxDistance) * 0.4 *
                        Math.min(this.particles[i].life, this.particles[j].life);

                    this.ctx.strokeStyle = this.config.connectionColor.replace('0.3', opacity.toString());
                    this.ctx.lineWidth = 0.8;
                    this.ctx.beginPath();
                    this.ctx.moveTo(this.particles[i].x, this.particles[i].y);
                    this.ctx.lineTo(this.particles[j].x, this.particles[j].y);
                    this.ctx.stroke();
                }
            }

            // Mouse to particle connections
            if (this.mouse.isActive) {
                const dx = this.mouse.x - this.particles[i].x;
                const dy = this.mouse.y - this.particles[i].y;
                const distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < this.config.mouseRadius) {
                    const opacity = (1 - distance / this.config.mouseRadius) * 0.6 * this.particles[i].life;
                    this.ctx.strokeStyle = this.config.mouseConnectionColor.replace('0.5', opacity.toString());
                    this.ctx.lineWidth = 1.5;
                    this.ctx.beginPath();
                    this.ctx.moveTo(this.mouse.x, this.mouse.y);
                    this.ctx.lineTo(this.particles[i].x, this.particles[i].y);
                    this.ctx.stroke();
                }
            }

            // Earth to particle connections
            if (this.earthPosition.x && this.earthPosition.y) {
                const dx = (this.earthPosition.x * window.devicePixelRatio) - this.particles[i].x;
                const dy = (this.earthPosition.y * window.devicePixelRatio) - this.particles[i].y;
                const distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < this.config.earthRadius && this.particles[i].attractedToEarth) {
                    const opacity = (1 - distance / this.config.earthRadius) * 0.3 * this.particles[i].life;
                    this.ctx.strokeStyle = this.config.earthConnectionColor.replace('0.4', opacity.toString());
                    this.ctx.lineWidth = 1;
                    this.ctx.beginPath();
                    this.ctx.moveTo(this.earthPosition.x * window.devicePixelRatio, this.earthPosition.y * window.devicePixelRatio);
                    this.ctx.lineTo(this.particles[i].x, this.particles[i].y);
                    this.ctx.stroke();
                }
            }
        }
    }

    drawParticles() {
        this.particles.forEach(particle => {
            // Draw particle trail for special particles
            if (particle.type === 'special' && particle.trail.length > 1) {
                for (let i = 1; i < particle.trail.length; i++) {
                    const trailOpacity = (i / particle.trail.length) * particle.trail[i].opacity;
                    this.ctx.save();
                    this.ctx.globalAlpha = trailOpacity;
                    this.ctx.fillStyle = particle.color;
                    this.ctx.beginPath();
                    this.ctx.arc(particle.trail[i].x, particle.trail[i].y, particle.radius * 0.3, 0, Math.PI * 2);
                    this.ctx.fill();
                    this.ctx.restore();
                }
            }

            // Draw main particle
            this.ctx.save();
            this.ctx.globalAlpha = particle.opacity;

            // Enhanced glow effect
            const glowRadius = particle.type === 'special' ? 15 : 8;
            this.ctx.shadowColor = particle.color.split(',')[0] + ',' + particle.color.split(',')[1] + ',' + particle.color.split(',')[2] + ', 1)';
            this.ctx.shadowBlur = glowRadius;
            this.ctx.fillStyle = particle.color;

            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2);
            this.ctx.fill();

            // Inner bright core
            this.ctx.shadowBlur = 0;
            this.ctx.globalAlpha = particle.opacity * 1.2;
            this.ctx.fillStyle = particle.type === 'special' ? 'rgba(255, 255, 255, 0.9)' : 'rgba(255, 255, 255, 0.6)';
            this.ctx.beginPath();
            this.ctx.arc(particle.x, particle.y, particle.radius * 0.4, 0, Math.PI * 2);
            this.ctx.fill();

            this.ctx.restore();
        });
    }

    drawSpecialEffects() {
        // Draw mouse glow effect
        if (this.mouse.isActive) {
            this.ctx.save();
            this.ctx.globalAlpha = 0.1;
            const gradient = this.ctx.createRadialGradient(
                this.mouse.x, this.mouse.y, 0,
                this.mouse.x, this.mouse.y, this.config.mouseRadius
            );
            gradient.addColorStop(0, 'rgba(168, 85, 247, 0.8)');
            gradient.addColorStop(1, 'rgba(168, 85, 247, 0)');

            this.ctx.fillStyle = gradient;
            this.ctx.beginPath();
            this.ctx.arc(this.mouse.x, this.mouse.y, this.config.mouseRadius, 0, Math.PI * 2);
            this.ctx.fill();
            this.ctx.restore();
        }
    }

    createParticleBurst(x, y) {
        const burstCount = 8;
        for (let i = 0; i < burstCount; i++) {
            const angle = (i / burstCount) * Math.PI * 2;
            const speed = Math.random() * 3 + 2;
            const particle = this.createParticle();

            particle.x = x;
            particle.y = y;
            particle.vx = Math.cos(angle) * speed;
            particle.vy = Math.sin(angle) * speed;
            particle.type = 'special';
            particle.opacity = 1;
            particle.radius *= 1.5;

            this.particles.push(particle);
        }

        // Remove excess particles to maintain performance
        if (this.particles.length > this.config.particleCount * 1.5) {
            this.particles.splice(0, burstCount);
        }
    }

    createCosmicEvents() {
        // Create periodic cosmic events (like solar wind effects)
        setInterval(() => {
            if (this.isDisabled || Math.random() > 0.3) return;

            this.particles.forEach(particle => {
                if (Math.random() < 0.1) { // 10% of particles affected
                    const windForce = (Math.random() - 0.5) * 0.5;
                    particle.vx += windForce;
                    particle.vy += windForce * 0.3;
                }
            });
        }, 5000); // Every 5 seconds
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
        // Clean up event listeners would go here if needed
    }
}

// Initialize the enhanced particle system
document.addEventListener('DOMContentLoaded', () => {
    // Initialize particle system
    if (!window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        window.spaceParticleSystem = new EnhancedSpaceParticleSystem();

        // Add performance monitoring
        let frameCount = 0;
        let lastTime = performance.now();

        const monitorPerformance = () => {
            frameCount++;
            const currentTime = performance.now();

            if (currentTime - lastTime >= 5000) { // Check every 5 seconds
                const fps = frameCount / 5;

                if (fps < 30 && window.spaceParticleSystem) {
                    // Reduce particle count if performance is poor
                    window.spaceParticleSystem.config.particleCount = Math.max(
                        window.spaceParticleSystem.config.particleCount * 0.8,
                        15
                    );
                    window.spaceParticleSystem.createParticles();
                }

                frameCount = 0;
                lastTime = currentTime;
            }

            if (window.spaceParticleSystem && !window.spaceParticleSystem.isDisabled) {
                requestAnimationFrame(monitorPerformance);
            }
        };

        requestAnimationFrame(monitorPerformance);
    }
});

// Handle visibility changes for performance
document.addEventListener('visibilitychange', () => {
    if (window.spaceParticleSystem) {
        if (document.hidden) {
            window.spaceParticleSystem.pause();
        } else {
            window.spaceParticleSystem.resume();
        }
    }
});

// Export for potential external use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = EnhancedSpaceParticleSystem;
}