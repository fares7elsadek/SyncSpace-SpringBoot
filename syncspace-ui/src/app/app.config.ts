import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection,APP_INITIALIZER, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS,provideHttpClient , withInterceptorsFromDi} from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { KeycloakService } from 'keycloak-angular';
import { provideToastr } from 'ngx-toastr';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { KeycloakAngularModule } from 'keycloak-angular';
import { environment } from './environments/environment';

export function initKeycloak(keycloak: KeycloakService) {
  return () => { 
    console.log('Starting Keycloak initialization...');
    return keycloak.init({
      config: environment.keycloak,
      initOptions: {
        onLoad: 'login-required', 
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256',
      },
      loadUserProfileAtStartUp: false
    }).then(() => {
      console.log('Keycloak initialized successfully');
    }).catch((error) => {
      console.error('Keycloak initialization failed:', error);
      throw error; 
    });
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimationsAsync(),
    provideRouter(routes), 
    importProvidersFrom(KeycloakAngularModule), 
    provideToastr({
      positionClass: 'toast-bottom-right',  
      timeOut: 4000,
      extendedTimeOut: 1000,
      closeButton: false, 
      progressBar: false, 
      newestOnTop: true,
      preventDuplicates: true,
      maxOpened: 5,
      autoDismiss: false,
      easeTime: 250,
      enableHtml: true, 
      tapToDismiss: true,
      toastClass: 'toast discord-toast',
      titleClass: 'toast-title discord-title',
      messageClass: 'toast-message discord-message',
      iconClasses: {
        error: 'toast-error discord-error',
        info: 'toast-info discord-info', 
        success: 'toast-success discord-success',
        warning: 'toast-warning discord-warning',
      },
    }),
    KeycloakService,
    {
      provide: APP_INITIALIZER,
      useFactory: initKeycloak,
      deps: [KeycloakService],
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
};