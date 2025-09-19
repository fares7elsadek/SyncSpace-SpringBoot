// SyncSpace Starfield Background
(function() {
    'use strict';

    const CONFIG = {
        STAR_COUNT: 200,
        ANIMATION_SPEED: 0.5,
        TWINKLE_SPEED: 2,
        PARALLAX_LAYERS: 3,
        REDUCED_MOTION: window.matchMedia('(prefers-reduced-motion: reduce)').matches
    };

    class Starfield {
        constructor() {
            this.container = document.getElementById('starfield');
            this.stars = [];
            this.animationId = null;
            this.mouseX = 0;
            this.mouseY = 0;

            if (!this.container || CONFIG.REDUCED_MOTION) return;

            this.init();
            this.bindEvents();
            this.animate();
        }

        init() {
            this.createStars();
        }

        createStars() {
            for (let i = 0; i < CONFIG.STAR_COUNT; i++) {
                const star = this.createStar();
                this.stars.push(star);
                this.container.appendChild(star.element);
            }
        }

        createStar() {
            const element = document.createElement('div');
            const layer = Math.floor(Math.random() * CONFIG.PARALLAX_LAYERS) + 1;
            const size = Math.random() * 3 + 1;
            const opacity = Math.random() * 0.8 + 0.2;

            element.className = `star star-layer-${layer}`;
            element.style.cssText = `
                position: absolute;
                width: ${size}px;
                height: ${size}px;
                background: radial-gradient(circle, rgba(124, 58, 237, ${opacity}) 0%, transparent 70%);
                border-radius: 50%;
                pointer-events: none;
                left: ${Math.random() * 100}%;
                top: ${Math.random() * 100}%;
                animation: twinkle ${2 + Math.random() * 4}s ease-in-out infinite alternate;
            `;

            return {
                element,
                x: Math.random() * window.innerWidth,
                y: Math.random() * window.innerHeight,
                originalX: Math.random() * window.innerWidth,
                originalY: Math.random() * window.innerHeight,
                layer,
                twinkleSpeed: Math.random() * CONFIG.TWINKLE_SPEED + 0.5
            };
        }

        bindEvents() {
            window.addEventListener('mousemove', (e) => {
                this.mouseX = (e.clientX / window.innerWidth - 0.5) * 2;
                this.mouseY = (e.clientY / window.innerHeight - 0.5) * 2;
            });

            window.addEventListener('resize', () => {
                this.handleResize();
            });
        }

        handleResize() {
            this.stars.forEach(star => {
                star.originalX = Math.random() * window.innerWidth;
                star.originalY = Math.random() * window.innerHeight;
            });
        }

        animate() {
            if (CONFIG.REDUCED_MOTION) return;

            this.stars.forEach(star => {
                const parallaxFactor = star.layer * 10;
                const offsetX = this.mouseX * parallaxFactor;
                const offsetY = this.mouseY * parallaxFactor;

                star.element.style.transform = `translate(${offsetX}px, ${offsetY}px)`;
            });

            this.animationId = requestAnimationFrame(() => this.animate());
        }

        destroy() {
            if (this.animationId) {
                cancelAnimationFrame(this.animationId);
            }
            this.stars.forEach(star => {
                if (star.element.parentNode) {
                    star.element.parentNode.removeChild(star.element);
                }
            });
        }
    }

    // CSS for star animations
    const style = document.createElement('style');
    style.textContent = `
        @keyframes twinkle {
            0%, 100% { opacity: 0.3; transform: scale(1); }
            50% { opacity: 1; transform: scale(1.2); }
        }
        
        .star-layer-1 { z-index: 1; }
        .star-layer-2 { z-index: 2; }
        .star-layer-3 { z-index: 3; }
    `;
    document.head.appendChild(style);

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => new Starfield());
    } else {
        new Starfield();
    }

    // Cleanup on page unload
    window.addEventListener('beforeunload', () => {
        if (window.starfieldInstance) {
            window.starfieldInstance.destroy();
        }
    });
})();