<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        <div class="error-icon-container">
            <div class="error-icon">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
                    <path d="M12 8v4M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
            </div>
            <div class="error-pulse"></div>
        </div>
        <h2 class="auth-title error-title">Something went wrong</h2>
        <p class="auth-subtitle error-subtitle">We encountered an unexpected error</p>
    <#elseif section = "form">
        <div class="error-content">
            <div class="alert alert-error error-details">
                <div class="alert-icon">
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"/>
                    </svg>
                </div>
                <div class="error-message">
                    <h4>Error Details</h4>
                    <p>${kcSanitize(message.summary)?no_esc}</p>
                    <#if message.details??>
                        <details class="error-technical">
                            <summary>Technical Information</summary>
                            <pre><code>${kcSanitize(message.details)?no_esc}</code></pre>
                        </details>
                    </#if>
                </div>
            </div>

            <div class="error-suggestions">
                <h4>What you can try:</h4>
                <ul class="suggestions-list">
                    <li>
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path d="M8 1a7 7 0 104.95 11.95l.707-.707A8 8 0 118 0v1z"/>
                            <path d="M7.5.5A.5.5 0 018 1v6H1.5a.5.5 0 010-1H7V.5z"/>
                        </svg>
                        Refresh the page and try again
                    </li>
                    <li>
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path d="M8 2a.5.5 0 01.5.5v5h5a.5.5 0 010 1h-5v5a.5.5 0 01-1 0v-5h-5a.5.5 0 010-1h5v-5A.5.5 0 018 2z"/>
                        </svg>
                        Clear your browser cache and cookies
                    </li>
                    <li>
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path d="M8.515 1.019A7 7 0 008 1V0a8 8 0 01.589.022l-.074.997zm2.004.45a7.003 7.003 0 00-.985-.299l.219-.976c.383.086.76.2 1.126.342l-.36.933zm1.37.71a7.01 7.01 0 00-.439-.27l.493-.87a8.025 8.025 0 01.979.654l-.615.789a6.996 6.996 0 00-.418-.302zm1.834 1.79a6.99 6.99 0 00-.653-.796l.724-.69c.27.285.52.59.747.91l-.818.576zm.744 1.352a7.08 7.08 0 00-.214-.468l.893-.45a7.976 7.976 0 01.45 1.088l-.95.313a7.023 7.023 0 00-.179-.483zm.53 2.507a6.991 6.991 0 00-.1-1.025l.985-.17c.067.386.106.778.116 1.17l-1 .025zm-.131 1.538c.033-.17.06-.339.081-.51l.993.123a7.957 7.957 0 01-.23 1.155l-.844-.768zm-.7 3.472a7.024 7.024 0 01-.415.598l.748.66a8.06 8.06 0 00.636-1.052l-.969-.206z"/>
                            <path d="M7.002 11a1 1 0 112 0 1 1 0 01-2 0zM7.1 4.995a.905.905 0 111.8 0l-.35 3.507a.552.552 0 01-1.1 0L7.1 4.995z"/>
                        </svg>
                        Check if the service is currently available
                    </li>
                    <li>
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path d="M0 4a2 2 0 012-2h12a2 2 0 012 2v8a2 2 0 01-2 2H2a2 2 0 01-2-2V4zm2-1a1 1 0 00-1 1v.217l7 4.2 7-4.2V4a1 1 0 00-1-1H2zm13 2.383l-4.758 2.855L15 11.114v-5.73zm-.034 6.878L9.271 8.82 8 9.583 6.728 8.82l-5.694 3.44A1 1 0 002 13h12a1 1 0 00.966-.739zM1 11.114l4.758-2.876L1 5.383v5.73z"/>
                        </svg>
                        Contact support if the problem persists
                    </li>
                </ul>
            </div>

            <div class="error-actions">
                <#if skipLink??>
                    <a href="${skipLink}" class="btn btn-primary">
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path fill-rule="evenodd" d="M1 8a.5.5 0 01.5-.5h11.793l-3.147-3.146a.5.5 0 01.708-.708l4 4a.5.5 0 010 .708l-4 4a.5.5 0 01-.708-.708L12.293 8.5H1.5A.5.5 0 011 8z"/>
                        </svg>
                        Continue
                    </a>
                <#else>
                    <a href="${url.loginUrl}" class="btn btn-primary">
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                            <path fill-rule="evenodd" d="M15 8a.5.5 0 00-.5-.5H2.707l3.147-3.146a.5.5 0 10-.708-.708l-4 4a.5.5 0 000 .708l4 4a.5.5 0 00.708-.708L2.707 8.5H14.5A.5.5 0 0015 8z"/>
                        </svg>
                        Back to Sign In
                    </a>
                </#if>

                <button onclick="window.location.reload()" class="btn btn-secondary">
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                        <path fill-rule="evenodd" d="M8 3a5 5 0 104.546 2.914.5.5 0 00-.908-.417A4 4 0 118 4v1z"/>
                        <path d="M8 0a.5.5 0 01.5.5v2a.5.5 0 01-1 0v-2A.5.5 0 018 0z"/>
                        <path d="M7.5.5A.5.5 0 018 0l2.5 2.5a.5.5 0 01-.708.708L8 1.414 6.207 3.207a.5.5 0 01-.708-.708L7.5.5z"/>
                    </svg>
                    Try Again
                </button>
            </div>

            <div class="error-help">
                <div class="help-card">
                    <h5>Need immediate help?</h5>
                    <p>If you're experiencing technical difficulties or need urgent assistance, our support team is here to help.</p>
                    <div class="help-actions">
                        <a href="mailto:support@syncspace.com" class="help-link">
                            <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                                <path d="M0 4a2 2 0 012-2h12a2 2 0 012 2v8a2 2 0 01-2 2H2a2 2 0 01-2-2V4zm2-1a1 1 0 00-1 1v.217l7 4.2 7-4.2V4a1 1 0 00-1-1H2zm13 2.383l-4.758 2.855L15 11.114v-5.73zm-.034 6.878L9.271 8.82 8 9.583 6.728 8.82l-5.694 3.44A1 1 0 002 13h12a1 1 0 00.966-.739zM1 11.114l4.758-2.876L1 5.383v5.73z"/>
                            </svg>
                            Email Support
                        </a>
                        <a href="#" class="help-link">
                            <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                                <path d="M8 15A7 7 0 118 1a7 7 0 010 14zm0 1A8 8 0 108 0a8 8 0 000 16z"/>
                                <path d="m8.93 6.588-2.29.287-.082.38.45.083c.294.07.352.176.288.469l-.738 3.468c-.194.897.105 1.319.808 1.319.545 0 1.178-.252 1.465-.598l.088-.416c-.2.176-.492.246-.686.246-.275 0-.375-.193-.304-.533L8.93 6.588zM9 4.5a1 1 0 11-2 0 1 1 0 012 0z"/>
                            </svg>
                            Help Center
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <style>
            .error-icon-container {
                position: relative;
                display: flex;
                justify-content: center;
                margin-bottom: var(--space-lg);
            }

            .error-icon {
                position: relative;
                z-index: 2;
                color: var(--color-error);
                animation: error-bounce 2s ease-in-out infinite;
            }

            .error-pulse {
                position: absolute;
                inset: -10px;
                border: 2px solid var(--color-error);
                border-radius: 50%;
                opacity: 0;
                animation: error-pulse 2s ease-out infinite;
            }

            @keyframes error-bounce {
                0%, 100% { transform: translateY(0); }
                50% { transform: translateY(-8px); }
            }

            @keyframes error-pulse {
                0% {
                    transform: scale(0.8);
                    opacity: 1;
                }
                100% {
                    transform: scale(1.4);
                    opacity: 0;
                }
            }

            .error-title {
                color: var(--color-error);
                text-align: center;
            }

            .error-subtitle {
                text-align: center;
                margin-bottom: var(--space-xl);
            }

            .error-content {
                display: flex;
                flex-direction: column;
                gap: var(--space-xl);
            }

            .error-details {
                animation-delay: 0.1s;
            }

            .error-message h4 {
                margin: 0 0 var(--space-sm);
                color: var(--color-error);
                font-size: 0.9rem;
                font-weight: 600;
            }

            .error-message p {
                margin: 0 0 var(--space-sm);
                line-height: 1.5;
            }

            .error-technical {
                margin-top: var(--space-md);
            }

            .error-technical summary {
                cursor: pointer;
                font-size: 0.8rem;
                color: var(--color-text-muted);
                font-weight: 500;
            }

            .error-technical pre {
                margin: var(--space-sm) 0 0;
                padding: var(--space-sm);
                background: var(--color-bg-tertiary);
                border-radius: var(--radius-md);
                font-size: 0.75rem;
                overflow-x: auto;
            }

            .error-suggestions {
                background: rgba(88, 166, 255, 0.05);
                border: 1px solid rgba(88, 166, 255, 0.1);
                border-radius: var(--radius-lg);
                padding: var(--space-lg);
                animation: slide-up 0.5s ease-out 0.2s both;
            }

            @keyframes slide-up {
                from {
                    opacity: 0;
                    transform: translateY(20px);
                }
                to {
                    opacity: 1;
                    transform: translateY(0);
                }
            }

            .error-suggestions h4 {
                margin: 0 0 var(--space-md);
                color: var(--color-info);
                font-size: 0.95rem;
                font-weight: 600;
            }

            .suggestions-list {
                list-style: none;
                margin: 0;
                padding: 0;
                display: flex;
                flex-direction: column;
                gap: var(--space-sm);
            }

            .suggestions-list li {
                display: flex;
                align-items: center;
                gap: var(--space-sm);
                font-size: 0.85rem;
                color: var(--color-text-secondary);
            }

            .suggestions-list svg {
                flex-shrink: 0;
                color: var(--color-info);
            }

            .error-actions {
                display: flex;
                gap: var(--space-md);
                justify-content: center;
                flex-wrap: wrap;
                animation: slide-up 0.5s ease-out 0.3s both;
            }

            .btn-secondary {
                color: var(--color-text-primary);
                background: var(--color-bg-tertiary);
                border: 1.5px solid var(--color-border-primary);
            }

            .btn-secondary:hover {
                background: var(--color-bg-secondary);
                border-color: var(--color-border-secondary);
                transform: translateY(-2px);
                box-shadow: var(--shadow-md);
            }

            .error-help {
                animation: slide-up 0.5s ease-out 0.4s both;
            }

            .help-card {
                background: rgba(124, 58, 237, 0.05);
                border: 1px solid rgba(124, 58, 237, 0.1);
                border-radius: var(--radius-lg);
                padding: var(--space-lg);
                text-align: center;
            }

            .help-card h5 {
                margin: 0 0 var(--space-sm);
                color: var(--color-brand-primary);
                font-size: 1rem;
                font-weight: 600;
            }

            .help-card p {
                margin: 0 0 var(--space-lg);
                color: var(--color-text-secondary);
                font-size: 0.85rem;
                line-height: 1.5;
            }

            .help-actions {
                display: flex;
                gap: var(--space-md);
                justify-content: center;
                flex-wrap: wrap;
            }

            .help-link {
                display: flex;
                align-items: center;
                gap: var(--space-xs);
                padding: var(--space-sm) var(--space-md);
                color: var(--color-brand-primary);
                text-decoration: none;
                font-size: 0.8rem;
                font-weight: 500;
                border-radius: var(--radius-md);
                transition: all var(--transition-fast);
                border: 1px solid transparent;
            }

            .help-link:hover {
                background: rgba(124, 58, 237, 0.1);
                border-color: rgba(124, 58, 237, 0.2);
                transform: translateY(-1px);
            }

            @media (max-width: 640px) {
                .error-actions {
                    flex-direction: column;
                }

                .help-actions {
                    flex-direction: column;
                }

                .suggestions-list li {
                    font-size: 0.8rem;
                }
            }
        </style>
    </#if>
</@layout.registrationLayout>